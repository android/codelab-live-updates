package com.example.jetsnack.widget.layout

import androidx.compose.runtime.Composable
import androidx.glance.LocalContext
import com.example.jetsnack.R
import com.example.jetsnack.widget.utils.ActionUtils

/**
 * Content to be displayed when there are no items in the list. To be displayed below the
 * app-specific title bar in the [androidx.glance.appwidget.components.Scaffold] .
 */
@Composable
internal fun EmptyListContent() {
  val context = LocalContext.current

  NoDataContent(
    noDataText = context.getString(R.string.sample_no_data_text),
    noDataIconRes = R.drawable.cupcake,
    actionButtonText = context.getString(R.string.sample_add_button_text),
    actionButtonIcon = R.drawable.cupcake,
    actionButtonOnClick = ActionUtils.actionStartDemoActivity("on-click of add item button")
  )
}