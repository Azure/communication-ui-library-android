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
import com.microsoft.appcenter.espresso.Factory
import com.microsoft.appcenter.espresso.ReportHelper
import org.junit.Assert
import org.junit.Assume
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class CallingCompositeACSTokenTest {

    companion object {
        @BeforeClass
        @JvmStatic
        fun setup() {
            // When running tests on AppCenter, a valid ACS token will be passed into local.properties
            // and not from command line arguments. In that case, don't run any ACS Token test
            Assume.assumeTrue(TestFixture.acsToken.isNotBlank())
        }
    }
    @get:Rule
    var reportHelper: ReportHelper = Factory.getReportHelper()

    @Rule
    @JvmField
    var mActivityTestRule = ActivityScenarioRule(CallLauncherActivity::class.java)

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
