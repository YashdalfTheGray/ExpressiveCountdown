package com.yashdalfthegray.expressivecountdown

import android.content.Context
import android.util.Log
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback

class RefreshWidgetAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        Log.d("ExpressiveCountdownWidget", "Refresh widget action triggered")
        ExpressiveCountdownWidget().update(context, glanceId)
        Log.d("ExpressiveCountdownWidget", "Widget refreshed")
    }
}