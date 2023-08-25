// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling

import androidx.test.platform.app.InstrumentationRegistry
import com.azure.android.communication.BaseUiTest
import com.azure.android.communication.common.CommunicationTokenCredential
import com.azure.android.communication.common.CommunicationTokenRefreshOptions
import com.azure.android.communication.tapWhenDisplayed
import com.azure.android.communication.ui.calling.models.CallCompositeCallStateCode
import com.azure.android.communication.ui.calling.models.CallCompositeGroupCallLocator
import com.azure.android.communication.ui.calling.models.CallCompositeRemoteOptions
import com.azure.android.communication.waitUntilDisplayed
import java.util.UUID
import java9.util.concurrent.CompletableFuture
import kotlinx.coroutines.test.runTest
import org.junit.Test

internal class CallStateEventsTest : BaseUiTest() {

    @Test
    fun testCallStateConnectedEvents() = runTest {
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
        assert(callComposite.callState == CallCompositeCallStateCode.NONE)

        val list = mutableListOf<CallCompositeCallStateCode>()

        callComposite.addOnCallStateChangedEventHandler {
            list.add(it.code)
        }

        callComposite.launchTest(appContext, remoteOptions, null)

        tapWhenDisplayed(joinCallId)
        waitUntilDisplayed(endCallId)

        var size = list.size
        assert(size == 2)
        assert(list.contains(CallCompositeCallStateCode.CONNECTED))
        assert(list.contains(CallCompositeCallStateCode.NONE))
        assert(callComposite.callState == CallCompositeCallStateCode.CONNECTED)
    }

    @Test
    fun testCallStateDisconnectedEvents() = runTest {
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
        assert(callComposite.callState == CallCompositeCallStateCode.NONE)
        val list = mutableListOf<CallCompositeCallStateCode>()

        val endCallCompletableFuture = CompletableFuture<Void>()
        callComposite.addOnCallStateChangedEventHandler {
            list.add(it.code)
            if (it.code == CallCompositeCallStateCode.DISCONNECTED) {
                endCallCompletableFuture.complete(null)
            }
        }
        callComposite.launchTest(appContext, remoteOptions, null)

        tapWhenDisplayed(joinCallId)
        waitUntilDisplayed(endCallId)

        endCallCompletableFuture.whenComplete { _, _ ->
            assert(list.size == 3)
            assert(list.contains(CallCompositeCallStateCode.CONNECTED))
            assert(list.contains(CallCompositeCallStateCode.NONE))
            assert(list.contains(CallCompositeCallStateCode.DISCONNECTED))
            assert(callComposite.callState == CallCompositeCallStateCode.DISCONNECTED)
        }
    }
}
