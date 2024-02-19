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
import java9.util.concurrent.CompletableFuture
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.util.UUID

internal class CompositeExitAPITest : BaseUiTest() {
    @Test
    fun testCompositeExitSuccessWhenStateIsConnected() =
        runTest {
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
                    "test",
                )

            // assert state is none
            assert(callComposite.callState == CallCompositeCallStateCode.NONE)

            var isExitCompositeReceived = false
            val exitCallCompletableFuture = CompletableFuture<Void>()
            callComposite.addOnDismissedEventHandler {
                isExitCompositeReceived = true
                exitCallCompletableFuture.complete(null)
            }

            callComposite.launchTest(appContext, remoteOptions, null)

            tapWhenDisplayed(joinCallId)
            waitUntilDisplayed(endCallId)

            assert(callComposite.callState == CallCompositeCallStateCode.CONNECTED)
            // end call
            callComposite.dismiss()

            exitCallCompletableFuture.whenComplete { _, _ ->
                assert(isExitCompositeReceived)
                assert(callComposite.callState == CallCompositeCallStateCode.DISCONNECTED)
            }
        }

    @Test
    fun testCompositeExitSuccessWhenStateIsNone() =
        runTest {
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
                    "test",
                )

            // assert state is none
            assert(callComposite.callState == CallCompositeCallStateCode.NONE)
            var isExitCompositeReceived = false
            val exitCallCompletableFuture = CompletableFuture<Void>()
            callComposite.addOnDismissedEventHandler {
                isExitCompositeReceived = true
                exitCallCompletableFuture.complete(null)
            }
            callComposite.launchTest(appContext, remoteOptions, null)

            waitUntilDisplayed(joinCallId)

            assert(callComposite.callState == CallCompositeCallStateCode.NONE)
            // end call
            callComposite.dismiss()

            exitCallCompletableFuture.whenComplete { _, _ ->
                assert(isExitCompositeReceived)
                assert(callComposite.callState == CallCompositeCallStateCode.NONE)
            }
        }
}
