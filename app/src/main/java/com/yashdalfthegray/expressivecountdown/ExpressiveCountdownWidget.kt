package com.yashdalfthegray.expressivecountdown

import android.content.Context
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.Box
import androidx.glance.layout.fillMaxSize
import androidx.glance.text.Text
import androidx.glance.text.TextStyle

class ExpressiveCountdownWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val manager = GlanceAppWidgetManager(context)
        val widgetId = manager.getAppWidgetId(id)
        val target = CountdownPreferences.loadTargetDate(context, widgetId)

        var label = "Set a date"
        if (target != null) {
            val days = daysLeft(java.time.Clock.systemDefaultZone(), target)
            label = if (days == 1L) "1 day" else "$days days"
        }

        provideContent {
            WidgetContent(label)
        }
    }

    @Composable
    private fun WidgetContent(label: String) {
        GlanceTheme {
            Box(modifier = GlanceModifier.fillMaxSize()) {
                Text(
                    text = label,
                    style = TextStyle(
                        fontSize = 28.sp,
                        color = GlanceTheme.colors.onSurface
                    )
                )
            }
        }
    }
}