package com.yashdalfthegray.expressivecountdown

import android.content.Context
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.Preferences
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.LocalSize
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.state.GlanceStateDefinition
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import java.time.LocalDate

class ExpressiveCountdownWidget : GlanceAppWidget() {

    override var stateDefinition: GlanceStateDefinition<*> = PreferencesGlanceStateDefinition

    override val sizeMode = SizeMode.Responsive(
        setOf(
            DpSize(80.dp, 40.dp),
            DpSize(120.dp, 80.dp),
            DpSize(160.dp, 120.dp),
        )
    )

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            GlanceTheme {
                val prefs = currentState<Preferences>()
                val targetString = prefs[WidgetPreferencesKeys.TARGET_DATE]
                val title = prefs[WidgetPreferencesKeys.TITLE]
                val colorMode = prefs[WidgetPreferencesKeys.COLOR_MODE]?.let {
                    runCatching { ColorMode.valueOf(it) }.getOrNull()
                } ?: ColorMode.System
                val photoUri = prefs[WidgetPreferencesKeys.IMAGE_URL]

                val target = targetString?.let { LocalDate.parse(it) }

                val daysLeftStr: String = if (target == null) {
                    context.getString(R.string.config_pick_date)
                } else {
                    val days = daysLeft(java.time.Clock.systemDefaultZone(), target)
                    context.resources.getQuantityString(R.plurals.days_left, days.toInt(), days)
                }

                WidgetContent(
                    daysLeftStr = daysLeftStr,
                    title = title ?: "",
                    colorMode = colorMode
                )
            }
        }
    }

    @Composable
    private fun WidgetContent(
        daysLeftStr: String,
        title: String,
        colorMode: ColorMode
    ) {
        val size = LocalSize.current

        val titleFontSize = when {
            size.width < 120.dp -> 12.sp
            size.width < 160.dp -> 18.sp
            else -> 28.sp
        }
        val countdownFontSize = when {
            size.width < 120.dp -> 16.sp
            size.width < 160.dp -> 24.sp
            else -> 36.sp
        }

        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(GlanceTheme.colors.widgetBackground)
                .padding(8.dp),
        ) {
            if (title.isNotBlank()) {
                Text(
                    text = title,
                    style = TextStyle(
                        fontSize = titleFontSize,
                        color = GlanceTheme.colors.onSurface,
                    )
                )
            }

            Spacer(modifier = GlanceModifier.defaultWeight())

            Text(
                text = daysLeftStr,
                style = TextStyle(
                    fontSize = countdownFontSize,
                    color = GlanceTheme.colors.primary,
                )
            )
        }
    }
}