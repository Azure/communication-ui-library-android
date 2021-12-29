// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callingcompositedemoapp


import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.GrantPermissionRule
import com.azure.android.communication.ui.callingcompositedemoapp.util.CompositeUiHelper
import com.github.kittinunf.fuel.httpGet
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.json.JSONObject
import org.junit.Assert
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
            acsToken = loadAcsToken()
        }

        private fun loadAcsToken(): String {

            val tokenFunctionURL = "https://acs-token-auth.azurewebsites.net/api/Auth?code=xXBqPQnuNyPT9oJTGUptTJ2FCe/LRUaX5m/PtFFe9F9oTl3Fwo2e9A=="
            val (request, response, result) = tokenFunctionURL
                .httpGet()
                .responseString()

            val resultBody = result.component1() ?: throw NullPointerException("Empty String!")
            val cause = result.component2()
            Assert.assertTrue(
                "network call error -> ${cause?.message}",
                cause == null && resultBody.isNotBlank()
            )

            val token = JSONObject(resultBody).getString("token")
            Assert.assertTrue("empty token! ", token.isNotBlank())
            return token
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

    @Test
    fun testJoinCallWithVideoDisabled() {
        CompositeUiHelper.run {
            setGroupId("74fce2c0-520f-11ec-97de-71411a9a8e13")
            Assert.assertTrue("empty token! ", acsToken.isNotBlank())
            setAcsToken(acsToken)
            clickLaunchButton()

            toggleCameraButton()

            clickJoinCallButton()
            showParticipantList()
            checkParticipantList()

            dismissParticipantList()
        }
    }

    private fun withViewAtPosition(position: Int, itemMatcher: Matcher<View?>): Matcher<View?>? {
        return object : BoundedMatcher<View?, RecyclerView>(RecyclerView::class.java) {
            override fun describeTo(description: Description?) {
                itemMatcher.describeTo(description)
            }

            override fun matchesSafely(recyclerView: RecyclerView): Boolean {
                val viewHolder = recyclerView.findViewHolderForAdapterPosition(position)
                return viewHolder != null && itemMatcher.matches(viewHolder.itemView)
            }
        }
    }
}
