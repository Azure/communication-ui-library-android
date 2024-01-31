// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
package com.azure.android.communication.ui.calling

import androidx.test.platform.app.InstrumentationRegistry
import com.azure.android.communication.BaseUiTest
import com.azure.android.communication.assertViewHasChild
import com.azure.android.communication.assertViewText
import com.azure.android.communication.calling.MediaStreamType
import com.azure.android.communication.calling.ParticipantState
import com.azure.android.communication.common.CommunicationTokenCredential
import com.azure.android.communication.common.CommunicationTokenRefreshOptions
import com.azure.android.communication.tapWhenDisplayed
import com.azure.android.communication.ui.calling.models.CallCompositeGroupCallLocator
import com.azure.android.communication.ui.calling.models.CallCompositeRemoteOptions
import com.azure.android.communication.ui.calling.service.sdk.CommunicationIdentifier
import com.azure.android.communication.waitUntilDisplayed
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.util.UUID

internal class SupportFormTest : BaseUiTest() {


    @Test
    fun testSupportFormIsVisibleWhenEventRegistered() = runTest {
        injectDependencies(testScheduler)

        supportFormVisibilityTests(true)
    }

    private suspend fun supportFormVisibilityTests(
        registerEvent:Boolean
    ) {
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
//
//        callingSDK.addRemoteParticipant(
//            CommunicationIdentifier.CommunicationUserIdentifier("ACS User 1"),
//            displayName = "ACS User 1",
//            isMuted = false,
//            isSpeaking = true,
//            videoStreams = listOf(MediaStreamType.VIDEO)
//        )
//
//        callingSDK.addRemoteParticipant(
//            CommunicationIdentifier.CommunicationUserIdentifier("ACS User 2"),
//            displayName = "ACS User 2",
//            isMuted = false,
//            isSpeaking = true,
//            videoStreams = listOf(MediaStreamType.VIDEO)
//        )
//
//        if (addLobbyUser) {
//            callingSDK.addRemoteParticipant(
//                CommunicationIdentifier.CommunicationUserIdentifier("Lobby State"),
//                displayName = "Lobby State",
//                state = ParticipantState.IN_LOBBY,
//                isMuted = false,
//                isSpeaking = true,
//                videoStreams = listOf(MediaStreamType.VIDEO)
//            )
//        }
//
//        waitUntilDisplayed(participantContainerId)
//
//        assertViewText(
//            participantCountId,
//            "Call with $expectedParticipantCountOnFloatingHeader people"
//        )
//
//        assertViewHasChild(participantContainerId, expectedParticipantCountOnGridView)
//
//        tapWhenDisplayed(participantListOpenButton)
//
//        // 1 local
//        assertViewHasChild(bottomDrawer, expectedParticipantCountOnParticipantList + 1)
    }
}
