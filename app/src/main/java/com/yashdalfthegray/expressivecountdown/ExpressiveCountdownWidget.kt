package com.yashdalfthegray.expressivecountdown

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.glance.layout.Box
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.layout.fillMaxSize

class ExpressiveCountdownWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            WidgetContent()
        }
    }

    @Composable
    private fun WidgetContent() {
        GlanceTheme {
            Box(modifier = GlanceModifier.fillMaxSize()) {
                Text(
                    text = "?? Days",
                    style = TextStyle(
                        fontSize = 28.sp,
                        color = GlanceTheme.colors.onSurface
                    )
                )
            }
        }
    }
}