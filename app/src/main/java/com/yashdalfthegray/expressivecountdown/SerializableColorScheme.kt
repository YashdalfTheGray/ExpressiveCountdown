package com.yashdalfthegray.expressivecountdown

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.materialkolor.PaletteStyle
import kotlinx.serialization.Serializable
import com.materialkolor.dynamicColorScheme
import com.materialkolor.dynamiccolor.ColorSpec

fun generateThemeFromSeedColor(seedColor: Color): StoredCustomTheme {
    val lightScheme = dynamicColorScheme(
        seedColor = seedColor,
        isDark = false,
        style = PaletteStyle.Expressive,
        specVersion = ColorSpec.SpecVersion.SPEC_2025
    )
    val darkScheme = dynamicColorScheme(
        seedColor = seedColor,
        isDark = true,
        style = PaletteStyle.Expressive,
        specVersion = ColorSpec.SpecVersion.SPEC_2025
    )

    return StoredCustomTheme(
        light = lightScheme.toSerializable(),
        dark = darkScheme.toSerializable()
    )
}

@Serializable
data class SerializableColorScheme(
    val primary: Int,
    val onPrimary: Int,
    val primaryContainer: Int,
    val onPrimaryContainer: Int,
    val inversePrimary: Int,
    val primaryFixed: Int,
    val primaryFixedDim: Int,
    val onPrimaryFixed: Int,
    val onPrimaryFixedVariant: Int,

    val secondary: Int,
    val onSecondary: Int,
    val secondaryContainer: Int,
    val onSecondaryContainer: Int,
    val secondaryFixed: Int,
    val secondaryFixedDim: Int,
    val onSecondaryFixed: Int,
    val onSecondaryFixedVariant: Int,

    val tertiary: Int,
    val onTertiary: Int,
    val tertiaryContainer: Int,
    val onTertiaryContainer: Int,
    val tertiaryFixed: Int,
    val tertiaryFixedDim: Int,
    val onTertiaryFixed: Int,
    val onTertiaryFixedVariant: Int,

    val error: Int,
    val onError: Int,
    val errorContainer: Int,
    val onErrorContainer: Int,

    val background: Int,
    val onBackground: Int,

    val surface: Int,
    val onSurface: Int,
    val surfaceVariant: Int,
    val onSurfaceVariant: Int,
    val surfaceTint: Int,
    val surfaceBright: Int,
    val surfaceDim: Int,
    val surfaceContainer: Int,
    val surfaceContainerHigh: Int,
    val surfaceContainerHighest: Int,
    val surfaceContainerLow: Int,
    val surfaceContainerLowest: Int,

    val inverseSurface: Int,
    val inverseOnSurface: Int,

    val outline: Int,
    val outlineVariant: Int,

    val scrim: Int
)

@Serializable
data class StoredCustomTheme(
    val light: SerializableColorScheme,
    val dark: SerializableColorScheme
)

