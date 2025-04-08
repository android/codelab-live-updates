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
import android.app.Notification.ProgressStyle
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_DEFAULT
import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.Icon
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import androidx.annotation.ColorInt
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.drawable.IconCompat
import com.example.jetsnack.R
import java.util.logging.Level
import java.util.logging.Logger

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
        notificationManager?.createNotificationChannel(channel)
    }

    private enum class OrderState(val delay: Long) {
        INITIALIZING(5000) {
            override fun buildNotification(): Notification.Builder {
                return buildBaseNotification(appContext)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle("You order is being placed")
                    .setContentText("Confirming with bakery...")
                    .setStyle(buildBaseProgressStyle().setProgressIndeterminate(true))
            }
        },
        FOOD_PREPARATION(12000) {
            override fun buildNotification(): Notification.Builder {
                return buildBaseNotification(appContext)
                    .setContentTitle("Your order is being prepared")
                    .setContentText("Next step will be delivery")
                    .setLargeIcon(
                        IconCompat.createWithResource(
                            appContext, R.drawable.ic_launcher_foreground).toIcon(appContext))
                    .setStyle(buildBaseProgressStyle()
                        .setProgress(25))
            }
        },
        FOOD_ENROUTE(18000) {
            override fun buildNotification(): Notification.Builder {
                return buildBaseNotification(appContext)
                    .setContentTitle("Your order is on its way")
                    .setContentText("Enroute to destination")
                    .setStyle(buildBaseProgressStyle()
                        .setProgressTrackerIcon(IconCompat.createWithResource(
                            appContext, R.drawable.delivery_car).toIcon(appContext))
                        .setProgress(50)
                    )
                    .setLargeIcon(
                        IconCompat.createWithResource(
                            appContext, R.drawable.delivery_car).toIcon(appContext))
            }
        },
        FOOD_ARRIVING(25000) {
            override fun buildNotification(): Notification.Builder {
                return buildBaseNotification(appContext)
                    .setContentTitle("Your food is arriving and has been dropped off")
                    .setContentText("Enjoy & don't forget to refrigerate any perishable items.")
                    .setStyle(buildBaseProgressStyle()
                        .setProgressTrackerIcon(IconCompat.createWithResource(
                            appContext, R.drawable.delivery_car).toIcon(appContext))
                        .setProgress(75)
                    )
                    .setLargeIcon(
                        IconCompat.createWithResource(
                            appContext, R.drawable.delivery_car).toIcon(appContext))
            }
        },
        ORDER_COMPLETE(30000) {
            override fun buildNotification(): Notification.Builder {
                return buildBaseNotification(appContext)
                    .setContentTitle("Your order is complete.")
                    .setContentText("Thank you for using JetSnack for your snacking needs.")
                    .setStyle(buildBaseProgressStyle()
                        .setProgressTrackerIcon(IconCompat.createWithResource(
                            appContext, R.drawable.ice_cream_sandwich).toIcon(appContext))
                        .setProgress(100)
                    )
                    .setLargeIcon(IconCompat.createWithResource(
                        appContext, R.drawable.ice_cream_sandwich).toIcon(appContext))
            }
        };

        fun buildBaseNotification(appContext: Context): Notification.Builder {
            return Notification.Builder(appContext, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setColorized(true)
                .setColor(Color.LightGray.toArgb())
                .setOngoing(true)
                .setShowWhen(true)
        }

        fun buildBaseProgressStyle(): ProgressStyle {
            return ProgressStyle()
                .setProgressTrackerIcon(
                    Icon.createWithResource(appContext, R.drawable.ic_launcher_foreground))
                .setProgressPoints(listOf(
                    ProgressStyle.Point(25),
                    ProgressStyle.Point(50),
                    ProgressStyle.Point(75),
                    ProgressStyle.Point(100)
                    ))
        }

        fun CharSequence.foregroundColor(@ColorInt foregroundColor: Int): CharSequence {
            val spannableString = SpannableString(this)
            spannableString.setSpan(
                ForegroundColorSpan(foregroundColor),
                0,
                spannableString.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE,
            )
            spannableString.setSpan(
                StyleSpan(Typeface.BOLD),
                0,
                spannableString.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            return spannableString
        }
        abstract fun buildNotification(): Notification.Builder
    }

    @RequiresApi(36)
    fun start() {
        if (Build.VERSION.SDK_INT >= 35) {
            Logger.getLogger("Logger").info("Called")
            for (state in OrderState.entries) {
                val notif = state.buildNotification().build()
                Handler(Looper.getMainLooper()).postDelayed({
                    notificationManager.notify(NOTIFICATION_ID, notif)
                    Logger.getLogger("canPostPromotedNotifications")
                        .log(
                            Level.INFO,
                            notificationManager.canPostPromotedNotifications().toString())
                    Logger.getLogger("hasPromotableCharacteristics")
                        .log(
                            Level.INFO,
                            notif.hasPromotableCharacteristics().toString())

                }, state.delay)
            }

        }

    }
}