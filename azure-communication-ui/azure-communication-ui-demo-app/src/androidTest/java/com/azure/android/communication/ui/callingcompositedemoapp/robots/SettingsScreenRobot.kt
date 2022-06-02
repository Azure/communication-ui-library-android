package com.azure.android.communication.ui.callingcompositedemoapp.robots

import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import com.azure.android.communication.ui.callingcompositedemoapp.R
import com.azure.android.communication.ui.callingcompositedemoapp.util.UiTestUtils

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
}
