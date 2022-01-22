// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callingcompositedemoapp

import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.GrantPermissionRule
import com.azure.android.communication.ui.callingcompositedemoapp.util.CompositeUiHelper
import com.azure.android.communication.ui.callingcompositedemoapp.util.TestFixture
import com.azure.android.communication.ui.callingcompositedemoapp.util.UiTestUtils
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class CallingCompositeACSTokenTest {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityScenarioRule(MainActivity::class.java)

    @Rule
    @JvmField
    var mGrantPermissionRule: GrantPermissionRule =
        GrantPermissionRule.grant(
            "android.permission.FOREGROUND_SERVICE",
            "android.permission.ACCESS_NETWORK_STATE",
            "android.permission.WAKE_LOCK",
            "android.permission.MODIFY_AUDIO_SETTINGS",
            "android.permission.CAMERA",
            "android.permission.RECORD_AUDIO"
        )

    @Before
    fun ciToolSetup() {
        Thread.sleep(2000)
    }

    @Test
    fun testExpiredAcsToken() {
        CompositeUiHelper.run {
            setGroupIdOrTeamsMeetingUrl(TestFixture.groupId)
            val expiredAcsToken =
                UiTestUtils.getTextFromEdittextView(R.id.acsTokenText)
            Assert.assertTrue(
                "Invalid acs token: ${expiredAcsToken.length}",
                expiredAcsToken.length >= 700
            )
            setAcsToken(expiredAcsToken)
            clickLaunchButton()

            turnCameraOn()

            clickJoinCallButton()

            clickAlertDialogOkButton()
        }
    }

    @Test
    fun testEmptyAcsToken() {
        CompositeUiHelper.run {
            setGroupIdOrTeamsMeetingUrl(TestFixture.groupId)
            setAcsToken("")

            clickLaunchButton()
            clickAlertDialogOkButton()
        }
    }
}