fun ColorScheme.toSerializable(): SerializableColorScheme {
    return SerializableColorScheme(
        primary = primary.toArgb(),
        onPrimary = onPrimary.toArgb(),
        primaryContainer = primaryContainer.toArgb(),
        onPrimaryContainer = onPrimaryContainer.toArgb(),
        inversePrimary = inversePrimary.toArgb(),
        primaryFixed = primaryFixed.toArgb(),
        primaryFixedDim = primaryFixedDim.toArgb(),
        onPrimaryFixed = onPrimaryFixed.toArgb(),
        onPrimaryFixedVariant = onPrimaryFixedVariant.toArgb(),

        secondary = secondary.toArgb(),
        onSecondary = onSecondary.toArgb(),
        secondaryContainer = secondaryContainer.toArgb(),
        onSecondaryContainer = onSecondaryContainer.toArgb(),
        secondaryFixed = secondaryFixed.toArgb(),
        secondaryFixedDim = secondaryFixedDim.toArgb(),
        onSecondaryFixed = onSecondaryFixed.toArgb(),
        onSecondaryFixedVariant = onSecondaryFixedVariant.toArgb(),

        tertiary = tertiary.toArgb(),
        onTertiary = onTertiary.toArgb(),
        tertiaryContainer = tertiaryContainer.toArgb(),
        onTertiaryContainer = onTertiaryContainer.toArgb(),
        tertiaryFixed = tertiaryFixed.toArgb(),
        tertiaryFixedDim = tertiaryFixedDim.toArgb(),
        onTertiaryFixed = onTertiaryFixed.toArgb(),
        onTertiaryFixedVariant = onTertiaryFixedVariant.toArgb(),

        error = error.toArgb(),
        onError = onError.toArgb(),
        errorContainer = errorContainer.toArgb(),
        onErrorContainer = onErrorContainer.toArgb(),

        background = background.toArgb(),
        onBackground = onBackground.toArgb(),

        surface = surface.toArgb(),
        onSurface = onSurface.toArgb(),
        surfaceVariant = surfaceVariant.toArgb(),
        onSurfaceVariant = onSurfaceVariant.toArgb(),
        surfaceTint = surfaceTint.toArgb(),
        surfaceBright = surfaceBright.toArgb(),
        surfaceDim = surfaceDim.toArgb(),
        surfaceContainer = surfaceContainer.toArgb(),
        surfaceContainerHigh = surfaceContainerHigh.toArgb(),
        surfaceContainerHighest = surfaceContainerHighest.toArgb(),
        surfaceContainerLow = surfaceContainerLow.toArgb(),
        surfaceContainerLowest = surfaceContainerLowest.toArgb(),

        inverseSurface = inverseSurface.toArgb(),
        inverseOnSurface = inverseOnSurface.toArgb(),

        outline = outline.toArgb(),
        outlineVariant = outlineVariant.toArgb(),

        scrim = scrim.toArgb()
    )
}

fun SerializableColorScheme.toColorScheme(): ColorScheme {
    return ColorScheme(
        primary = Color(primary),
        onPrimary = Color(onPrimary),
        primaryContainer = Color(primaryContainer),
        onPrimaryContainer = Color(onPrimaryContainer),
        inversePrimary = Color(inversePrimary),
        primaryFixed = Color(primaryFixed),
        primaryFixedDim = Color(primaryFixedDim),
        onPrimaryFixed = Color(onPrimaryFixed),
        onPrimaryFixedVariant = Color(onPrimaryFixedVariant),

        secondary = Color(secondary),
        onSecondary = Color(onSecondary),
        secondaryContainer = Color(secondaryContainer),
        onSecondaryContainer = Color(onSecondaryContainer),
        secondaryFixed = Color(secondaryFixed),
        secondaryFixedDim = Color(secondaryFixedDim),
        onSecondaryFixed = Color(onSecondaryFixed),
        onSecondaryFixedVariant = Color(onSecondaryFixedVariant),

        tertiary = Color(tertiary),
        onTertiary = Color(onTertiary),
        tertiaryContainer = Color(tertiaryContainer),
        onTertiaryContainer = Color(onTertiaryContainer),
        tertiaryFixed = Color(tertiaryFixed),
        tertiaryFixedDim = Color(tertiaryFixedDim),
        onTertiaryFixed = Color(onTertiaryFixed),
        onTertiaryFixedVariant = Color(onTertiaryFixedVariant),

        error = Color(error),
        onError = Color(onError),
        errorContainer = Color(errorContainer),
        onErrorContainer = Color(onErrorContainer),

        background = Color(background),
        onBackground = Color(onBackground),

        surface = Color(surface),
        onSurface = Color(onSurface),
        surfaceVariant = Color(surfaceVariant),
        onSurfaceVariant = Color(onSurfaceVariant),
        surfaceTint = Color(surfaceTint),
        surfaceBright = Color(surfaceBright),
        surfaceDim = Color(surfaceDim),
        surfaceContainer = Color(surfaceContainer),
        surfaceContainerHigh = Color(surfaceContainerHigh),
        surfaceContainerHighest = Color(surfaceContainerHighest),
        surfaceContainerLow = Color(surfaceContainerLow),
        surfaceContainerLowest = Color(surfaceContainerLowest),

        inverseSurface = Color(inverseSurface),
        inverseOnSurface = Color(inverseOnSurface),

        outline = Color(outline),
        outlineVariant = Color(outlineVariant),

        scrim = Color(scrim)
    )
}