// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callingcompositedemoapp.robots

import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import com.azure.android.communication.ui.callingcompositedemoapp.R
import com.azure.android.communication.ui.callingcompositedemoapp.util.UiTestUtils
import com.azure.android.communication.ui.callingcompositedemoapp.util.ViewIsDisplayedResource

class HomeScreenRobot : ScreenRobot<HomeScreenRobot>() {
    fun clickSettings(): SettingsScreenRobot {
        val viewDisplayResource = ViewIsDisplayedResource()
        val settings =
            waitUntilTextOnViewIsDisplayed(
                R.id.azure_composite_show_settings,
                "Settings",
            )
        settings.perform(click())
        return SettingsScreenRobot()
    }

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

    fun setEmptyAcsToken(): HomeScreenRobot {
        waitUntilViewIdIsDisplayed(R.id.acsTokenText).run {
            perform(ViewActions.replaceText(""))
            perform(ViewActions.closeSoftKeyboard())
        }
        return this
    }

    fun setAcsToken(token: String): HomeScreenRobot {
        if (token.isBlank()) return this
        waitUntilViewIdIsDisplayed(R.id.acsTokenText).run {
            perform(ViewActions.replaceText(token))
            perform(ViewActions.closeSoftKeyboard())
        }
        return this
    }

    fun clickTeamsMeetingRadioButton(): HomeScreenRobot {
        waitUntilTextOnViewIsDisplayed(R.id.teamsMeetingRadioButton, R.string.teams_meeting_label)
        UiTestUtils.clickViewWithId(R.id.teamsMeetingRadioButton)
        return this
    }

    fun clickGroupCallRadioButton(): HomeScreenRobot {
        waitUntilTextOnViewIsDisplayed(R.id.groupCallRadioButton, R.string.group_call_label)
        UiTestUtils.clickViewWithId(R.id.groupCallRadioButton)
        return this
    }

    fun clickLaunchButton(): SetupScreenRobot {
        waitUntilViewIdIsDisplayed(R.id.launchButton)
        UiTestUtils.clickViewWithId(R.id.launchButton)
        return SetupScreenRobot()
    }

    fun clickAlertDialogOkButton() {
        waitUntilTextOnViewIsDisplayed(android.R.id.button1, "OK")
        UiTestUtils.clickViewWithIdAndText(android.R.id.button1, "OK")
    }
}
