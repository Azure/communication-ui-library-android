// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callingcompositedemoapp

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.azure.android.communication.ui.callingcompositedemoapp.robots.HomeScreenRobot
import com.azure.android.communication.ui.callingcompositedemoapp.util.CallIdentifiersHelper
import com.azure.android.communication.ui.callingcompositedemoapp.util.TestFixture
import com.azure.android.communication.ui.callingcompositedemoapp.util.UiTestUtils
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class CallingCompositeTeamsUrlTest : BaseUiTest() {
    @Before
    override fun setup() {
        super.setup()
        Thread.sleep(2000)
    }

    @Test
    fun testInvalidTeamsUrl() {
        val testString = getTeamsUrl().substringAfter("om")
        val homeScreen =
            HomeScreenRobot()
                .clickTeamsMeetingRadioButton()
                .setGroupIdOrTeamsMeetingUrl(testString)
                .setAcsToken(CallIdentifiersHelper.getACSToken())
        val setupScreen = homeScreen.clickLaunchButton()

        setupScreen.clickJoinCallButton()

        homeScreen.clickAlertDialogOkButton()
    }

    @Test
    fun testInvalidTeamsUrlTriggersAlert() {
        val teamsUrl = getTeamsUrl()
        val testString = teamsUrl.substring(0, teamsUrl.length - 6)
        val homeScreen =
            HomeScreenRobot()
                .clickTeamsMeetingRadioButton()
                .setGroupIdOrTeamsMeetingUrl(testString)
                .setAcsToken(CallIdentifiersHelper.getACSToken())
        val setupScreen = homeScreen.clickLaunchButton()

        setupScreen.clickJoinCallButton()

        homeScreen.clickAlertDialogOkButton()
    }

    @Test
    fun testEmptyTeamsUrl() {
        val homeScreen =
            HomeScreenRobot()
                .clickTeamsMeetingRadioButton()
                .setEmptyTeamsUrl()
                .setAcsToken(CallIdentifiersHelper.getACSToken())
        homeScreen.clickLaunchButton()

        homeScreen.clickAlertDialogOkButton()
    }

    private fun getTeamsUrl(): String {
        if (TestFixture.teamsUrl.isNotBlank()) return TestFixture.teamsUrl
        return UiTestUtils.getTextFromEdittextView(R.id.groupIdOrTeamsMeetingLinkText)
    }
}
