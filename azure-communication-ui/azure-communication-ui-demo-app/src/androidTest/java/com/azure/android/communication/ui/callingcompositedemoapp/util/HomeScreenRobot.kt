package com.azure.android.communication.ui.callingcompositedemoapp.util

import androidx.test.espresso.action.ViewActions
import com.azure.android.communication.ui.callingcompositedemoapp.R
import com.azure.android.communication.ui.callingcompositedemoapp.robots.ScreenRobot
import com.azure.android.communication.ui.callingcompositedemoapp.robots.SetupScreenRobot

class HomeScreenRobot: ScreenRobot<HomeScreenRobot>() {
    fun setEmptyTeamsUrl(): HomeScreenRobot {
        waitUntilViewIdIsDisplayed(R.id.groupIdOrTeamsMeetingLinkText).run {
            perform(ViewActions.replaceText(""))
            perform(ViewActions.closeSoftKeyboard())
        }

        return this
    }

    fun setGroupIdOrTeamsMeetingUrl(groupIdOrTeamsMeetingUrl: String): HomeScreenRobot {
        if (groupIdOrTeamsMeetingUrl.isBlank()) return this
        waitUntilViewIdIsDisplayed(R.id.groupIdOrTeamsMeetingLinkText).run {
            perform(ViewActions.replaceText(groupIdOrTeamsMeetingUrl))
            perform(ViewActions.closeSoftKeyboard())
        }

        return this
    }

    fun setAcsToken(token: String): HomeScreenRobot {
        waitUntilViewIdIsDisplayed(R.id.acsTokenText).run {
            perform(ViewActions.replaceText(token))
            perform(ViewActions.closeSoftKeyboard())
        }
        return this
    }

    fun clickTeamsMeetingRadioButton(): HomeScreenRobot {
        waitUntilViewAndTextIsDisplayed(R.id.teamsMeetingRadioButton, R.string.teamsMeetingLabel)
        UiTestUtils.clickViewWithId(R.id.teamsMeetingRadioButton)
        return this
    }

    fun clickLaunchButton(): SetupScreenRobot {
        waitUntilViewIdIsDisplayed(R.id.launchButton)
        UiTestUtils.clickViewWithId(R.id.launchButton)
        return SetupScreenRobot()
    }

    fun clickAlertDialogOkButton() {
        waitUntilViewIdIsDisplayed(android.R.id.button1)
        UiTestUtils.clickViewWithIdAndText(android.R.id.button1, "OK")
    }
}