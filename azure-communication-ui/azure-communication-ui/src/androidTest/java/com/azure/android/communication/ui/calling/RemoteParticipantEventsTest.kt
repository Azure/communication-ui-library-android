// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling

import com.azure.android.communication.BaseUiTest
import com.azure.android.communication.calling.MediaStreamType
import com.azure.android.communication.calling.ParticipantState
import com.azure.android.communication.tapWhenDisplayed
import com.azure.android.communication.ui.calling.service.sdk.CommunicationIdentifier
import com.azure.android.communication.waitUntilDisplayed
import kotlinx.coroutines.test.runTest
import org.junit.Test

internal class RemoteParticipantEventsTest : BaseUiTest() {
    @Test
    fun testRemoteParticipantBasicEvents() = runTest {
        injectDependencies(testScheduler)

        // A demonstration of how to invoke remote participant events.

        // Launch the UI.
        launchComposite()
        tapWhenDisplayed(joinCallId)
        waitUntilDisplayed(endCallId)

        "ACS User 1".also { userId ->
            callingSDK.addRemoteParticipant(
                CommunicationIdentifier.CommunicationUserIdentifier(userId),
                displayName = userId,
                isMuted = true,
                isSpeaking = false,
                videoStreams = listOf(MediaStreamType.VIDEO)
            )

            // todo assertions
            // verify muted icon is displayed

            callingSDK.changeParticipant(
                userId,
                isMuted = false,
                isSpeaking = true,
            )

            // verify muted icon is absent
            // verify isSpeaking frame

            callingSDK.changeParticipant(
                userId,
                state = ParticipantState.DISCONNECTED
            )

            // verify participant state change
        }

        callingSDK.addRemoteParticipant(
            CommunicationIdentifier.CommunicationUserIdentifier("ACS User 2"),
            displayName = "ACS User 2",
            isMuted = false,
            isSpeaking = true,
            videoStreams = listOf(MediaStreamType.SCREEN_SHARING)
        )

        // verify main speaker switched
        // verify isSpeaking frame
    }
}
