package com.yashdalfthegray.expressivecountdown

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import java.time.ZonedDateTime

object MidnightUpdateScheduler {
    private const val REQUEST_CODE = 1001
    const val ACTION_MIDNIGHT_UPDATE = "com.yashdalfthegray.expressivecountdown.ACTION_UPDATE_MIDNIGHT"

    fun scheduleMidnightUpdate(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ExpressiveCountdownWidgetReceiver::class.java).apply {
            action = ACTION_MIDNIGHT_UPDATE
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val now = ZonedDateTime.now()
        val nextMidnight = now.toLocalDate()
            .plusDays(1)
            .atStartOfDay(now.zone)
            .plusSeconds(1)

        val triggerMillis = nextMidnight.toInstant().toEpochMilli()

        alarmManager.set(AlarmManager.RTC, triggerMillis, pendingIntent)

        Log.d(
            "ExpressiveCountdownMidnightUpdateScheduler",
            "Midnight update scheduled for $nextMidnight"
        )
    }

    fun cancelMidnightUpdate(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, ExpressiveCountdownWidgetReceiver::class.java).apply {
            action = ACTION_MIDNIGHT_UPDATE
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_NO_CREATE
        )

        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
            Log.d("ExpressiveCountdownMidnightUpdateScheduler", "Midnight update canceled")
        } else {
            Log.d("ExpressiveCountdownMidnightUpdateScheduler", "No scheduled midnight update found")
        }
    }
}