// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callingcompositedemoapp

import android.util.Base64
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.azure.android.communication.ui.callingcompositedemoapp.robots.HomeScreenRobot
import com.azure.android.communication.ui.callingcompositedemoapp.util.TestFixture
import com.azure.android.communication.ui.callingcompositedemoapp.util.UiTestUtils
import org.json.JSONObject
import org.junit.Assert
import org.junit.Assume
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.*


@LargeTest
@RunWith(AndroidJUnit4::class)
class CallingCompositeACSTokenTest : BaseUiTest() {

    companion object {
        private const val ExpiryDate = "exp"

        @BeforeClass
        @JvmStatic
        fun tokenTestSetup() {
            // When running tests on AppCenter, a valid ACS token will be passed into local.properties
            // and not from command line arguments. In that case, don't run any ACS Token test
            Assume.assumeTrue(TestFixture.acsToken.isNotBlank())
        }
    }

    @Test
    fun testAcsTokenIsValid() {
        val jwtPayload = TestFixture.acsToken.split(".")?.getOrNull(1)

        require(jwtPayload != null && jwtPayload.length > 200)
        val decodedToken = Base64.decode(jwtPayload.toByteArray(), Base64.DEFAULT)
        val utfTokenString = String(decodedToken, StandardCharsets.UTF_8)
        var expiryDate = ""

        val accessJwt = JSONObject(utfTokenString)
        Assert.assertTrue("skypeId field missing from: $accessJwt", accessJwt.has("skypeid"))
        Assert.assertTrue("iat field missing from: $accessJwt", accessJwt.has("iat"))
        Assert.assertTrue("resourceId field missing from: $accessJwt", accessJwt.has("resourceId"))
        Assert.assertTrue("exp field missing from: $accessJwt", accessJwt.has(ExpiryDate))

        expiryDate = accessJwt.getString(ExpiryDate)
        val expiryTime = expiryDate.toLong() * 1000
        val now = Date().time

        Assert.assertTrue(
            "Acs token expired: currently $now, expire time: $expiryTime",
            now < expiryTime)
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
            .setEmptyAcsToken()

        val setupScreen = homeScreen.clickLaunchButton()
        homeScreen.clickAlertDialogOkButton()
    }
}
