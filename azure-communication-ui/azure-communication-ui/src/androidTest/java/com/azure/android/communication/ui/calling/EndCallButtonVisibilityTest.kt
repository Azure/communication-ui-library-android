// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling

import android.os.SystemClock
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.azure.android.communication.common.CommunicationTokenCredential
import com.azure.android.communication.common.CommunicationTokenRefreshOptions
import com.azure.android.communication.mocking.TestCallingSDKWrapper
import com.azure.android.communication.mocking.TestVideoViewManager
import com.azure.android.communication.ui.R
import com.azure.android.communication.ui.calling.models.CallCompositeGroupCallLocator
import com.azure.android.communication.ui.calling.models.CallCompositeRemoteOptions
import com.azure.android.communication.ui.calling.utilities.CoroutineContextProvider
import com.azure.android.communication.ui.calling.utilities.TestHelper
import java.util.UUID
import java.util.concurrent.Callable
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import junit.framework.AssertionFailedError
import org.hamcrest.Matchers
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
internal class EndCallButtonVisibilityTest {

    @Test
    fun testCallEndButtonVisibilityAfterJoiningCall() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val callComposite = CallCompositeBuilder().build()
        val communicationTokenRefreshOptions =
            CommunicationTokenRefreshOptions(UrlTokenFetcher(), true)
        val communicationTokenCredential =
            CommunicationTokenCredential(communicationTokenRefreshOptions)
        val remoteOptions =
            CallCompositeRemoteOptions(
                CallCompositeGroupCallLocator(UUID.fromString("74fce2c1-520f-11ec-97de-71411a9a8e14")),
                communicationTokenCredential,
                "test"
            )
        TestHelper.videoViewManager = TestVideoViewManager(appContext)
        TestHelper.customCallingSDK = TestCallingSDKWrapper(
            CoroutineContextProvider()
        )

        callComposite.launchTest(appContext, remoteOptions, null)

        onView(withId(R.id.azure_communication_ui_setup_join_call_button)).perform(click())

        waitUntilViewIsDisplayed {
            onView(
                Matchers.allOf(
                    withId(R.id.azure_communication_ui_call_end_call_button)
                )
            ).check(matches(isDisplayed()))
        }

        onView(withId(R.id.azure_communication_ui_call_end_call_button)).check(matches(isDisplayed()))
    }

    class UrlTokenFetcher : Callable<String> {
        override fun call(): String {
            return "token"
        }
    }

    private fun waitUntilViewIsDisplayed(idlingCheck: () -> ViewInteraction): ViewInteraction {
        var isReady = false
        var timeOut = 0L
        lateinit var viewInteraction: ViewInteraction

        while (!isReady && timeOut < 70000L) {
            try {
                viewInteraction = idlingCheck()
                isReady = true
            } catch (ex: Throwable) {
                if (ex is AssertionFailedError || ex is NoMatchingViewException) {
                    SystemClock.sleep(2000L)
                    timeOut += 2000L
                } else throw ex
            }
        }
        if (isReady) return viewInteraction
        throw IllegalStateException("Timed out waiting for view")
    }
}
