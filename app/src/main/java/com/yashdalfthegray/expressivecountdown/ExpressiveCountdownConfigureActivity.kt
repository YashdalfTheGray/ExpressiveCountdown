package com.yashdalfthegray.expressivecountdown

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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

class ExpressiveCountdownConfigureActivity : ComponentActivity() {

    private var appWidgetId: Int = AppWidgetManager.INVALID_APPWIDGET_ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
                    onComplete = { millis, title, colorMode ->
                        onDone(millis, title, colorMode)
                    },
                    onCancel = {
                        setResult(RESULT_CANCELED)
                        finish()
                    }
                )
            }
        }
    }

    private fun onDone(selectedDateMillis: Long, title: String, colorMode: ColorMode) {
        lifecycleScope.launch {
            val manager = GlanceAppWidgetManager(this@ExpressiveCountdownConfigureActivity)
            val glanceId: GlanceId = manager.getGlanceIds(ExpressiveCountdownWidget::class.java)
                .firstOrNull { manager.getAppWidgetId(it) == appWidgetId }
                ?: return@launch

            val target = Instant.ofEpochMilli(selectedDateMillis)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()

            updateAppWidgetState(
                context = this@ExpressiveCountdownConfigureActivity,
                definition = PreferencesGlanceStateDefinition,
                glanceId = glanceId
            ) { prefs ->
                prefs.toMutablePreferences().apply {
                    this[WidgetPreferencesKeys.TARGET_DATE] = target.toString()
                    this[WidgetPreferencesKeys.TITLE] = title.trim()
                    this[WidgetPreferencesKeys.COLOR_MODE] = colorMode.name
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ConfigureScreen(
    appWidgetId: Int,
    onComplete: (Long, String, ColorMode) -> Unit,
    onCancel: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var title by rememberSaveable { mutableStateOf("") }
    val dateState = rememberDatePickerState(initialSelectedDateMillis = null)
    var colorMode by rememberSaveable { mutableStateOf(ColorMode.System) }

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

                title = storedTitle
                colorMode = storedColorMode
                storedDate?.let { date ->
                    val millis = date.atStartOfDay(ZoneId.systemDefault())
                        .toInstant()
                        .toEpochMilli()
                    dateState.selectedDateMillis = millis
                }
            }
        }
    }

    val previewLabel = dateState.selectedDateMillis?.let { millis ->
        val target = Instant.ofEpochMilli(millis)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
        val n = daysLeft(Clock.systemDefaultZone(), target)
        if (n == 1L) stringResource(R.string.one_day) else context.getString(R.string.many_days, n)
    } ?: stringResource(R.string.config_pick_date)

    val canComplete = dateState.selectedDateMillis != null

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
                                    onComplete(millis, title, colorMode)
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
                    onClick = { colorMode = ColorMode.Photo },
                    selected = colorMode == ColorMode.Photo
                ) {
                    Text(text = stringResource(R.string.color_mode_photo))
                }
            }

            DatePicker(state = dateState)

            Text(
                text = previewLabel,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }

}