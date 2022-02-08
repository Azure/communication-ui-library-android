// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callingcompositedemoapp

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.GrantPermissionRule
import com.microsoft.appcenter.espresso.Factory
import com.microsoft.appcenter.espresso.ReportHelper
import com.azure.android.communication.ui.callingcompositedemoapp.util.CompositeUiHelper
import com.azure.android.communication.ui.callingcompositedemoapp.util.RunWhenScreenOffOrLockedRule
import com.azure.android.communication.ui.callingcompositedemoapp.util.TestFixture
import com.azure.android.communication.ui.callingcompositedemoapp.util.HomeScreenRobot
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class CallingCompositeBaselineUiTest: BaseUiTest() {

    @get:Rule
    val screenLockRule = RunWhenScreenOffOrLockedRule()

    @Test
    fun testJoinTeamsCallWithVideoEnabled() {
        joinTeamsCall()
    }

    @Test
    fun testJoinTeamsCallWithVideoDisabled() {
        joinTeamsCall(false)
    }

    @Test
    fun testJoinGroupCallWithVideoDisabled() {
        joinGroupCall(false)
    }

    @Test
    fun testJoinGroupCallWithVideoEnabled() {
        joinGroupCall()
    }


    private fun joinTeamsCall(videoEnabled: Boolean = true) {
        val setupScreen = HomeScreenRobot()
            .clickTeamsMeetingRadioButton()
            .setGroupIdOrTeamsMeetingUrl(TestFixture.teamsUrl)
            .setAcsToken(TestFixture.acsToken)
            .clickLaunchButton()

        if (videoEnabled) {
            setupScreen.turnCameraOn()
        }
        val callScreen = setupScreen.clickJoinCallButton()
        callScreen
            .checkWaitForTeamsMeetingMessage()
            .clickEndCall()
            .clickLeaveCall()
    }

    private fun joinGroupCall(videoEnabled: Boolean = true) {
        CompositeUiHelper.run {
            setGroupIdOrTeamsMeetingUrl(TestFixture.groupId)

            startAndJoinCall(TestFixture.acsToken, videoEnabled)
            showParticipantList()
            checkParticipantList()

            dismissParticipantList()
            clickEndCall()
            clickLeaveCall()
        }
    }
}
