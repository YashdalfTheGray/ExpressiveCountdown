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
                val target = targetString?.let { LocalDate.parse(it) }

                val daysLeftStr: String = if (target == null) {
                    context.getString(R.string.config_pick_date)
                } else {
                    val days = daysLeft(java.time.Clock.systemDefaultZone(), target)
                    context.resources.getQuantityString(R.plurals.days_left, days.toInt(), days)
                }

                Log.d("ExpressiveCountdownWidget", "daysLeftStr: $daysLeftStr")

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
        val titleFontSize = if (isSmall(size)) 16.sp else if (isMedium(size)) 20.sp else 24.sp
        val countdownFontSize = if (isSmall(size)) 24.sp else if (isMedium(size)) 32.sp else 40.sp

        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(GlanceTheme.colors.widgetBackground)
                .padding(8.dp),
        ) {
            if (title.isNotBlank()) {
                Row(
                    modifier = GlanceModifier.fillMaxWidth()
                ) {
                    Text(
                        text = title,
                        style = TextStyle(
                            fontSize = titleFontSize,
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
                    text = daysLeftStr,
                    style = TextStyle(
                        fontSize = countdownFontSize,
                        color = GlanceTheme.colors.primary,
                    )
                )
            }
        }
    }

    private fun isSmall(size: DpSize): Boolean {
        return size.width < 120.dp || size.height < 80.dp
    }

    private fun isMedium(size: DpSize): Boolean {
        return size.width < 160.dp || size.height < 120.dp
    }
}