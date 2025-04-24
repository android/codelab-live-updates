package com.example.jetsnack.widget

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.core.net.toUri
import androidx.glance.GlanceId
import androidx.glance.GlanceTheme
import androidx.glance.LocalContext
import androidx.glance.LocalSize
import androidx.glance.appwidget.AppWidgetId
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.provideContent
import com.example.jetsnack.R
import com.example.jetsnack.ui.MainActivity
import com.example.jetsnack.widget.data.FakeImageTextListDataRepository
import com.example.jetsnack.widget.data.FakeImageTextListDataRepository.Companion.getImageTextListDataRepo
import com.example.jetsnack.widget.layout.ImageTextListItemData
import com.example.jetsnack.widget.layout.ImageTextListLayout
import kotlinx.coroutines.Dispatchers
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

                key(LocalSize.current) {
                    WidgetContent(
                        items = items,
                        shoppingCartActionIntent = Intent(context.applicationContext, MainActivity::class.java)
                            .setAction(Intent.ACTION_VIEW)
                            .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                            .setData("https://jetsnack.example.com/home/cart".toUri())
                    )
                }
            }
        }
    }

    @Composable
    fun WidgetContent(
        items: List<ImageTextListItemData>,
        shoppingCartActionIntent: Intent
    ) {
        val context = LocalContext.current

        ImageTextListLayout(
            items = items,
            title = context.getString(R.string.widget_title),
            titleIconRes = R.drawable.logo,
            titleBarActionIconRes = R.drawable.shopping_cart,
            titleBarActionIconContentDescription = context.getString(
                R.string.shopping_cart_button_label
            ),
            titleBarAction = actionStartActivity(shoppingCartActionIntent),
            shoppingCartActionIntent = shoppingCartActionIntent
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