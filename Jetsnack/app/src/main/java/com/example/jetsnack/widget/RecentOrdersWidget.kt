package com.example.jetsnack.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.rememberCoroutineScope
import androidx.glance.GlanceId
import androidx.glance.GlanceTheme
import androidx.glance.LocalContext
import androidx.glance.LocalSize
import androidx.glance.appwidget.AppWidgetId
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.provideContent
import com.example.jetsnack.R
import com.example.jetsnack.widget.data.FakeImageTextListDataRepository
import com.example.jetsnack.widget.data.FakeImageTextListDataRepository.Companion.getImageTextListDataRepo
import com.example.jetsnack.widget.layout.ImageTextListItemData
import com.example.jetsnack.widget.layout.ImageTextListLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RecentOrdersWidget : GlanceAppWidget() {
    // Unlike the "Single" size mode, using "Exact" allows us to have better control over rendering in
    // different sizes. And, unlike the "Responsive" mode, it doesn't cause several views for each
    // supported size to be held in the widget host's memory.
    override val sizeMode: SizeMode = SizeMode.Exact

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val repo = getImageTextListDataRepo(id)

        val initialItems = withContext(Dispatchers.Default) {
            repo.load()
        }

        provideContent {
            GlanceTheme {
                val items by repo.data().collectAsState(initial = initialItems)
                val coroutineScope = rememberCoroutineScope()

                key(LocalSize.current) {
                    WidgetContent(
                        items = items,
                        refreshAction = {
                            coroutineScope.launch {
                                withContext(Dispatchers.IO) {
                                    repo.refresh()
                                }
                            }
                        }
                    )
                }
            }
        }
    }

    @Composable
    fun WidgetContent(
        items: List<ImageTextListItemData>,
        refreshAction: () -> Unit,
    ) {
        val context = LocalContext.current

        ImageTextListLayout(
            items = items,
            title = context.getString(R.string.widget_title),
            titleIconRes = R.drawable.logo,
            titleBarActionIconRes = R.drawable.refresh,
            titleBarActionIconContentDescription = context.getString(
                R.string.refresh_icon_button_label
            ),
            titleBarAction = refreshAction,
        )
    }
}

class RecentOrdersWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget = RecentOrdersWidget()

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        appWidgetIds.forEach {
            FakeImageTextListDataRepository.cleanUp(AppWidgetId(appWidgetId = it))
        }
        super.onDeleted(context, appWidgetIds)
    }
}