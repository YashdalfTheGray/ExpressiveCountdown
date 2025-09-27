package com.yashdalfthegray.expressivecountdown

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId

class ExpressiveCountdownConfigureActivity : ComponentActivity() {
    private var appWidgetId: Int = AppWidgetManager.INVALID_APPWIDGET_ID

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        appWidgetId = intent?.extras?.getInt(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        ) ?: AppWidgetManager.INVALID_APPWIDGET_ID

        val resultValue = Intent().putExtra(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            appWidgetId
        )

        setResult(RESULT_CANCELED, resultValue)

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }

        setContent {
            MaterialTheme {
                val pickerState = rememberDatePickerState(
                    initialSelectedDateMillis = System.currentTimeMillis()
                )
                var title by rememberSaveable { mutableStateOf("") }

                val selectedMillis = pickerState.selectedDateMillis
                val doneEnabled = selectedMillis != null

                val previewLabel = remember(selectedMillis) {
                    selectedMillis?.let {
                        val target = java.time.Instant.ofEpochMilli(it)
                            .atZone(java.time.ZoneId.systemDefault())
                            .toLocalDate()
                        val daysToTarget = daysLeft(
                            java.time.Clock.systemDefaultZone(),
                            target
                        )

                        if (daysToTarget == 1L) "1 day" else "$daysToTarget days"
                    } ?: "Select a date"
                }

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
                        Text("Expressive Countdown setup")
                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("Name") },
                            singleLine = true
                        )
                        DatePicker(state = pickerState)
                        Text(previewLabel)
                        Button(
                            enabled = doneEnabled,
                            onClick = {
                                selectedMillis?.let { millis ->
                                    onDone(millis, title)
                                }
                            }
                        ) { Text("Done") }
                    }
                }
            }
        }
    }

    private fun onDone(
        selectedDateMillis: Long,
        title: String
    ) {
        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            val resultValue = Intent().putExtra(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                appWidgetId
            )
            setResult(RESULT_CANCELED, resultValue)
            finish()
            return
        }

        val target = Instant.ofEpochMilli(selectedDateMillis)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()

        lifecycleScope.launch {
            val manager = GlanceAppWidgetManager(this@ExpressiveCountdownConfigureActivity)
            val glanceId = manager.getGlanceIdBy(appWidgetId)

            updateAppWidgetState(
                this@ExpressiveCountdownConfigureActivity,
                PreferencesGlanceStateDefinition,
                glanceId
            ) {
                it.toMutablePreferences().apply {
                    this[WidgetPreferencesKeys.TARGET_DATE] = target.toString()
                    if (title.isNotBlank()) {
                        this[WidgetPreferencesKeys.TITLE] = title
                    }
                }
            }

            ExpressiveCountdownWidget().update(
                this@ExpressiveCountdownConfigureActivity,
                glanceId
            )
        }

        val resultValue = Intent().putExtra(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            appWidgetId
        )
        setResult(RESULT_OK, resultValue)

        finish()
    }
}