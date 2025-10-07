package com.yashdalfthegray.expressivecountdown

import androidx.compose.runtime.saveable.Saver
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

/**
 * Saver for Color to make it compatible with rememberSaveable.
 *
 * Converts Color to/from Int (ARGB) for persistence across configuration changes.
 *
 * Usage:
 * ```
 * var color by rememberSaveable(stateSaver = ColorSaver) {
 *     mutableStateOf(Color.Red)
 * }
 * ```
 */
val ColorSaver = Saver<Color, Int>(
    save = { color -> color.toArgb() },
    restore = { argb -> Color(argb) }
)