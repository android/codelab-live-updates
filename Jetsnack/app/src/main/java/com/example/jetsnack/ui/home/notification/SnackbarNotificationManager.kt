/*
 * Copyright 2025 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.jetsnack.ui.home.notification

import android.app.Notification
import android.app.NotificationChannel
//import android.app.Notification.ProgressStyle
import android.os.Build
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_DEFAULT
import android.content.Context
import androidx.compose.ui.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.drawable.IconCompat
import com.example.jetsnack.R

object SnackbarNotificationManager {
    private lateinit var notificationManager: NotificationManager
    private lateinit var appContext: Context
    const val CHANNEL_ID = "live_updates_channel_id"
    private const val CHANNEL_NAME = "live_updates_channel_name"
    private const val NOTIFICATION_ID = 1234


    fun initialize(context: Context, notifManager: NotificationManager) {
        notificationManager = notifManager
        val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, IMPORTANCE_DEFAULT)
        appContext = context
        notificationManager.createNotificationChannel(channel)
    }

    private enum class OrderState(val delay: Long) {
        INITIALIZING(0) {
            override fun buildNotification(): Notification.Builder {
                return buildBaseNotification(appContext, INITIALIZING)
                    .setContentTitle("You order is being placed")
                    .setContentText("Confirming with bakery...")
            }
        },
        FOOD_PREPARATION(7000) {
            override fun buildNotification(): Notification.Builder {
                return buildBaseNotification(appContext, FOOD_PREPARATION)
                    .setContentTitle("Your order is being prepared")
                    .setContentText("Next step will be delivery")
                    .setLargeIcon(
                        IconCompat.createWithResource(
                            appContext, R.drawable.cupcake).toIcon(appContext))
            }
        },
        FOOD_ENROUTE(13000) {
            override fun buildNotification(): Notification.Builder {
                return buildBaseNotification(appContext, FOOD_ENROUTE)
                    .setContentTitle("Your order is on its way")
                    .setContentText("Enroute to destination")
                    .setLargeIcon(
                        IconCompat.createWithResource(
                            appContext, R.drawable.cupcake).toIcon(appContext))
            }
        },
        FOOD_ARRIVING(18000) {
            override fun buildNotification(): Notification.Builder {
                return buildBaseNotification(appContext, FOOD_ARRIVING)
                    .setContentTitle("Your order is arriving and has been dropped off")
                    .setContentText("Enjoy & don't forget to refrigerate any perishable items.")
                    .setLargeIcon(
                        IconCompat.createWithResource(
                            appContext, R.drawable.cupcake).toIcon(appContext))
            }
        },
        ORDER_COMPLETE(21000) {
            override fun buildNotification(): Notification.Builder {
                return buildBaseNotification(appContext, ORDER_COMPLETE)
                    .setContentTitle("Your order is complete.")
                    .setContentText("Thank you for using JetSnack for your snacking needs.")
                    .setLargeIcon(IconCompat.createWithResource(
                        appContext, R.drawable.cupcake).toIcon(appContext))
            }
        };

        fun buildBaseNotification(appContext: Context, orderState: OrderState): Notification.Builder {
            val notificationBuilder = Notification.Builder(appContext, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setOngoing(true)

            when (orderState) {
                INITIALIZING -> {}
                FOOD_PREPARATION -> {}
                FOOD_ENROUTE -> {}
                FOOD_ARRIVING ->
                    notificationBuilder
                        .addAction(
                            Notification.Action.Builder(null, "Got it", null).build()
                        )
                        .addAction(
                            Notification.Action.Builder(null, "Tip", null).build()
                        )
                ORDER_COMPLETE ->
                    notificationBuilder
                        .addAction(
                            Notification.Action.Builder(
                                null, "Rate delivery", null).build()
                        )
            }
            return notificationBuilder
        }

        abstract fun buildNotification(): Notification.Builder
    }

    fun start() {
        for (state in OrderState.entries) {
            val notif = state.buildNotification().build()
            Handler(Looper.getMainLooper()).postDelayed({
                notificationManager.notify(NOTIFICATION_ID, notif)
            }, state.delay)
        }

    }
}