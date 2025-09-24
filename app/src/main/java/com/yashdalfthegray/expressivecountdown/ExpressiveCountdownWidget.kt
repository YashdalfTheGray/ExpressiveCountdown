package com.yashdalfthegray.expressivecountdown

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.Preferences
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.currentState
import androidx.glance.layout.Box
import androidx.glance.layout.fillMaxSize
import androidx.glance.state.GlanceStateDefinition
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import java.time.LocalDate

class ExpressiveCountdownWidget : GlanceAppWidget() {

    override var stateDefinition: GlanceStateDefinition<*> = PreferencesGlanceStateDefinition

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            val prefs = currentState<Preferences>()
            val targetString = prefs[WidgetPreferencesKeys.TARGET_DATE]
            val target = targetString?.let { LocalDate.parse(it) }

            var label = "Set a date"
            if (target != null) {
                val days = daysLeft(java.time.Clock.systemDefaultZone(), target)
                label = if (days == 1L) "1 day" else "$days days"
            }
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