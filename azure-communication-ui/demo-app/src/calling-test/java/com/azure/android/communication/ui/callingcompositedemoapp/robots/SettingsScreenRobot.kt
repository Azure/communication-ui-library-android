package com.azure.android.communication.ui.callingcompositedemoapp.robots

import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.matcher.ViewMatchers
import com.azure.android.communication.ui.callingcompositedemoapp.R
import com.azure.android.communication.ui.callingcompositedemoapp.util.UiTestUtils
import org.hamcrest.Matchers

class SettingsScreenRobot : ScreenRobot<SettingsScreenRobot>() {

    fun selectLanguageDropDown(language: String) {
        val dropDownIndicator = UiTestUtils.clickViewWithIdAndText(
            R.id.auto_complete_text_view,
            language
        )
        // close drop down list
        dropDownIndicator.perform(click())

        pressBack()
    }

    fun selectSetupScreenOrientationDropDown(orientation: String) {
        val dropDownIndicator = Espresso.onView(
            Matchers.allOf(
                ViewMatchers.withId(R.id.setup_screen_orientation_auto_complete_text_view),
                ViewMatchers.withText(orientation),
                ViewMatchers.isDisplayed()
            )
        ).perform(scrollTo()).perform(click())

        // close drop down list
        dropDownIndicator.perform(click())

        pressBack()
    }

    fun selectCallScreenOrientationDropDown(orientation: String) {
        val dropDownIndicator = Espresso.onView(
            Matchers.allOf(
                ViewMatchers.withId(R.id.call_screen_orientation_auto_complete_text_view),
                ViewMatchers.withText(orientation),
                ViewMatchers.isDisplayed()
            )
        ).perform(scrollTo()).perform(click())
        // close drop down list
        dropDownIndicator.perform(click())

        pressBack()
    }
}
