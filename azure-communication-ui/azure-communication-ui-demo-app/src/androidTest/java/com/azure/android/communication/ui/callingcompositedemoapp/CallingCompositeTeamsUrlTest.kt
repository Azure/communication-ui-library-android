package com.azure.android.communication.ui.callingcompositedemoapp

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.azure.android.communication.ui.callingcompositedemoapp.robots.HomeScreenRobot
import com.azure.android.communication.ui.callingcompositedemoapp.util.TestFixture
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class CallingCompositeTeamsUrlTest : BaseUiTest() {

    @Test
    fun testInvalidTeamsUrl() {
        val testString = "https://t.com" + TestFixture.teamsUrl.substringAfter("om")
        val setupScreen = HomeScreenRobot()
            .clickTeamsMeetingRadioButton()
            .setGroupIdOrTeamsMeetingUrl(testString)
            .setAcsToken(TestFixture.acsToken)
            .clickLaunchButton()

        val callScreen = setupScreen.clickJoinCallButton()

        callScreen
            .checkWaitForTeamsMeetingMessage()
            .clickEndCall()
            .clickLeaveCall()
    }

    @Test
    fun testInvalidTeamsUrlTriggersAlert() {
        val testString = TestFixture.teamsUrl.substring(0, TestFixture.teamsUrl.length - 6)
        val homeScreen = HomeScreenRobot()
            .clickTeamsMeetingRadioButton()
            .setGroupIdOrTeamsMeetingUrl(testString)
            .setAcsToken(TestFixture.acsToken)
        val setupScreen = homeScreen.clickLaunchButton()

        val callScreen = setupScreen.clickJoinCallButton()

        homeScreen.clickAlertDialogOkButton()
    }

    @Test
    fun testInvalidTeamsUrlTriggersBanner() {
        val lastHalf = TestFixture.teamsUrl.substringAfter("context=").substringAfter("-")
        val testString = TestFixture.teamsUrl.substringBefore("%7b") + "%7b%22Tid%22%3a%227" + lastHalf
        val homeScreen = HomeScreenRobot()
            .clickTeamsMeetingRadioButton()
            .setGroupIdOrTeamsMeetingUrl(testString)
            .setAcsToken(TestFixture.acsToken)
        val setupScreen = homeScreen.clickLaunchButton()

        val callScreen = setupScreen.clickJoinCallButton()

        setupScreen
            .dismissJoinFailureBanner()
            .navigateUpFromSetupScreen()

        homeScreen.clickAlertDialogOkButton()
    }

    @Test
    fun testEmptyTeamsUrl() {
        val homeScreen = HomeScreenRobot()
            .clickTeamsMeetingRadioButton()
            .setEmptyTeamsUrl()
            .setAcsToken(TestFixture.acsToken)
        val setupScreen = homeScreen.clickLaunchButton()

        homeScreen.clickAlertDialogOkButton()
    }
}
