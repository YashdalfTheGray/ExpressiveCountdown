package com.yashdalfthegray.expressivecountdown

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.state.PreferencesGlanceStateDefinition
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ExpressiveCountdownWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = ExpressiveCountdownWidget()

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        super.onDeleted(context, appWidgetIds)

        val appCtx = context.applicationContext
        CoroutineScope(Dispatchers.IO).launch {
            val mgr = GlanceAppWidgetManager(appCtx)
            appWidgetIds.forEach { id ->
                val glanceId = runCatching { mgr.getGlanceIdBy(id) }
                    .getOrNull() ?: return@forEach

                updateAppWidgetState(
                    appCtx,
                    PreferencesGlanceStateDefinition,
                    glanceId
                ) { prefs ->
                    prefs.toMutablePreferences().apply { clear() }
                }
            }
        }
    }
}