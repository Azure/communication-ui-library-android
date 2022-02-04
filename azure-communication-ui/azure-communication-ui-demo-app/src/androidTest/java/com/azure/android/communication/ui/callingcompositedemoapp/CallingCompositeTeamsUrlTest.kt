package com.azure.android.communication.ui.callingcompositedemoapp

import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.GrantPermissionRule
import com.azure.android.communication.ui.callingcompositedemoapp.util.CompositeUiHelper
import com.azure.android.communication.ui.callingcompositedemoapp.util.HomeScreenRobot
import com.azure.android.communication.ui.callingcompositedemoapp.util.TestFixture
import com.azure.android.communication.ui.callingcompositedemoapp.util.UiTestUtils
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class CallingCompositeTeamsUrlTest: BaseUiTest() {

    @Test
    fun testInvalidTeamsUrl() {
        val setupScreen = HomeScreenRobot()
            .clickTeamsMeetingRadioButton()
            .setGroupIdOrTeamsMeetingUrl(TestFixture.teamsUrl)
            .setAcsToken(TestFixture.acsToken)
            .clickLaunchButton()

        val callScreen = setupScreen
            .turnCameraOn()
            .clickJoinCallButton()

        callScreen
            .checkWaitForTeamsMeetingMessage()
            .clickEndCall()
            .clickLeaveCall()

    }

    @Test
    fun testEmptyTeamsUrl() {

    }
}

