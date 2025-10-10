package com.yashdalfthegray.expressivecountdown

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.DialogProperties
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Slider
import androidx.compose.material3.TextButton
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColorPickerDialog(
    initialColor: Color,
    onDismiss: () -> Unit,
    onColorSelected: (Color) -> Unit
) {
    val initialHsv = remember {
        val hsv = FloatArray(3)
        android.graphics.Color.colorToHSV(initialColor.toArgb(), hsv)
        hsv
    }

    var hue by remember { mutableFloatStateOf(initialHsv[0]) }
    var saturation by remember { mutableFloatStateOf(initialHsv[1]) }
    var value by remember { mutableFloatStateOf(initialHsv[2]) }

    val selectedColor by remember {
        derivedStateOf {
            Color(android.graphics.Color.HSVToColor(floatArrayOf(hue, saturation, value)))
        }
    }

    BasicAlertDialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = dimensionResource(R.dimen.color_picker_dialog_elevation)
        ) {
            Column(
                modifier = Modifier
                    .padding(dimensionResource(R.dimen.color_picker_dialog_padding))
                    .fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.color_picker_dialog_title),
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = dimensionResource(R.dimen.padding_l))
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = dimensionResource(R.dimen.color_picker_slider_padding)),
                    verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_m))
                ) {
                    Text(
                        text = stringResource(R.string.color_picker_hue_label),
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(start = dimensionResource(R.dimen.padding_xs))
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_m)),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Slider(
                            value = hue,
                            onValueChange = { hue = it },
                            valueRange = 0f..360f,
                            modifier = Modifier.weight(1f),
                            track = { sliderState ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(dimensionResource(R.dimen.color_picker_slider_track_height))
                                        .background(
                                            brush = Brush.horizontalGradient(
                                                colors = listOf(
                                                    Color.Red,
                                                    Color.Yellow,
                                                    Color.Green,
                                                    Color.Cyan,
                                                    Color.Blue,
                                                    Color.Magenta,
                                                    Color.Red
                                                )
                                            ),
                                            shape = MaterialTheme.shapes.small
                                        )
                                )
                            }
                        )
                        Text(
                            text = hue.toInt().toString(),
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.widthIn(
                                min = dimensionResource(R.dimen.color_picker_hsv_number_label_width)
                            ),
                            textAlign = TextAlign.End
                        )
                    }

                    Text(
                        text = stringResource(R.string.color_picker_saturation_label),
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(start = dimensionResource(R.dimen.padding_xs))
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_m)),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Slider(
                            value = saturation,
                            onValueChange = { saturation = it },
                            valueRange = 0f..1f,
                            modifier = Modifier.weight(1f),
                            track = { sliderState ->
                                val saturationGradient = remember(hue, value) {
                                    Brush.horizontalGradient(
                                        colors = listOf(
                                            Color(android.graphics.Color.HSVToColor(floatArrayOf(hue, 0f, value))),
                                            Color(android.graphics.Color.HSVToColor(floatArrayOf(hue, 1f, value))),
                                        )
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(dimensionResource(R.dimen.color_picker_slider_track_height))
                                        .background(
                                            brush = saturationGradient,
                                            shape = MaterialTheme.shapes.small
                                        )
                                )
                            }
                        )
                        Text(
                            text = (saturation * 100).toInt().toString(),
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.widthIn(
                                min = dimensionResource(R.dimen.color_picker_hsv_number_label_width)
                            ),
                            textAlign = TextAlign.End
                        )
                    }

                    Text(
                        text = stringResource(R.string.color_picker_value_label),
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(start = dimensionResource(R.dimen.padding_xs))
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_m)),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Slider(
                            value = value,
                            onValueChange = { value = it },
                            valueRange = 0f..1f,
                            modifier = Modifier.weight(1f),
                            track = { sliderState ->
                                val valueGradient = remember(hue, saturation) {
                                    Brush.horizontalGradient(
                                        colors = listOf(
                                            Color.Black,
                                            Color(android.graphics.Color.HSVToColor(floatArrayOf(hue, saturation, 1f)))
                                        )
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(dimensionResource(R.dimen.color_picker_slider_track_height))
                                        .background(
                                            brush = valueGradient,
                                            shape = MaterialTheme.shapes.small
                                        )
                                )
                            }
                        )
                        Text(
                            text = (value * 100).toInt().toString(),
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.widthIn(
                                min = dimensionResource(R.dimen.color_picker_hsv_number_label_width)
                            ),
                            textAlign = TextAlign.End
                        )
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(text = stringResource(R.string.color_picker_dialog_cancel))
                    }
                    TextButton(onClick = { onColorSelected(selectedColor) }) {
                        Text(text = stringResource(R.string.color_picker_dialog_choose))
                    }
                }
            }
        }
    }
}

@Preview(
    name = "Color Picker Dialog",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Preview(
    name = "Color Picker Dialog (Dark)",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun ColorPickerDialogPreview() = ColorPickerDialog(
    initialColor = Color(0xFF6750A4),
    onDismiss = {},
    onColorSelected = {}
)
