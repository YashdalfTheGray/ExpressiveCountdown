package com.yashdalfthegray.expressivecountdown

import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object WidgetPreferencesKeys {
    val TARGET_DATE = stringPreferencesKey("target_date")
    val TITLE = stringPreferencesKey("title")
    val COLOR_MODE = stringPreferencesKey("color_mode")
    val CUSTOM_THEME = stringPreferencesKey("custom_theme")
    val IMAGE_URL = stringPreferencesKey("image_url")
    val LAST_REFRESH = longPreferencesKey("last_refresh")
}

enum class ColorMode {
    System,
    Custom,
    Photo
}