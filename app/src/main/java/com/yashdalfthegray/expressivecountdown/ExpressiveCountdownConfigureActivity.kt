package com.yashdalfthegray.expressivecountdown

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.getAppWidgetState
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.lifecycle.lifecycleScope
import com.yashdalfthegray.expressivecountdown.ui.theme.ExpressiveCountdownTheme
import kotlinx.coroutines.launch
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import androidx.core.graphics.drawable.toDrawable
import coil.compose.rememberAsyncImagePainter
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import androidx.core.net.toUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class ExpressiveCountdownConfigureActivity : ComponentActivity() {

    private var appWidgetId: Int = AppWidgetManager.INVALID_APPWIDGET_ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val dynamicColorScheme = if (resources.configuration.uiMode and
                android.content.res.Configuration.UI_MODE_NIGHT_MASK ==
                android.content.res.Configuration.UI_MODE_NIGHT_YES) {
                dynamicDarkColorScheme(this)
            } else {
                dynamicLightColorScheme(this)
            }
            window.setBackgroundDrawable(dynamicColorScheme.background.toArgb().toDrawable())
        }

        appWidgetId = intent?.getIntExtra(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        ) ?: AppWidgetManager.INVALID_APPWIDGET_ID

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            setResult(RESULT_CANCELED)
            finish()
            return
        }

        setResult(RESULT_CANCELED)

        setContent {
            ExpressiveCountdownTheme {
                ConfigureScreen(
                    appWidgetId = appWidgetId,
                    onComplete = { millis, title, colorMode, photoUri ->
                        onDone(millis, title, colorMode, photoUri)
                    },
                    onCancel = {
                        setResult(RESULT_CANCELED)
                        finish()
                    }
                )
            }
        }
    }

    private fun onDone(
        selectedDateMillis: Long,
        title: String,
        colorMode: ColorMode,
        photoUri: Uri?
    ) {
        photoUri?.let { uri ->
            try {
                contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )

                grantUriPermission(
                    "com.yashdalfthegray.expressivecountdown",
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (e: SecurityException) {
                Log.e("ExpressiveCountdownConfigureActivity", "Failed to grant URI permission", e)
            }

        }

        lifecycleScope.launch {
            val manager = GlanceAppWidgetManager(this@ExpressiveCountdownConfigureActivity)
            val glanceId: GlanceId = manager.getGlanceIds(ExpressiveCountdownWidget::class.java)
                .firstOrNull { manager.getAppWidgetId(it) == appWidgetId }
                ?: return@launch

            val target = Instant.ofEpochMilli(selectedDateMillis)
                .atZone(ZoneId.of("UTC"))
                .toLocalDate()

            val imagePath = saveWidgetImage(photoUri, appWidgetId)

            updateAppWidgetState(
                context = this@ExpressiveCountdownConfigureActivity,
                definition = PreferencesGlanceStateDefinition,
                glanceId = glanceId
            ) { prefs ->
                prefs.toMutablePreferences().apply {
                    this[WidgetPreferencesKeys.TARGET_DATE] = target.toString()
                    this[WidgetPreferencesKeys.TITLE] = title.trim()
                    this[WidgetPreferencesKeys.COLOR_MODE] = colorMode.name
                    this[WidgetPreferencesKeys.IMAGE_URL] = imagePath
                }
            }

            ExpressiveCountdownWidget().update(this@ExpressiveCountdownConfigureActivity, glanceId)

            val resultValue = Intent().putExtra(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                appWidgetId
            )
            setResult(RESULT_OK, resultValue)
            finish()
        }
    }

    private suspend fun saveWidgetImage(uri: Uri?, widgetId: Int): String {
        return withContext(Dispatchers.IO) {
            val imageFile = File(filesDir, "widget_${widgetId}_background.jpg")

            if (uri == null) {
                if (imageFile.exists()) {
                    imageFile.delete()
                    Log.d("ExpressiveCountdownConfigureActivity", "Deleted image for widget $widgetId")
                }
                return@withContext ""
            }

            try {
                if (imageFile.exists()) {
                    imageFile.delete()
                }

                contentResolver.openInputStream(uri)?.use { input ->
                    imageFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }

                Log.d("ExpressiveCountdownConfigureActivity", "Saved image: ${imageFile.absolutePath}")
                imageFile.absolutePath
            } catch (e: Exception) {
                Log.e("ExpressiveCountdownConfigureActivity", "Failed to save image", e)
                ""
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ConfigureScreen(
    appWidgetId: Int,
    onComplete: (Long, String, ColorMode, Uri?) -> Unit,
    onCancel: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var title by rememberSaveable { mutableStateOf("") }
    val dateState = rememberDatePickerState(initialSelectedDateMillis = null)
    var colorMode by rememberSaveable { mutableStateOf(ColorMode.System) }
    var showDatePicker by rememberSaveable { mutableStateOf(false) }
    var selectedPhotoUri by rememberSaveable { mutableStateOf<Uri?>(null) }

    val pickMedia = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        selectedPhotoUri = uri
    }

    LaunchedEffect(appWidgetId) {
        val manager = AppWidgetManager.getInstance(context)
        val providerInfo = manager.getAppWidgetInfo(appWidgetId)
        if (providerInfo != null) {
            val manager = GlanceAppWidgetManager(context)
            val glanceId: GlanceId? = manager.getGlanceIds(ExpressiveCountdownWidget::class.java)
                .firstOrNull { manager.getAppWidgetId(it) == appWidgetId }

            if (glanceId != null) {
                val prefs = getAppWidgetState(
                    context = context,
                    definition = PreferencesGlanceStateDefinition,
                    glanceId = glanceId
                )
                val storedTitle = prefs[WidgetPreferencesKeys.TITLE] ?: ""
                val storedDate = prefs[WidgetPreferencesKeys.TARGET_DATE]?.let(LocalDate::parse)
                val storedColorMode = prefs[WidgetPreferencesKeys.COLOR_MODE]?.let {
                    runCatching { ColorMode.valueOf(it) }.getOrNull()
                } ?: ColorMode.System
                val storedPhotoUri = prefs[WidgetPreferencesKeys.IMAGE_URL]?.let {
                    if (it.isNotEmpty()) it.toUri() else null
                }

                title = storedTitle
                colorMode = storedColorMode
                selectedPhotoUri = storedPhotoUri
                storedDate?.let { date ->
                    val millis = date.atStartOfDay(ZoneId.of("UTC"))
                        .toInstant()
                        .toEpochMilli()

                    dateState.selectedDateMillis = millis
                    dateState.displayedMonthMillis = millis
                }
            }
        }
    }

    val previewLabel = dateState.selectedDateMillis?.let { millis ->
        val target = Instant.ofEpochMilli(millis)
            .atZone(ZoneId.of("UTC"))
            .toLocalDate()
        val n = daysLeft(Clock.systemDefaultZone(), target)
        if (n == 1L) stringResource(R.string.one_day) else context.getString(R.string.many_days, n)
    } ?: stringResource(R.string.config_pick_date)

    val canComplete = dateState.selectedDateMillis != null
    val hasPhoto = selectedPhotoUri != null

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.config_title)) },
                navigationIcon = {
                    IconButton(onClick = onCancel) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Cancel"
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            dateState.selectedDateMillis?.let { millis ->
                                scope.launch {
                                    onComplete(millis, title, colorMode, selectedPhotoUri)
                                }
                            }
                        },
                        enabled = canComplete
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Done"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(dimensionResource(R.dimen.activity_padding))
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(
                dimensionResource(R.dimen.vertical_spacing)
            ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text(stringResource(R.string.config_text_name)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedButton(
                onClick = { showDatePicker = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = dateState.selectedDateMillis?.let { millis ->
                        val date = Instant.ofEpochMilli(millis)
                            .atZone(ZoneId.of("UTC"))
                            .toLocalDate()
                        val formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
                        date.format(formatter)
                    } ?: stringResource(R.string.config_pick_date)
                )
            }

            if (showDatePicker) {
                DatePickerDialog(
                    onDismissRequest = { showDatePicker = false },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                showDatePicker = false
                            }
                        ) {
                            Text(stringResource(R.string.datepicker_dialog_confirm))
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = {
                                showDatePicker = false
                            }
                        ) {
                            Text(stringResource(R.string.datepicker_dialog_cancel))
                        }
                    }
                ) {
                    DatePicker(state = dateState)
                }
            }

            OutlinedButton(
                onClick = {
                    pickMedia.launch(
                        PickVisualMediaRequest(
                            mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.config_pick_photo)
                )
            }

            Text(
                text = stringResource(R.string.config_set_color_mode),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.fillMaxWidth()
            )

            SingleChoiceSegmentedButtonRow(
                modifier = Modifier.fillMaxWidth()
            ) {
                SegmentedButton(
                    shape = SegmentedButtonDefaults.itemShape(index = 0, count = 3),
                    onClick = { colorMode = ColorMode.System },
                    selected = colorMode == ColorMode.System
                ) {
                    Text(text = stringResource(R.string.color_mode_system))
                }

                SegmentedButton(
                    shape = SegmentedButtonDefaults.itemShape(index = 1, count = 3),
                    onClick = { colorMode = ColorMode.Custom },
                    selected = colorMode == ColorMode.Custom
                ) {
                    Text(text = stringResource(R.string.color_mode_custom))
                }

                SegmentedButton(
                    shape = SegmentedButtonDefaults.itemShape(index = 2, count = 3),
                    onClick = {
                        if (hasPhoto) {
                            colorMode = ColorMode.Photo
                        }
                    },
                    selected = colorMode == ColorMode.Photo,
                    enabled = hasPhoto,
                    colors = SegmentedButtonDefaults.colors(
                        disabledActiveContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                        disabledActiveContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
                        disabledInactiveContainerColor = Color.Transparent,
                        disabledInactiveContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                    )
                ) {
                    Text(text = stringResource(R.string.color_mode_photo))
                }
            }
            Text(
                text = stringResource(R.string.photo_button_message),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = dimensionResource(R.dimen.padding_s))
            )

            Text(
                text = stringResource(R.string.config_photo_preview),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = dimensionResource(R.dimen.padding_m))
            )

            Text(
                text = dateState.selectedDateMillis?.let { previewLabel } ?: "",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(dimensionResource(R.dimen.empty_label_height))
                    .wrapContentWidth(Alignment.CenterHorizontally)
            )

            if (selectedPhotoUri != null) {
                Box(
                    modifier = Modifier
                        .size(dimensionResource(R.dimen.photo_preview_size))
                        .align(Alignment.CenterHorizontally)
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(selectedPhotoUri),
                        contentDescription = stringResource(R.string.photo_preview_description),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(dimensionResource(R.dimen.clipping_radius)))
                            .border(
                                width = dimensionResource(R.dimen.photo_preview_border),
                                color = MaterialTheme.colorScheme.outline,
                                shape = RoundedCornerShape(dimensionResource(R.dimen.clipping_radius))
                            )
                    )
                    IconButton(
                        onClick = { selectedPhotoUri = null },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(dimensionResource(R.dimen.photo_remove_button_padding))
                            .size(dimensionResource(R.dimen.photo_remove_button_size))
                            .background(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(R.string.close_icon_description),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

            }
        }
    }
}