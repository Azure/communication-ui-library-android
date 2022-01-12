// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callingcompositedemoapp

import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.GrantPermissionRule
import com.azure.android.communication.ui.callingcompositedemoapp.util.CompositeUiHelper
import com.azure.android.communication.ui.callingcompositedemoapp.util.UiTestUtils
import com.azure.android.communication.ui.callingcompositedemoapp.util.ViewIsDisplayedResource
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
            setGroupIdOrTeamsMeetingUrl("74fce2c0-520f-11ec-97de-71411a9a8e13")
            setAcsToken("eyJhbGciOiJSUzI1NiIsImtpZCI6IjEwMyIsIng1dCI6Ikc5WVVVTFMwdlpLQTJUNjFGM1dzYWdCdmFMbyIsInR5cCI6IkpXVCJ9.eyJza3lwZWlkIjoiYWNzOmU1Y2M1ZGMwLTkwODMtNGFmZC1iYmMwLThhZGQ0MWVmODcwOF8wMDAwMDAwZC1kM2ViLWQ2NWUtNGZmNy0zNDNhMGQwMDI1YTIiLCJzY3AiOjE3OTIsImNzaSI6IjE2MzcyNTU2MjMiLCJleHAiOjE2MzczNDIwMjMsImFjc1Njb3BlIjoidm9pcCIsInJlc291cmNlSWQiOiJlNWNjNWRjMC05MDgzLTRhZmQtYmJjMC04YWRkNDFlZjg3MDgiLCJpYXQiOjE2MzcyNTU2MjN9.M9Smjciv_zsd8RIjq8yiopyKco_L7Ye9vXfMn7HazwfspFfS0HxxnSE3JdbioWEXUJ5vXRm2wVdEkOd0JcY80qT6AXGmsJ4O-Q9f9ZhLEkS2saOiBpCZ9q_a5vpw2OfDsWCdbffWwbjZeTI3cgF_h5TbwLgQhSqxxPfPQ-Qu-XFKze-tUxGwFQ8c2Xy-LdjM2jYTA8hRoxgOTiFSFP2aS7k35ml9mvhlz0l6co9w-xKj-IAJD1zQpTytSK5YV36bMF-UhenlkNj3DLInDJ3og9sEwPaFLXPL-66iyVH0lzrBEi1aTEtS72et7I2ecRx2QWNScfhm8r_YAp75O__n3A")
            clickLaunchButton()

            turnCameraOn()

            clickJoinCallButton()

            ViewIsDisplayedResource().waitUntilViewIsDisplayed(::checkAlertDialogButtonIsDisplayed)
            UiTestUtils.clickViewWithIdAndText(android.R.id.button1, "OK")
        }
    }

    @Test
    fun testEmptyAcsToken() {
        CompositeUiHelper.run {
            setGroupIdOrTeamsMeetingUrl("74fce2c0-520f-11ec-97de-71411a9a8e13")
            setAcsToken("")

            clickLaunchButton()
            ViewIsDisplayedResource().waitUntilViewIsDisplayed(::checkAlertDialogButtonIsDisplayed)
            UiTestUtils.clickViewWithIdAndText(android.R.id.button1, "OK")
        }
    }

    private fun checkAlertDialogButtonIsDisplayed() =
        UiTestUtils.checkViewIdIsDisplayed(android.R.id.button1)
}
