// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callingcompositedemoapp

import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import com.azure.android.communication.ui.callingcompositedemoapp.util.CompositeUiHelper
import com.azure.android.communication.ui.callingcompositedemoapp.util.RunWhenScreenOffOrLockedRule
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class CallingCompositeBaselineUiTest {
    companion object {
        private var acsToken = ""

        @BeforeClass
        @JvmStatic
        fun setup() {
            if (acsToken.isNotBlank()) return
            acsToken = loadAcsToken()
        }

        private fun loadAcsToken(): String {

            return InstrumentationRegistry.getArguments()
                .getString("acsToken") ?: return ""
        }
    }

    @Rule
    @JvmField
    var activityTestRule = ActivityScenarioRule(MainActivity::class.java)
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
        CompositeUiHelper.run {
            if (isGroupCall) {
                setGroupIdOrTeamsMeetingUrl("74fce2c0-520f-11ec-97de-71411a9a8e13")
            } else {
                clickTeamsMeetingRadioButton()
                setGroupIdOrTeamsMeetingUrl("https://teams.microsoft.com/l/meetup-join/19%3ameeting_OTgyYWRhZTgtNTA0MS00NjNlLTliMTQtNDJhN2I3YjVmZTM5%40thread.v2/0?context=%7b%22Tid%22%3a%2272f988bf-86f1-41af-91ab-2d7cd011db47%22%2c%22Oid%22%3a%22009cb10a-d33f-4e2f-85eb-249a30042a51%22%7d")
            }
            disableNetwork()

            startAndJoinCall(acsToken, true)

            dismissNetworkLossSnackbar()
            enableNetwork()

            navigateUpFromSetupScreen()
            clickAlertDialogOkButton()
        }
    }

    private fun joinTeamsCall(videoEnabled: Boolean = true) {
        CompositeUiHelper.run {
            clickTeamsMeetingRadioButton()
            setGroupIdOrTeamsMeetingUrl("https://teams.microsoft.com/l/meetup-join/19%3ameeting_OTgyYWRhZTgtNTA0MS00NjNlLTliMTQtNDJhN2I3YjVmZTM5%40thread.v2/0?context=%7b%22Tid%22%3a%2272f988bf-86f1-41af-91ab-2d7cd011db47%22%2c%22Oid%22%3a%22009cb10a-d33f-4e2f-85eb-249a30042a51%22%7d")
            startAndJoinCall(acsToken, videoEnabled)

            checkWaitForTeamsMeetingMessage()
            clickEndCall()
            clickLeaveCall()
        }
    }

    private fun joinGroupCall(videoEnabled: Boolean = true) {
        CompositeUiHelper.run {
            setGroupIdOrTeamsMeetingUrl("74fce2c0-520f-11ec-97de-71411a9a8e13")

            startAndJoinCall(acsToken, videoEnabled)
            showParticipantList()
            checkParticipantList()

            dismissParticipantList()
            clickEndCall()
            clickLeaveCall()
        }
    }
}
