// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling

import androidx.test.platform.app.InstrumentationRegistry
import com.azure.android.communication.BaseUiTest
import com.azure.android.communication.common.CommunicationTokenCredential
import com.azure.android.communication.common.CommunicationTokenRefreshOptions
import com.azure.android.communication.tapWhenDisplayed
import com.azure.android.communication.ui.calling.models.CallCompositeCallState
import com.azure.android.communication.ui.calling.models.CallCompositeExitEvent
import com.azure.android.communication.ui.calling.models.CallCompositeGroupCallLocator
import com.azure.android.communication.ui.calling.models.CallCompositeRemoteOptions
import com.azure.android.communication.waitUntilDisplayed
import java.util.UUID
import kotlinx.coroutines.test.runTest
import org.junit.Test

internal class CompositeExitAPITest : BaseUiTest() {

    @Test
    fun testCompositeExitSuccessWhenStateIsConnected() = runTest {
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

        // assert state is none
        assert(callComposite.callCompositeCallState == CallCompositeCallState.NONE)

        var isExitCallReceived = false
        callComposite.addOnExitEventHandler {
            isExitCallReceived = true
        }

        callComposite.launchTest(appContext, remoteOptions, null)

        tapWhenDisplayed(joinCallId)
        waitUntilDisplayed(endCallId)

        assert(callComposite.callCompositeCallState == CallCompositeCallState.CONNECTED)
        // end call
        callComposite.exit()

        assert(isExitCallReceived)
        assert(callComposite.callCompositeCallState == CallCompositeCallState.DISCONNECTED)
    }

    @Test
    fun testCompositeExitSuccessWhenStateIsNone() = runTest {
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

        // assert state is none
        assert(callComposite.callCompositeCallState == CallCompositeCallState.NONE)

        val handler = TestHandler()
        callComposite.addOnExitEventHandler(handler)

        callComposite.launchTest(appContext, remoteOptions, null)

        waitUntilDisplayed(joinCallId)

        assert(callComposite.callCompositeCallState == CallCompositeCallState.NONE)
        // end call
        callComposite.exit()

        assert(handler.list.size == 1)
        assert(callComposite.callCompositeCallState == CallCompositeCallState.NONE)
    }

    @Test
    fun testCompositeExitEventUnSubscriptionWhenStateIsConnected() = runTest {
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

        // assert state is none
        assert(callComposite.callCompositeCallState == CallCompositeCallState.NONE)

        val handler = TestHandler()
        callComposite.addOnExitEventHandler(handler)
        callComposite.removeOnExitEventHandler(handler)
        callComposite.launchTest(appContext, remoteOptions, null)

        tapWhenDisplayed(joinCallId)
        waitUntilDisplayed(endCallId)

        assert(callComposite.callCompositeCallState == CallCompositeCallState.CONNECTED)
        // end call
        callComposite.exit()

        assert(handler.list.size == 0)
        assert(callComposite.callCompositeCallState == CallCompositeCallState.DISCONNECTED)
    }

    class TestHandler : CallCompositeEventHandler<CallCompositeExitEvent> {
        val list = mutableListOf<CallCompositeExitEvent>()
        override fun handle(eventArgs: CallCompositeExitEvent) {
            list.add(eventArgs)
        }
    }
}
