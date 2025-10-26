package com.yashdalfthegray.expressivecountdown

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.click
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yashdalfthegray.expressivecountdown.ui.theme.ExpressiveCountdownTheme
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ColorPickerDialogTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun colorPickerDialog_showsInitialColor() {
        composeTestRule.setContent {
            ExpressiveCountdownTheme {
                ColorPickerDialog(
                    initialColor = Color.Red,
                    onDismiss = {},
                    onColorSelected = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Choose a color").assertIsDisplayed()
        composeTestRule.onNodeWithText("Choose").assertIsDisplayed()
        composeTestRule.onNodeWithText("Cancel").assertIsDisplayed()
    }

    @Test
    fun colorPickerDialog_cancelDismissesDialog() {
        var dismissed = false

        composeTestRule.setContent {
            ExpressiveCountdownTheme {
                ColorPickerDialog(
                    initialColor = Color.Blue,
                    onDismiss = { dismissed = true },
                    onColorSelected = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Cancel").performClick()

        assert(dismissed)
    }

    @Test
    fun colorPickerDialog_chooseCallsOnColorSelected() {
        var selectedColor: Color? = null

        composeTestRule.setContent {
            ExpressiveCountdownTheme {
                ColorPickerDialog(
                    initialColor = Color.Green,
                    onDismiss = {},
                    onColorSelected = { color -> selectedColor = color }
                )
            }
        }

        composeTestRule.onNodeWithText("Choose").performClick()

        assertNotNull(selectedColor)
    }

    @Test
    fun colorPickerDialog_hueSliderChangesColor() {
        var selectedColor: Color? = null

        composeTestRule.setContent {
            ExpressiveCountdownTheme {
                ColorPickerDialog(
                    initialColor = Color.Red,
                    onDismiss = {},
                    onColorSelected = { color -> selectedColor = color }
                )
            }
        }

        composeTestRule.onNodeWithText("Hue").assertExists()

        val hueSlider = composeTestRule.onNodeWithTag("hue_slider")
        hueSlider.performTouchInput {
            click(Offset(visibleSize.width * 0.75f, visibleSize.height / 2f))
        }

        composeTestRule.onNodeWithText("Choose").performClick()

        assertNotNull(selectedColor)
        assertNotEquals(Color.Red.toArgb(), selectedColor?.toArgb())
    }

    @Test
    fun colorPickerDialog_saturationSliderChangesColor() {
        var selectedColor: Color? = null

        composeTestRule.setContent {
            ExpressiveCountdownTheme {
                ColorPickerDialog(
                    initialColor = Color.Red,
                    onDismiss = {},
                    onColorSelected = { color -> selectedColor = color }
                )
            }
        }

        val saturationSlider = composeTestRule.onNodeWithTag("saturation_slider")
        saturationSlider.performTouchInput {
            click(Offset(visibleSize.width * 0.25f, visibleSize.height / 2f))
        }

        composeTestRule.onNodeWithText("Choose").performClick()

        assertNotNull(selectedColor)
        assertNotEquals(Color.Red.toArgb(), selectedColor?.toArgb())
    }

    @Test
    fun colorPickerDialog_valueSliderChangesColor() {
        var selectedColor: Color? = null

        composeTestRule.setContent {
            ExpressiveCountdownTheme {
                ColorPickerDialog(
                    initialColor = Color.Red,
                    onDismiss = {},
                    onColorSelected = { color -> selectedColor = color }
                )
            }
        }

        val valueSlider = composeTestRule.onNodeWithTag("value_slider")
        valueSlider.performTouchInput {
            click(Offset(visibleSize.width * 0.5f, visibleSize.height / 2f))
        }

        composeTestRule.onNodeWithText("Choose").performClick()

        assertNotNull(selectedColor)
        assertNotEquals(Color.Red.toArgb(), selectedColor?.toArgb())
    }

    @Test
    fun colorPickerDialog_hexFieldUpdatesOnSliderChange() {
        composeTestRule.setContent {
            ExpressiveCountdownTheme {
                ColorPickerDialog(
                    initialColor = Color.Red,
                    onDismiss = {},
                    onColorSelected = {}
                )
            }
        }

        val hueSlider = composeTestRule.onNodeWithTag("hue_slider")
        hueSlider.performTouchInput {
            click(Offset(visibleSize.width * 0.5f, visibleSize.height / 2f))
        }

        // Hex field should update when slider changes
        composeTestRule.onNodeWithText("#FF0000").assertDoesNotExist()
    }
}