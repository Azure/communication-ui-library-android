// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling

import com.azure.android.communication.BaseUiTest
import com.azure.android.communication.assertViewText
import com.azure.android.communication.calling.MediaStreamType
import com.azure.android.communication.tapWhenDisplayed
import com.azure.android.communication.ui.calling.service.sdk.CommunicationIdentifier
import com.azure.android.communication.waitUntilDisplayed
import kotlinx.coroutines.test.runTest
import org.junit.Test

internal class RemoteParticipantCounterTest : BaseUiTest() {
    @Test
    fun testInitiallyPopulatedCallDisplaysCorrectParticipantCounter() =
        runTest {
            injectDependencies(testScheduler)

            // Add participants that are already present on the call before we join it.
            callingSDK.addRemoteParticipant(
                CommunicationIdentifier.MicrosoftTeamsUserIdentifier("Teams User 1", true),
                displayName = "Teams User 1",
            )
            callingSDK.addRemoteParticipant(
                CommunicationIdentifier.MicrosoftTeamsUserIdentifier("Teams User 2", false),
                displayName = "Teams User 2",
                videoStreams = listOf(MediaStreamType.VIDEO),
            )
            callingSDK.addRemoteParticipant(
                CommunicationIdentifier.PhoneNumberIdentifier("16047891234"),
                displayName = "16047891234",
            )
            callingSDK.addRemoteParticipant(
                CommunicationIdentifier.CommunicationUserIdentifier("ACS User 1"),
                displayName = "ACS User 1",
                videoStreams = listOf(MediaStreamType.SCREEN_SHARING),
            )

            // Launch the UI.
            launchComposite()
            tapWhenDisplayed(joinCallId)
            waitUntilDisplayed(endCallId)

            // Verify we're displaying correct participant count.
            assertViewText(participantCountId, "Call with 4 people")

            // One of the remote participants drops from the call.
            callingSDK.removeParticipant("ACS User 1")

            // Verify we're displaying a reduced participant count.
            assertViewText(participantCountId, "Call with 3 people")
        }

    @Test
    fun testInitiallyEmptyCallDisplaysCorrectParticipantCounter() =
        runTest {
            injectDependencies(testScheduler)

            // Launch the UI.
            launchComposite()
            tapWhenDisplayed(joinCallId)
            waitUntilDisplayed(endCallId)

            // Initial label with no remote participants present.
            assertViewText(participantCountId, "Waiting for others to join")

            // Add participants that are already present on the call before we join it.
            callingSDK.addRemoteParticipant(
                CommunicationIdentifier.MicrosoftTeamsUserIdentifier("Teams User 1", true),
                displayName = "Teams User 1",
            )

            callingSDK.addRemoteParticipant(
                CommunicationIdentifier.MicrosoftTeamsUserIdentifier("Teams User 2", false),
                displayName = "Teams User 2",
                videoStreams = listOf(MediaStreamType.VIDEO),
            )

            // Verify we're displaying correct participant count.
            assertViewText(participantCountId, "Call with 2 people")

            // One of the remote participants drops from the call.
            callingSDK.removeParticipant("Teams User 1")

            // Verify we're displaying a reduced participant count.
            assertViewText(participantCountId, "Call with 1 person")

            // Back to where we started, no participants.
            callingSDK.removeParticipant("Teams User 2")
            assertViewText(participantCountId, "Waiting for others to join")
        }

    @Test
    fun testLargeNumberOfParticipants() =
        runTest {
            injectDependencies(testScheduler)

            // Launch the UI.
            launchComposite()
            tapWhenDisplayed(joinCallId)
            waitUntilDisplayed(endCallId)

            val maxNumberOfPeople = 200

            // Add a bunch of remote participants into the call.
            repeat(maxNumberOfPeople) { id ->
                callingSDK.addRemoteParticipant(
                    CommunicationIdentifier.CommunicationUserIdentifier("ACS User $id"),
                    displayName = "ACS User $id",
                    videoStreams = listOf(MediaStreamType.VIDEO),
                )

                if (id == 0) {
                    assertViewText(participantCountId, "Call with 1 person")
                } else {
                    assertViewText(participantCountId, "Call with ${id + 1} people")
                }
            }

            assertViewText(participantCountId, "Call with $maxNumberOfPeople people")
        }
}
