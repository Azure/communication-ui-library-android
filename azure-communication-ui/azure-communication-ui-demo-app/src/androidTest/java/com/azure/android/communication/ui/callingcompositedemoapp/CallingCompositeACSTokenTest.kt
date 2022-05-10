// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callingcompositedemoapp

import android.util.Base64
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.azure.android.communication.ui.callingcompositedemoapp.robots.HomeScreenRobot
import com.azure.android.communication.ui.callingcompositedemoapp.util.CallIdentifiersHelper
import org.json.JSONObject
import org.junit.Assert
import org.junit.Assume
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import java.nio.charset.StandardCharsets
import java.util.Date

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
            Assume.assumeTrue(CallIdentifiersHelper.getACSToken().isNotBlank())
        }
    }

    @Test
    fun testAcsTokenIsValid() {
        val jwtPayload = CallIdentifiersHelper.getACSToken().split(".").getOrNull(1)

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
            now < expiryTime
        )
    }

    @Test
    fun testExpiredAcsToken() {
        val expiredAcsToken =
            "eyJhbGciOiJSUzI1NiIsImtpZCI6IjEwMyIsIng1dCI6Ikc5WVVVTFMwdlpLQTJUNjFGM1dzYWdCdmFMbyIsInR5cCI6IkpXVCJ9.eyJza3lwZWlkIjoiYWNzOjcxZWM1OTBiLWNiYWQtNDkwYy05OWM1LWI1NzhiZGFjZGU1NF8wMDAwMDAwZS00MDM0LTAyZTYtNmEwYi0zNDNhMGQwMDA2ZTEiLCJzY3AiOjE3OTIsImNzaSI6IjE2MzkwNzIyOTIiLCJleHAiOjE2MzkxNTg2OTIsImFjc1Njb3BlIjoidm9pcCIsInJlc291cmNlSWQiOiI3MWVjNTkwYi1jYmFkLTQ5MGMtOTljNS1iNTc4YmRhY2RlNTQiLCJpYXQiOjE2MzkwNzIyOTJ9.aS6Z93eQKHjTJVwM6NHO-goWInC1CSnFlqQQ7clXFL4ey_oL4JOcE6EfaM3KtNCrCOrPPtLUIetin_pEzXW0xz8fzN0CtEdVfqo0W12RH1W4gUeUTwrXfCt5z6gqHek0ixu8VtrQ6XT_1dSgpR49J2p0_kspkbWg_WajDiy3Lr1-_Zg28bRaJhhLsuwIs7WnV6tr_RrcDrzMNBjYALvesVryBkJCSu8BFBQoyFT7OoWvaywPT6AkRt8mJTwZUyTGWIqydEQd5hCpfspxqNyQJ1siWCkOjhOGNLWyq0xQ5GhQOYG2RYrV7t5WfWraJGnCEfVGCpedT92unkwctLyzCQ"
        Assert.assertTrue(
            "Invalid acs token: ${expiredAcsToken.length}",
            expiredAcsToken.length >= 700
        )

        val homeScreen = HomeScreenRobot()
            .setGroupIdOrTeamsMeetingUrl(CallIdentifiersHelper.getUUID())
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
            .setGroupIdOrTeamsMeetingUrl(CallIdentifiersHelper.getUUID())
            .setEmptyAcsToken()

        val setupScreen = homeScreen.clickLaunchButton()
        homeScreen.clickAlertDialogOkButton()
    }
}
