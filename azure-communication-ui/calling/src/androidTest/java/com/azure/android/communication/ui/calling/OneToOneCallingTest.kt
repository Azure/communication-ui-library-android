// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling

import androidx.test.platform.app.InstrumentationRegistry
import com.azure.android.communication.BaseUiTest
import com.azure.android.communication.calling.ParticipantState
import com.azure.android.communication.common.CommunicationIdentifier
import com.azure.android.communication.common.CommunicationTokenCredential
import com.azure.android.communication.common.CommunicationTokenRefreshOptions
import com.azure.android.communication.confirmTextDisplayed
import com.azure.android.communication.tapWhenDisplayed
import com.azure.android.communication.ui.calling.models.CallCompositeCallStateCode
import com.azure.android.communication.ui.calling.models.CallCompositeLocalOptions
import com.azure.android.communication.ui.calling.models.CallCompositeRemoteParticipantJoinedEvent
import com.azure.android.communication.waitUntilDisplayed
import com.azure.android.communication.waitUntilTextDisplayed
import java9.util.concurrent.CompletableFuture
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Test

internal class OneToOneCallingTest : BaseUiTest() {
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun startOutgoingCallWithoutSkipSetupAndParticipantStatusConnecting() = runTest {
        injectDependencies(testScheduler)

        // Launch the UI.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext

        val communicationTokenRefreshOptions =
            CommunicationTokenRefreshOptions({ "token" }, true)
        val communicationTokenCredential =
            CommunicationTokenCredential(communicationTokenRefreshOptions)

        val callComposite = CallCompositeBuilder()
            .credential(communicationTokenCredential)
            .context(appContext)
            .displayName("test")
            .build()

        // assert state is none
        assert(callComposite.callState == CallCompositeCallStateCode.NONE)

        var remoteParticipantJoined: CallCompositeRemoteParticipantJoinedEvent? = null
        val remoteParticipantJoinedCompletableFuture = CompletableFuture<Void>()
        callComposite.addOnRemoteParticipantJoinedEventHandler {
            remoteParticipantJoined = it
            remoteParticipantJoinedCompletableFuture.complete(null)
        }
        val calleeRawId = "8:acs:b6aada2f-0b1d-47ac-866f-91aae00a1d01_00000020-0840-99cd-51b9-a43a0d00cc28"

        callComposite.launchTest(appContext, listOf(CommunicationIdentifier.fromRawId(calleeRawId)), null)

        waitUntilTextDisplayed(startCallString)
        tapWhenDisplayed(joinCallId)
        callingSDK.addRemoteParticipant(
            com.azure.android.communication.ui.calling.service.sdk.CommunicationIdentifier.CommunicationUserIdentifier(calleeRawId),
            displayName = "User",
            state = ParticipantState.CONNECTING
        )
        confirmTextDisplayed(audioTextView, appContext.getString(callingString))
        waitUntilDisplayed(endCallId)

        remoteParticipantJoinedCompletableFuture.get()

        assert(remoteParticipantJoined?.identifiers?.first()?.rawId == calleeRawId)

        callComposite.dismiss()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun startOutgoingCallWithSkipSetupAndParticipantStatusConnected() = runTest {
        injectDependencies(testScheduler)

        // Launch the UI.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext

        val communicationTokenRefreshOptions =
            CommunicationTokenRefreshOptions({ "token" }, true)
        val communicationTokenCredential =
            CommunicationTokenCredential(communicationTokenRefreshOptions)

        val callComposite = CallCompositeBuilder()
            .credential(communicationTokenCredential)
            .context(appContext)
            .displayName("test")
            .build()

        // assert state is none
        assert(callComposite.callState == CallCompositeCallStateCode.NONE)

        var remoteParticipantJoined: CallCompositeRemoteParticipantJoinedEvent? = null
        val remoteParticipantJoinedCompletableFuture = CompletableFuture<Void>()
        callComposite.addOnRemoteParticipantJoinedEventHandler {
            remoteParticipantJoined = it
            remoteParticipantJoinedCompletableFuture.complete(null)
        }
        val calleeRawId = "8:acs:b6aada2f-0b1d-47ac-866f-91aae00a1d01_00000020-0840-99cd-51b9-a43a0d00cc28"
        val localOptions = CallCompositeLocalOptions().setSkipSetupScreen(true)
        callComposite.launchTest(appContext, listOf(CommunicationIdentifier.fromRawId(calleeRawId)), localOptions)

        callingSDK.addRemoteParticipant(
            com.azure.android.communication.ui.calling.service.sdk.CommunicationIdentifier.CommunicationUserIdentifier(calleeRawId),
            displayName = "User",
            state = ParticipantState.CONNECTED
        )
        confirmTextDisplayed(audioTextView, "User")
        waitUntilDisplayed(endCallId)

        remoteParticipantJoinedCompletableFuture.get()

        assert(remoteParticipantJoined?.identifiers?.first()?.rawId == calleeRawId)

        callComposite.dismiss()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun acceptCallWithoutSkipSetup() = runTest {
        injectDependencies(testScheduler)

        // Launch the UI.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext

        val communicationTokenRefreshOptions =
            CommunicationTokenRefreshOptions({ "token" }, true)
        val communicationTokenCredential =
            CommunicationTokenCredential(communicationTokenRefreshOptions)

        val callComposite = CallCompositeBuilder()
            .credential(communicationTokenCredential)
            .context(appContext)
            .displayName("test")
            .build()

        // assert state is none
        assert(callComposite.callState == CallCompositeCallStateCode.NONE)

        var remoteParticipantJoined: CallCompositeRemoteParticipantJoinedEvent? = null
        val remoteParticipantJoinedCompletableFuture = CompletableFuture<Void>()
        callComposite.addOnRemoteParticipantJoinedEventHandler {
            remoteParticipantJoined = it
            remoteParticipantJoinedCompletableFuture.complete(null)
        }
        val calleeRawId = "8:acs:b6aada2f-0b1d-47ac-866f-91aae00a1d01_00000020-0840-99cd-51b9-a43a0d00cc28"

        callComposite.launchTest(appContext, "abc", null)

        tapWhenDisplayed(joinCallId)
        callingSDK.addRemoteParticipant(
            com.azure.android.communication.ui.calling.service.sdk.CommunicationIdentifier.CommunicationUserIdentifier(calleeRawId),
            displayName = "User",
            state = ParticipantState.CONNECTED
        )
        waitUntilDisplayed(endCallId)
        waitUntilDisplayed(audioTextView)
        confirmTextDisplayed(audioTextView, "User")

        remoteParticipantJoinedCompletableFuture.get()

        assert(remoteParticipantJoined?.identifiers?.first()?.rawId == calleeRawId)

        callComposite.dismiss()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun acceptCallWithSkipSetup() = runTest {
        injectDependencies(testScheduler)

        // Launch the UI.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext

        val communicationTokenRefreshOptions =
            CommunicationTokenRefreshOptions({ "token" }, true)
        val communicationTokenCredential =
            CommunicationTokenCredential(communicationTokenRefreshOptions)

        val callComposite = CallCompositeBuilder()
            .credential(communicationTokenCredential)
            .context(appContext)
            .displayName("test")
            .build()

        // assert state is none
        assert(callComposite.callState == CallCompositeCallStateCode.NONE)

        var remoteParticipantJoined: CallCompositeRemoteParticipantJoinedEvent? = null
        val remoteParticipantJoinedCompletableFuture = CompletableFuture<Void>()
        callComposite.addOnRemoteParticipantJoinedEventHandler {
            remoteParticipantJoined = it
            remoteParticipantJoinedCompletableFuture.complete(null)
        }
        val calleeRawId = "8:acs:b6aada2f-0b1d-47ac-866f-91aae00a1d01_00000020-0840-99cd-51b9-a43a0d00cc28"
        val localOptions = CallCompositeLocalOptions().setSkipSetupScreen(true)
        callComposite.launchTest(appContext, "abc", localOptions)

        callingSDK.addRemoteParticipant(
            com.azure.android.communication.ui.calling.service.sdk.CommunicationIdentifier.CommunicationUserIdentifier(calleeRawId),
            displayName = "User",
            state = ParticipantState.CONNECTED
        )
        waitUntilDisplayed(endCallId)
        waitUntilDisplayed(audioTextView)
        confirmTextDisplayed(audioTextView, "User")

        remoteParticipantJoinedCompletableFuture.get()
        assert(remoteParticipantJoined?.identifiers?.first()?.rawId == calleeRawId)
        callComposite.dismiss()
    }
}
