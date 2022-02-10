package com.azure.android.communication.ui.callingcompositedemoapp

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.azure.android.communication.ui.callingcompositedemoapp.robots.HomeScreenRobot
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
        if (isAppCenter()) {
            Thread.sleep(2000)
        }
    }

    @Test
    fun testInvalidTeamsUrl() {
        val testString = getTeamsUrl().substringAfter("om")
        val homeScreen = HomeScreenRobot()
            .clickTeamsMeetingRadioButton()
            .setGroupIdOrTeamsMeetingUrl(testString)
            .setAcsToken(TestFixture.acsToken)
        val setupScreen = homeScreen.clickLaunchButton()

        val callScreen = setupScreen.clickJoinCallButton()

        homeScreen.clickAlertDialogOkButton()
    }

    @Test
    fun testInvalidTeamsUrlTriggersAlert() {
        val teamsUrl = getTeamsUrl()
        val testString = teamsUrl.substring(0, teamsUrl.length - 6)
        val homeScreen = HomeScreenRobot()
            .clickTeamsMeetingRadioButton()
            .setGroupIdOrTeamsMeetingUrl(testString)
            .setAcsToken(TestFixture.acsToken)
        val setupScreen = homeScreen.clickLaunchButton()

        val callScreen = setupScreen.clickJoinCallButton()

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

    private fun getTeamsUrl(): String {
        if (TestFixture.teamsUrl.isNotBlank()) return TestFixture.teamsUrl
        return UiTestUtils.getTextFromEdittextView(R.id.groupIdOrTeamsMeetingLinkText)
    }
}
