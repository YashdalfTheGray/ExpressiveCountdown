package com.yashdalfthegray.expressivecountdown

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.TextButton
import androidx.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColorPickerDialog(
    initialColor: Color,
    onDismiss: () -> Unit,
    onColorSelected: (Color) -> Unit
) {
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
                    modifier = Modifier.padding(bottom = dimensionResource(R.dimen.padding_xl))
                )
                Text(
                    text = "Color picker coming soon",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = dimensionResource(R.dimen.padding_l))
                )

                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(text = stringResource(R.string.color_picker_dialog_cancel))
                    }
                    TextButton(onClick = { onColorSelected(initialColor) }) {
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
