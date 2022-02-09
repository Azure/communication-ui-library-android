// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callingcompositedemoapp

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.azure.android.communication.ui.callingcompositedemoapp.robots.HomeScreenRobot
import com.azure.android.communication.ui.callingcompositedemoapp.util.TestFixture
import com.azure.android.communication.ui.callingcompositedemoapp.util.UiTestUtils
import org.junit.Assert
import org.junit.Assume
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class CallingCompositeACSTokenTest : BaseUiTest() {

    companion object {
        @BeforeClass
        @JvmStatic
        fun tokenTestSetup() {
            // When running tests on AppCenter, a valid ACS token will be passed into local.properties
            // and not from command line arguments. In that case, don't run any ACS Token test
            Assume.assumeTrue(TestFixture.acsToken.isNotBlank())
        }
    }

    @Test
    fun testExpiredAcsToken() {
        val expiredAcsToken = UiTestUtils.getTextFromEdittextView(R.id.acsTokenText)
        Assert.assertTrue(
            "Invalid acs token: ${expiredAcsToken.length}",
            expiredAcsToken.length >= 700
        )

        val homeScreen = HomeScreenRobot()
            .setGroupIdOrTeamsMeetingUrl(TestFixture.groupId)
            .setAcsToken(expiredAcsToken)

        val setupScreen = homeScreen.clickLaunchButton()

        setupScreen
            .turnCameraOn()
            .clickJoinCallButton()

        homeScreen.clickAlertDialogOkButton()
    }

    @Test
    fun testEmptyAcsToken() {
        val homeScreen = HomeScreenRobot()
            .setGroupIdOrTeamsMeetingUrl(TestFixture.groupId)
            .setAcsToken("")

        val setupScreen = homeScreen.clickLaunchButton()
        homeScreen.clickAlertDialogOkButton()
    }
}
