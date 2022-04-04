// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
package com.azure.android.communication.ui.callingcompositedemoapp.robots

import androidx.test.espresso.action.ViewActions
import com.azure.android.communication.ui.callingcompositedemoapp.R
import com.azure.android.communication.ui.callingcompositedemoapp.util.UiTestUtils

class HomeScreenRobot : ScreenRobot<HomeScreenRobot>() {
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
