// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
/* <CUSTOM_CALL_HEADER> */
package com.azure.android.communication.ui.calling

import android.os.SystemClock
import androidx.test.platform.app.InstrumentationRegistry
import com.azure.android.communication.BaseUiTest
import com.azure.android.communication.assertDisplayed
import com.azure.android.communication.assertTextDisplayed
import com.azure.android.communication.common.CommunicationTokenCredential
import com.azure.android.communication.common.CommunicationTokenRefreshOptions
import com.azure.android.communication.tapOnScreen
import com.azure.android.communication.tapWhenDisplayed
import com.azure.android.communication.ui.calling.models.CallCompositeCallDurationTimer
import com.azure.android.communication.ui.calling.models.CallCompositeCallScreenHeaderOptions
import com.azure.android.communication.ui.calling.models.CallCompositeCallScreenOptions
import com.azure.android.communication.ui.calling.models.CallCompositeLocalOptions
import com.azure.android.communication.ui.calling.models.CallCompositeTeamsMeetingLinkLocator
import com.azure.android.communication.waitUntilDisplayed
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Test

internal class CustomHeaderTest : BaseUiTest() {

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testCustomHeaderTextIsDisplayed() = runTest {
        injectDependencies(testScheduler)

        val header = "custom header"

        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val options = CallCompositeCallScreenOptions().setHeaderOptions(
            CallCompositeCallScreenHeaderOptions().setTitle(header)
        )
        val communicationTokenRefreshOptions =
            CommunicationTokenRefreshOptions({ "token" }, true)
        val communicationTokenCredential =
            CommunicationTokenCredential(communicationTokenRefreshOptions)

        val callComposite = CallCompositeBuilder().credential(communicationTokenCredential).displayName("test")
            .applicationContext(appContext).build()

        // Launch the UI.
        callComposite.launchTest(
            appContext,
            CallCompositeTeamsMeetingLinkLocator("https:teams.meeting"),
            CallCompositeLocalOptions().setCallScreenOptions(options)
        )

        tapWhenDisplayed(joinCallId)
        waitUntilDisplayed(endCallId)

        // Assert header displayed.
        assertTextDisplayed(header)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testTimerIsDisplayed() = runTest {
        injectDependencies(testScheduler)

        val header = "custom header"

        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val callCompositeCallDurationTimer = CallCompositeCallDurationTimer()
        val options = CallCompositeCallScreenOptions().setHeaderOptions(
            CallCompositeCallScreenHeaderOptions().setTitle(header).setTimer(callCompositeCallDurationTimer)
        )
        val communicationTokenRefreshOptions =
            CommunicationTokenRefreshOptions({ "token" }, true)
        val communicationTokenCredential =
            CommunicationTokenCredential(communicationTokenRefreshOptions)

        val callComposite = CallCompositeBuilder().credential(communicationTokenCredential).displayName("test")
            .applicationContext(appContext).build()

        // Launch the UI.
        callComposite.launchTest(
            appContext,
            CallCompositeTeamsMeetingLinkLocator("https:teams.meeting"),
            CallCompositeLocalOptions().setCallScreenOptions(options)
        )

        tapWhenDisplayed(joinCallId)
        waitUntilDisplayed(endCallId)
        callCompositeCallDurationTimer.start()

        // Assert header displayed.
        assertTextDisplayed(header)

        // Assert timer displayed.
        waitUntilDisplayed(callDurationTimerId)
        assertDisplayed(callDurationTimerId)

        // assert reset timer
        SystemClock.sleep(2000)
        tapOnScreen()
        callCompositeCallDurationTimer.reset()
        tapOnScreen()
        SystemClock.sleep(1000)
        assertTextDisplayed("00:00")
    }
}
/* </CUSTOM_CALL_HEADER> */
