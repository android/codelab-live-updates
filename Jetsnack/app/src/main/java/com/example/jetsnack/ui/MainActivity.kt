/*
 * Copyright 2020 The Android Open Source Project
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

package com.example.jetsnack.ui

import android.appwidget.AppWidgetManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.lifecycle.lifecycleScope
import com.example.jetsnack.widget.RecentOrdersWidgetReceiver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val TAG = "MainActivity"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val cartItemsFromWidget = intent.getStringExtra("CART_ITEMS_KEY")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            lifecycleScope.launch(Dispatchers.Default) {
                setWidgetPreviews()
            }
        }

        setContent { JetsnackApp(cartItemsFromWidget) }
    }

    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    suspend fun setWidgetPreviews() {
        val receiver = RecentOrdersWidgetReceiver::class
        val installedProviders = getSystemService(AppWidgetManager::class.java).installedProviders
        val glanceAppWidgetManager = GlanceAppWidgetManager(this)
        val providerInfo = installedProviders.firstOrNull { it.provider.className == receiver.qualifiedName }
        providerInfo?.generatedPreviewCategories.takeIf { it == 0 }?.let {
            // Set previews if this provider if unset
            val success = glanceAppWidgetManager.setWidgetPreviews(receiver)
            val logMessage = if (success) {
                "set previews for ${receiver.simpleName}"
            } else {
                "failed to set previews for ${receiver.simpleName}, rate-limited"
            }
            Log.i(TAG, logMessage)
        } ?: run {
            Log.i(TAG, "${receiver.simpleName} already has previews set")
        }
    }
}
