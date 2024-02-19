// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callingcompositedemoapp

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.azure.android.communication.ui.callingcompositedemoapp.robots.HomeScreenRobot
import com.azure.android.communication.ui.callingcompositedemoapp.util.CallIdentifiersHelper
import com.azure.android.communication.ui.callingcompositedemoapp.util.RunWhenScreenOffOrLockedRule
import com.azure.android.communication.ui.callingcompositedemoapp.util.TestFixture
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class CallingCompositeBaselineUiTest : BaseUiTest() {
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

    @Test
    fun testTeamsLobbyOverlay() {
        val setupScreen =
            HomeScreenRobot()
                .clickTeamsMeetingRadioButton()
                .setGroupIdOrTeamsMeetingUrl(TestFixture.teamsUrl)
                .setAcsToken(CallIdentifiersHelper.getACSToken())
                .clickLaunchButton()
        setupScreen.clickJoinCallButton()
            .checkTeamsLobbyOverlay()
            .clickEndCall()
            .clickLeaveCall()
    }

    private fun joinTeamsCall(videoEnabled: Boolean = true) {
        val setupScreen =
            HomeScreenRobot()
                .clickTeamsMeetingRadioButton()
                .setGroupIdOrTeamsMeetingUrl(TestFixture.teamsUrl)
                .setAcsToken(CallIdentifiersHelper.getACSToken())
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
        val setupScreen =
            HomeScreenRobot()
                .setGroupIdOrTeamsMeetingUrl(CallIdentifiersHelper.getGroupId())
                .setAcsToken(CallIdentifiersHelper.getACSToken())
                .clickLaunchButton()

        if (videoEnabled) {
            setupScreen.turnCameraOn()
        }
        val callScreen = setupScreen.clickJoinCallButton()
        callScreen
            .showParticipantList()
            .dismissParticipantList()
            .clickEndCall()
            .clickLeaveCall()
    }

    @Test
    fun testJoinAndLeaveMultipleTimes() {
        for (i in 0..5) {
            joinGroupCall()
            Thread.sleep(1000)
        }
    }
}
