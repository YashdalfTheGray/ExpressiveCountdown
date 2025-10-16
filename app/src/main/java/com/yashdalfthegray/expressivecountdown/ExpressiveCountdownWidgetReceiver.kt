package com.yashdalfthegray.expressivecountdown

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.appwidget.updateAll
import androidx.glance.state.PreferencesGlanceStateDefinition
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ExpressiveCountdownWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = ExpressiveCountdownWidget()

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        if (intent.action == MidnightUpdateScheduler.ACTION_MIDNIGHT_UPDATE) {
            Log.d("ExpressiveCountdownWidgetReceiver", "Midnight update triggered")
            handleMidnightUpdate(context)
        }
    }

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        MidnightUpdateScheduler.scheduleMidnightUpdate(context)
        Log.d("ExpressiveCountdownWidgetReceiver", "Widget scheduled for midnight refresh")
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        MidnightUpdateScheduler.cancelMidnightUpdate(context)
        Log.d("ExpressiveCountdownWidgetReceiver", "Midnight update canceled")
    }

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

    private fun handleMidnightUpdate(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                ExpressiveCountdownWidget().updateAll(context)
                Log.d("ExpressiveCountdownWidgetReceiver", "Updated all widgets at midnight")

                MidnightUpdateScheduler.scheduleMidnightUpdate(context)
            } catch (e: Exception) {
                Log.e("ExpressiveCountdownWidgetReceiver", "Error during midnight update", e)
            }
        }
    }
}