package com.yashdalfthegray.expressivecountdown

import android.appwidget.AppWidgetManager
import android.content.Intent
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@RunWith(AndroidJUnit4::class)
class ConfigureActivityUITest {

    private val intent = Intent(ApplicationProvider.getApplicationContext(), ExpressiveCountdownConfigureActivity::class.java).apply {
        putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, 123)
    }

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ExpressiveCountdownConfigureActivity>()

    @Test
    fun configureActivity_showsBasicElements() {
        ActivityScenario.launch<ExpressiveCountdownConfigureActivity>(intent).use {
            composeTestRule.onNodeWithText("Pick a date").assertExists()
            composeTestRule.onNodeWithText("System").assertExists()
        }
    }

    @Test
    fun configureActivity_datePickerOpensAndCloses() {
        ActivityScenario.launch<ExpressiveCountdownConfigureActivity>(intent).use {
            composeTestRule.onNodeWithText("Pick a date").performClick()

            composeTestRule.onNodeWithText("Save").assertExists()
            composeTestRule.onNodeWithText("Cancel").assertExists()

            composeTestRule.onNodeWithText("Cancel").performClick()

            composeTestRule.onNodeWithText("Save").assertDoesNotExist()
        }
    }

    @Test
    fun configureActivity_colorModeSwitching() {
        ActivityScenario.launch<ExpressiveCountdownConfigureActivity>(intent).use {
            composeTestRule.onNodeWithText("Custom").performClick()

            composeTestRule.onNodeWithText("Pick custom color").assertExists()

            composeTestRule.onNodeWithText("System").performClick()

            composeTestRule.onNodeWithText("Pick custom color").assertDoesNotExist()
        }
    }

    @Test
    fun doneButton_enabledOnlyWhenValid() {
        ActivityScenario.launch<ExpressiveCountdownConfigureActivity>(intent).use {
            composeTestRule.onNodeWithContentDescription("Done").assertIsNotEnabled()

            composeTestRule.onNodeWithText("Pick a date").performClick()

            val lastDayOfMonth = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth())
            val formatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")
            val dateText = lastDayOfMonth.format(formatter)

            composeTestRule.onNodeWithText(dateText).performClick()
            composeTestRule.onNodeWithText("Save").performClick()

            composeTestRule.onNodeWithContentDescription("Done").assertIsEnabled()
        }
    }

    @Test
    fun doneButton_enabledOnlyWhenValidCustomColorIsPicked() {
        ActivityScenario.launch<ExpressiveCountdownConfigureActivity>(intent).use {
            composeTestRule.onNodeWithContentDescription("Done").assertIsNotEnabled()

            composeTestRule.onNodeWithText("Pick a date").performClick()

            val lastDayOfMonth = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth())
            val formatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")
            val dateText = lastDayOfMonth.format(formatter)

            composeTestRule.onNodeWithText(dateText).performClick()
            composeTestRule.onNodeWithText("Save").performClick()

            composeTestRule.onNodeWithText("Custom").performClick()
            composeTestRule.onNodeWithContentDescription("Done").assertIsEnabled()

            composeTestRule.onNodeWithContentDescription("Clear color").performClick()
            composeTestRule.onNodeWithContentDescription("Done").assertIsNotEnabled()
        }
    }

    @Test
    fun previewLabel_showsCorrectDaysCount() {
        ActivityScenario.launch<ExpressiveCountdownConfigureActivity>(intent).use {
            val today = LocalDate.now()
            val lastDayOfMonth = today.withDayOfMonth(today.lengthOfMonth())
            val daysUntilLastDay = ChronoUnit.DAYS.between(today, lastDayOfMonth)

            val formatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")
            val dateText = lastDayOfMonth.format(formatter)

            composeTestRule.onNodeWithText("Pick a date").performClick()
            composeTestRule.onNodeWithText(dateText).performClick()
            composeTestRule.onNodeWithText("Save").performClick()

            val expectedText = if (daysUntilLastDay == 1L) "1 day" else "$daysUntilLastDay days"
            composeTestRule.onNodeWithText(expectedText).assertExists()
        }
    }

    @Test
    fun customColorChip_displaysSelectedColor() {
        ActivityScenario.launch<ExpressiveCountdownConfigureActivity>(intent).use {
            val today = LocalDate.now()
            val lastDayOfMonth = today.withDayOfMonth(today.lengthOfMonth())
            val formatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")
            val dateText = lastDayOfMonth.format(formatter)

            composeTestRule.onNodeWithText("Pick a date").performClick()
            composeTestRule.onNodeWithText(dateText).performClick()
            composeTestRule.onNodeWithText("Save").performClick()

            composeTestRule.onNodeWithText("Custom").performClick()
            composeTestRule.onNodeWithText("#6750A4").assertExists()
            composeTestRule.onNodeWithContentDescription("Clear color").assertExists()

            composeTestRule.onNodeWithContentDescription("Clear color").performClick()
            composeTestRule.onNodeWithText("#6750A4").assertDoesNotExist()
            composeTestRule.onNodeWithContentDescription("Clear color").assertDoesNotExist()
        }
    }
}