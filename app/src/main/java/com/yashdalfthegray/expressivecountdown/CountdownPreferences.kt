package com.yashdalfthegray.expressivecountdown

import android.content.Context
import java.time.LocalDate
import androidx.core.content.edit

object CountdownPreferences {
    private const val PREFS_NAME = "com.yashdalfthegray.expressivecountdown.CountdownPrefs"
    private const val KEY_PREFIX_DATE = "target_date_"

    fun saveTargetDate(context: Context, appWidgetId: Int, date: LocalDate) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit {
            putString(KEY_PREFIX_DATE + appWidgetId, date.toString()).apply()
        }
    }

    fun loadTargetDate(context: Context, appWidgetId: Int): LocalDate? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val dateString = prefs.getString(KEY_PREFIX_DATE + appWidgetId, null)
        return dateString?.let { LocalDate.parse(it) }
    }

    fun deleteTargetDate(context: Context, appWidgetId: Int) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit {
            remove(KEY_PREFIX_DATE + appWidgetId).apply()
        }
    }
}