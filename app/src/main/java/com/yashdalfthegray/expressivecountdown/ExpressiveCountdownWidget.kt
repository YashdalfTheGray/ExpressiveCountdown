package com.yashdalfthegray.expressivecountdown

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.Preferences
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.currentState
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
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
            val title = prefs[WidgetPreferencesKeys.TITLE]
            val target = targetString?.let { LocalDate.parse(it) }

            val label: String = if (target == null) {
                context.getString(R.string.config_pick_date)
            } else {
                val days = daysLeft(java.time.Clock.systemDefaultZone(), target)
                context.resources.getQuantityString(R.plurals.days_left, days.toInt(), days)
            }

            WidgetContent(label, title ?: "")
        }
    }

    @Composable
    private fun WidgetContent(label: String, title: String) {
        GlanceTheme {
            Column(
                modifier = GlanceModifier.fillMaxSize().padding(8.dp),
            ) {
                Row(
                    modifier = GlanceModifier.fillMaxWidth()
                ) {
                    if (title.isNotBlank()) {
                        Text(
                            text = title,
                            style = TextStyle(
                                fontSize = 28.sp,
                                color = GlanceTheme.colors.onSurface,
                            )
                        )
                    }
                }

                Spacer(modifier = GlanceModifier.defaultWeight())

                Row(
                    modifier = GlanceModifier.fillMaxWidth()
                ) {
                    Text(
                        text = label,
                        style = TextStyle(
                            fontSize = 28.sp,
                            color = GlanceTheme.colors.onSurface,
                        )
                    )
                }
            }
        }
    }
}
