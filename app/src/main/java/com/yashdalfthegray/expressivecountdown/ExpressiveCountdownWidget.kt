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
        val clock = java.time.Clock.systemDefaultZone()
        val target = java.time.LocalDate.of(2025, 12, 31) // just for testing
        val n = daysLeft(clock, target)
        val label = if (n == 1L) "1 day" else "$n days"


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