// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
package com.azure.android.communication.ui.calling

import androidx.test.platform.app.InstrumentationRegistry
import com.azure.android.communication.BaseUiTest
import com.azure.android.communication.common.CommunicationTokenCredential
import com.azure.android.communication.common.CommunicationTokenRefreshOptions
import com.azure.android.communication.tapOnText
import com.azure.android.communication.tapWhenDisplayed
import com.azure.android.communication.ui.calling.models.CallCompositeGroupCallLocator
import com.azure.android.communication.ui.calling.models.CallCompositeRemoteOptions
import com.azure.android.communication.ui.calling.models.CallCompositeUserReportedIssueEvent
import com.azure.android.communication.waitUntilDisplayed
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.util.UUID
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.azure.android.communication.assertTextNotDisplayed
import com.azure.android.communication.waitUntilTextDisplayed
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull

@OptIn(ExperimentalCoroutinesApi::class)
internal class SupportFormTest : BaseUiTest() {

    @Test
    fun testSupportFormIsDisplayedAndSendsEvent() = runTest {
        injectDependencies(testScheduler)
        // Launch the UI.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val callComposite = CallCompositeBuilder().build()
        val communicationTokenRefreshOptions =
            CommunicationTokenRefreshOptions({ "token" }, true)
        val communicationTokenCredential =
            CommunicationTokenCredential(communicationTokenRefreshOptions)
        val remoteOptions =
            CallCompositeRemoteOptions(
                CallCompositeGroupCallLocator(UUID.fromString("74fce2c1-520f-11ec-97de-71411a9a8e14")),
                communicationTokenCredential,
                "test"
            )

        var event: CallCompositeUserReportedIssueEvent? = null

        callComposite.addOnUserReportedEventHandler {
            event = it
        }

        callComposite.launchTest(appContext, remoteOptions, null)

        tapWhenDisplayed(joinCallId)
        waitUntilDisplayed(endCallId)
        tapWhenDisplayed(moreOptionsId)
        waitUntilTextDisplayed(showSupportFormTextId)
        tapOnText(showSupportFormTextId)
        waitUntilDisplayed(userMessageEditTextId)

        val testMessage = "Test support message"
        onView(withId(userMessageEditTextId))
            .perform(ViewActions.typeText(testMessage))

        tapWhenDisplayed(sendButtonId)

        assertNotNull(event)
        assertEquals(testMessage, event?.userMessage)
    }

    @Test
    fun testSupportFormIsNotDisplayedWhenNoHandler() = runTest {
        injectDependencies(testScheduler)
        // Launch the UI.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val callComposite = CallCompositeBuilder().build()
        val communicationTokenRefreshOptions =
            CommunicationTokenRefreshOptions({ "token" }, true)
        val communicationTokenCredential =
            CommunicationTokenCredential(communicationTokenRefreshOptions)
        val remoteOptions =
            CallCompositeRemoteOptions(
                CallCompositeGroupCallLocator(UUID.fromString("74fce2c1-520f-11ec-97de-71411a9a8e14")),
                communicationTokenCredential,
                "test"
            )

        callComposite.launchTest(appContext, remoteOptions, null)

        tapWhenDisplayed(joinCallId)
        waitUntilDisplayed(endCallId)
        tapWhenDisplayed(moreOptionsId)
        assertTextNotDisplayed(showSupportFormTextId)
    }
}
