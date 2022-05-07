// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
package com.azure.android.communication.ui.callingcompositedemoapp

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.azure.android.communication.ui.callingcompositedemoapp.robots.CallScreenRobot
import com.azure.android.communication.ui.callingcompositedemoapp.robots.HomeScreenRobot
import com.azure.android.communication.ui.callingcompositedemoapp.util.TestFixture
import com.azure.android.communication.ui.callingcompositedemoapp.util.UiTestUtils
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class CallingCompositeParticipantListTest : BaseUiTest() {

    @Test
    fun testGroupCallParticipantName() {
        val userName = "${UiTestUtils.getTextFromEdittextView(R.id.userNameText)} (me)"
        joinGroupCall()
            .showParticipantList()
            .verifyFirstParticipantName(userName)
            .dismissParticipantList()
            .clickEndCall()
            .clickLeaveCall()
    }

    @Test
    fun testGroupCallParticipantList() {
        joinGroupCall()
            .showParticipantList()
            .checkFirstParticipantInList()
            .dismissParticipantList()
            .clickEndCall()
            .clickLeaveCall()
    }

    private fun joinGroupCall(videoEnabled: Boolean = true): CallScreenRobot {
        val setupScreen = HomeScreenRobot()
            .setGroupIdOrTeamsMeetingUrl(TestFixture.groupId)
            .setAcsToken(TestFixture.acsToken)
            .clickLaunchButton()

        if (videoEnabled) {
            setupScreen.turnCameraOn()
        }
        return setupScreen.clickJoinCallButton()
    }
}
