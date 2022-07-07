// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling

import android.os.SystemClock
import com.azure.android.communication.BaseUiTest
import com.azure.android.communication.calling.MediaStreamType
import com.azure.android.communication.tapWhenDisplayed
import com.azure.android.communication.ui.calling.service.sdk.CommunicationIdentifier
import com.azure.android.communication.waitUntilDisplayed
import kotlinx.coroutines.test.runTest
import org.junit.Test

internal class RemoteParticipantEventsTest : BaseUiTest() {
    @Test
    fun testRemoteParticipantBasicEvents() = runTest {
        injectDependencies(testScheduler)

        // Launch the UI.
        launchComposite()
        tapWhenDisplayed(joinCallId)
        waitUntilDisplayed(endCallId)

        callingSDK.addRemoteParticipant(
            CommunicationIdentifier.CommunicationUserIdentifier("ACS User 1"),
            displayName = "ACS User 1",
            isMuted = true,
            isSpeaking = false,
            videoStreams = listOf(MediaStreamType.VIDEO)
        )

        SystemClock.sleep(5000)

        callingSDK.changeParticipant(
            "ACS User 1",
            isMuted = false,
            isSpeaking = true,
        )

        SystemClock.sleep(5000)

        callingSDK.addRemoteParticipant(
            CommunicationIdentifier.CommunicationUserIdentifier("ACS User 2"),
            displayName = "ACS User 2",
            isMuted = false,
            isSpeaking = true,
            videoStreams = listOf(MediaStreamType.SCREEN_SHARING)
        )

        SystemClock.sleep(500000)
    }
}
