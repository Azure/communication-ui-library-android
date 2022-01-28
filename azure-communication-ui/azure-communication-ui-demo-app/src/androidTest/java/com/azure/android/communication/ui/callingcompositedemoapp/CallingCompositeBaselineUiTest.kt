// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callingcompositedemoapp

import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.GrantPermissionRule
import com.azure.android.communication.ui.callingcompositedemoapp.util.CompositeUiHelper
import com.azure.android.communication.ui.callingcompositedemoapp.util.NetworkUtils
import com.azure.android.communication.ui.callingcompositedemoapp.util.RunWhenScreenOffOrLockedRule
import com.azure.android.communication.ui.callingcompositedemoapp.util.TestFixture
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class CallingCompositeBaselineUiTest {

    @Rule
    @JvmField
    var activityTestRule = ActivityScenarioRule(CallLauncherActivity::class.java)
    @Rule
    @JvmField
    var grantPermissionRule: GrantPermissionRule =
        GrantPermissionRule.grant(
            "android.permission.FOREGROUND_SERVICE",
            "android.permission.ACCESS_NETWORK_STATE",
            "android.permission.WAKE_LOCK",
            "android.permission.MODIFY_AUDIO_SETTINGS",
            "android.permission.CAMERA",
            "android.permission.RECORD_AUDIO"
        )

    @get:Rule
    val screenLockRule = RunWhenScreenOffOrLockedRule()

    @Before
    fun ciToolSetup() {
        Thread.sleep(2000)
    }

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
    fun testJoinTeamsCallAfterNetworkDisconnected() {
        joinAfterNetworkDisconnected(false)
    }

    @Test
    fun testJoinGroupCallAfterNetworkDisconnected() {
        joinAfterNetworkDisconnected()
    }

    private fun joinAfterNetworkDisconnected(isGroupCall: Boolean = true) {
        NetworkUtils.disableNetwork()
        CompositeUiHelper.run {
            if (isGroupCall) {
                setGroupIdOrTeamsMeetingUrl(TestFixture.groupId)
            } else {
                clickTeamsMeetingRadioButton()
                setGroupIdOrTeamsMeetingUrl(TestFixture.teamsUrl)
            }

            startAndJoinCall(TestFixture.acsToken, true)
            dismissNetworkLossSnackbar()

            NetworkUtils.enableNetworkThatWasDisabled {
                navigateUpFromSetupScreen()
                clickAlertDialogOkButton()
            }
        }
    }

    private fun joinTeamsCall(videoEnabled: Boolean = true) {
        CompositeUiHelper.run {
            clickTeamsMeetingRadioButton()
            setGroupIdOrTeamsMeetingUrl(TestFixture.teamsUrl)
            startAndJoinCall(TestFixture.acsToken, videoEnabled)

            checkWaitForTeamsMeetingMessage()
            clickEndCall()
            clickLeaveCall()
        }
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
