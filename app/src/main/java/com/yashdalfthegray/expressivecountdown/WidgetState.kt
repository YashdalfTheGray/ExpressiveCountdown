package com.yashdalfthegray.expressivecountdown

import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object WidgetPreferencesKeys {
    val TARGET_DATE = stringPreferencesKey("target_date")
    val TITLE = stringPreferencesKey("title")
    val COLOR_MODE = stringPreferencesKey("color_mode")
    val CUSTOM_COLOR = intPreferencesKey("custom_color")
    val IMAGE_URL = stringPreferencesKey("image_url")
    val LAST_REFRESH = longPreferencesKey("last_refresh")
}