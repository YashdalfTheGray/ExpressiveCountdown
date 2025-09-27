package com.yashdalfthegray.expressivecountdown

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.getAppWidgetState
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.lifecycle.lifecycleScope
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
            ConfigureScreen(
                appWidgetId = appWidgetId,
                onComplete = { millis, title ->
                    onDone(millis, title)
                }
            )
        }
    }

    private fun onDone(selectedDateMillis: Long, title: String) {
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
    onComplete: (Long, String) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var title by rememberSaveable { mutableStateOf("") }
    val dateState = rememberDatePickerState(initialSelectedDateMillis = null)

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
                title = storedTitle
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
        if (n == 1L) "1 day" else "$n days"
    } ?: "Pick a date"

    MaterialTheme {
        Surface(Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(
                    16.dp,
                    Alignment.CenterVertically
                ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Setup your countdown")

                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(8.dp))

                DatePicker(state = dateState)
                Text(previewLabel)

                Spacer(Modifier.height(8.dp))

                Button(
                    enabled = dateState.selectedDateMillis != null,
                    onClick = {
                        dateState.selectedDateMillis?.let { millis ->
                            scope.launch { onComplete(millis, title) }
                        }
                    }
                ) { Text("Done") }
            }
        }
    }
}
