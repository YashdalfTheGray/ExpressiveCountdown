package com.yashdalfthegray.expressivecountdown

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.util.Log
import androidx.exifinterface.media.ExifInterface
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.Preferences
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.LocalSize
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.provideContent
import androidx.glance.currentState
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.state.GlanceStateDefinition
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import java.time.LocalDate
import androidx.glance.ImageProvider
import androidx.glance.action.clickable
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.background
import androidx.glance.layout.Box
import androidx.glance.layout.ContentScale
import androidx.glance.material3.ColorProviders
import androidx.glance.text.FontWeight
import kotlinx.serialization.json.Json

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
            val prefs = currentState<Preferences>()
            val targetString = prefs[WidgetPreferencesKeys.TARGET_DATE]
            val title = prefs[WidgetPreferencesKeys.TITLE]
            val colorMode = prefs[WidgetPreferencesKeys.COLOR_MODE]?.let {
                runCatching { ColorMode.valueOf(it) }.getOrNull()
            } ?: ColorMode.System
            val imageUriString = prefs[WidgetPreferencesKeys.IMAGE_URL]

            val customColorProviders = if (colorMode != ColorMode.System) {
                val customThemeJson = prefs[WidgetPreferencesKeys.CUSTOM_THEME]
                if (!customThemeJson.isNullOrEmpty()) {
                    try {
                        val storedTheme = Json.decodeFromString<StoredCustomTheme>(customThemeJson)
                        ColorProviders(
                            light = storedTheme.light.toColorScheme(),
                            dark = storedTheme.dark.toColorScheme()
                        )
                    } catch (e: Exception) {
                        Log.e("ExpressiveCountdownWidget", "Failed to parse custom theme", e)
                        null
                    }
                } else null
            } else null

            val backgroundImage = if (!imageUriString.isNullOrEmpty()) {
                try {
                    val bitmap = decodeSampledAndOrientedBitmap(
                        imageUriString,
                        MAX_BACKGROUND_IMAGE_DIMENSION_PX,
                        MAX_BACKGROUND_IMAGE_DIMENSION_PX
                    )
                    if (bitmap != null) ImageProvider(bitmap) else null
                } catch (e: Exception) {
                    Log.e("ExpressiveCountdownWidget", "Failed to load image", e)
                    null
                }
            } else {
                null
            }

            GlanceTheme(
                colors = customColorProviders ?: GlanceTheme.colors,
            ) {
                val target = targetString?.let { LocalDate.parse(it) }

                val (numberText, labelText) = if (target == null) {
                    "" to context.getString(R.string.config_pick_date)
                } else {
                    val days = daysLeft(
                        java.time.Clock.systemDefaultZone(),
                        target,
                        clampToZero = false
                    )
                    when {
                        days == 0L -> context.getString(R.string.today) to ""
                        days > 0L -> days.toString() to context.resources.getQuantityString(
                            R.plurals.days_left,
                            days.toInt(),
                        )
                        else -> days.unaryMinus().toString() to context.resources.getQuantityString(
                            R.plurals.days_ago,
                            days.unaryMinus().toInt(),
                        )
                    }
                }

                Log.d("ExpressiveCountdownWidget", "imageUri: $imageUriString")

                WidgetContent(
                    countdownNumberText = numberText,
                    countdownLabelText = labelText,
                    title = title ?: "",
                    backgroundImage = backgroundImage
                )
            }
        }
    }

    @Composable
    private fun WidgetContent(
        countdownNumberText: String,
        countdownLabelText: String,
        title: String,
        backgroundImage: ImageProvider?
    ) {
        val size = LocalSize.current

        val titleFontSize = when {
            size.width < 120.dp -> 12.sp
            size.width < 160.dp -> 18.sp
            else -> 28.sp
        }
        val countdownNumberFontSize = when {
            size.width < 120.dp -> 16.sp
            size.width < 160.dp -> 24.sp
            else -> 36.sp
        }
        val countdownLabelFontSize = when {
            size.width < 120.dp -> 12.sp
            size.width < 160.dp -> 18.sp
            else -> 28.sp
        }

        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .clickable(actionRunCallback<RefreshWidgetAction>())
                .then(
                    if (backgroundImage == null) {
                        GlanceModifier.background(GlanceTheme.colors.widgetBackground)
                    } else {
                        GlanceModifier
                    }
                )
        ) {
            if (backgroundImage != null) {
                Image(
                    provider = backgroundImage,
                    contentDescription = null,
                    modifier = GlanceModifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Column(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .padding(8.dp),
            ) {
                if (title.isNotBlank()) {
                    Text(
                        text = title,
                        style = TextStyle(
                            fontSize = titleFontSize,
                            color = GlanceTheme.colors.onSurface,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                Spacer(modifier = GlanceModifier.defaultWeight())

                if (countdownNumberText.isNotBlank()) {
                    Text(
                        text = countdownNumberText,
                        style = TextStyle(
                            fontSize = countdownNumberFontSize,
                            color = GlanceTheme.colors.primary,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                if (countdownLabelText.isNotBlank()) {
                    Text(
                        text = countdownLabelText,
                        style = TextStyle(
                            fontSize = countdownLabelFontSize,
                            color = GlanceTheme.colors.primary,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }
    }
}

// Widgets top out at 160x120dp; RemoteViews also enforces a per-device bitmap
// memory budget, so decode background photos down to a size that comfortably
// fits both instead of holding a full camera-resolution bitmap in memory.
private const val MAX_BACKGROUND_IMAGE_DIMENSION_PX = 640

private fun calculateInSampleSize(
    options: BitmapFactory.Options,
    reqWidth: Int,
    reqHeight: Int
): Int {
    val height = options.outHeight
    val width = options.outWidth
    var inSampleSize = 1

    if (height > reqHeight || width > reqWidth) {
        val halfHeight = height / 2
        val halfWidth = width / 2

        while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
            inSampleSize *= 2
        }
    }

    return inSampleSize
}

// Camera photos are usually stored in the sensor's native orientation with an
// EXIF tag saying how to rotate them for display; BitmapFactory ignores that
// tag, so without this the widget background comes out sideways/mirrored.
private fun decodeSampledAndOrientedBitmap(
    path: String,
    reqWidth: Int,
    reqHeight: Int
): Bitmap? {
    val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
    BitmapFactory.decodeFile(path, bounds)

    val options = BitmapFactory.Options().apply {
        inSampleSize = calculateInSampleSize(bounds, reqWidth, reqHeight)
    }
    val bitmap = BitmapFactory.decodeFile(path, options) ?: return null

    val orientation = ExifInterface(path).getAttributeInt(
        ExifInterface.TAG_ORIENTATION,
        ExifInterface.ORIENTATION_NORMAL
    )

    val matrix = Matrix()
    when (orientation) {
        ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
        ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
        ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
        ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.postScale(-1f, 1f)
        ExifInterface.ORIENTATION_FLIP_VERTICAL -> matrix.postScale(1f, -1f)
        ExifInterface.ORIENTATION_TRANSPOSE -> {
            matrix.postRotate(90f)
            matrix.postScale(-1f, 1f)
        }
        ExifInterface.ORIENTATION_TRANSVERSE -> {
            matrix.postRotate(270f)
            matrix.postScale(-1f, 1f)
        }
        else -> return bitmap
    }

    return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true).also {
        if (it !== bitmap) bitmap.recycle()
    }
}