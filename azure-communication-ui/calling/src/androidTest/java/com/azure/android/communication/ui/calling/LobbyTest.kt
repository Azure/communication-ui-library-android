// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
package com.azure.android.communication.ui.calling

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import com.azure.android.communication.BaseUiTest
import com.azure.android.communication.assertNotDisplayed
import com.azure.android.communication.assertViewHasChild
import com.azure.android.communication.assertViewText
import com.azure.android.communication.calling.MediaStreamType
import com.azure.android.communication.calling.ParticipantState
import com.azure.android.communication.common.CommunicationTokenCredential
import com.azure.android.communication.common.CommunicationTokenRefreshOptions
import com.azure.android.communication.tapOnScreen
import com.azure.android.communication.tapWhenDisplayed
import com.azure.android.communication.tapWithTextWhenDisplayed
import com.azure.android.communication.ui.calling.models.CallCompositeGroupCallLocator
import com.azure.android.communication.ui.calling.models.CallCompositeParticipantRole
import com.azure.android.communication.ui.calling.models.CallCompositeRemoteOptions
import com.azure.android.communication.ui.calling.models.CallCompositeTeamsMeetingLinkLocator
import com.azure.android.communication.ui.calling.service.sdk.CommunicationIdentifier
import com.azure.android.communication.waitUntilDisplayed
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.util.UUID
import com.azure.android.communication.ui.R
import com.azure.android.communication.ui.calling.models.CallCompositeLobbyErrorCode
import java9.util.concurrent.CompletableFuture

internal class LobbyTest : BaseUiTest() {

    @Test
    fun testOnGridViewLobbyParticipantAreNotVisible() = runTest {
        injectDependencies(testScheduler)

        val expectedParticipantCountOnGridView = 2
        val expectedParticipantCountOnParticipantList = 3
        val expectedParticipantCountOnFloatingHeader = 2

        lobbyParticipantsVisibilityTests(
            expectedParticipantCountOnFloatingHeader,
            expectedParticipantCountOnGridView,
            expectedParticipantCountOnParticipantList
        )
    }

    @Test
    fun testOnGridViewAndParticipantListAllConnectedParticipantsAreVisible() = runTest {
        injectDependencies(testScheduler)

        val expectedParticipantCountOnGridView = 2
        val expectedParticipantCountOnParticipantList = 3
        val expectedParticipantCountOnFloatingHeader = 2

        lobbyParticipantsVisibilityTests(
            expectedParticipantCountOnFloatingHeader,
            expectedParticipantCountOnGridView,
            expectedParticipantCountOnParticipantList,
            false
        )
    }

    @Test
    fun testOnLobbyParticipantNotDisplayedIfParticipantRoleIsAttendee() = runTest {
        injectDependencies(testScheduler)
        // Launch the UI.
        joinTeamsCall()

        callingSDK.setParticipantRoleSharedFlow(CallCompositeParticipantRole.ATTENDEE)

        callingSDK.addRemoteParticipant(
            CommunicationIdentifier.CommunicationUserIdentifier("ACS User 2"),
            displayName = "ACS User 2",
            isMuted = false,
            isSpeaking = true,
            videoStreams = listOf(MediaStreamType.VIDEO),
            state = ParticipantState.IN_LOBBY
        )

        // assert lobby header is not displayed
        assertNotDisplayed(lobbyHeaderId)
    }

    @Test
    fun testOnLobbyParticipantAddLobbyHeaderIsDisplayed() = runTest {
        injectDependencies(testScheduler)
        // Launch the UI.
        val appContext = joinTeamsCall()

        callingSDK.setParticipantRoleSharedFlow(CallCompositeParticipantRole.PRESENTER)

        callingSDK.addRemoteParticipant(
            CommunicationIdentifier.CommunicationUserIdentifier("ACS User 2"),
            displayName = "ACS User 2",
            isMuted = false,
            isSpeaking = true,
            videoStreams = listOf(MediaStreamType.VIDEO),
            state = ParticipantState.IN_LOBBY
        )

        waitUntilDisplayed(lobbyHeaderId)

        // assert lobby header is displayed
        assertViewText(lobbyHeaderText, appContext!!.getString(R.string.azure_communication_ui_calling_lobby_header_text))

        callingSDK.removeParticipant("ACS User 2")

        // assert lobby header is not displayed
        assertNotDisplayed(lobbyHeaderId)

        callingSDK.addRemoteParticipant(
            CommunicationIdentifier.CommunicationUserIdentifier("ACS User 3"),
            displayName = "ACS User 3",
            isMuted = false,
            isSpeaking = true,
            videoStreams = listOf(MediaStreamType.VIDEO),
            state = ParticipantState.IN_LOBBY
        )

        waitUntilDisplayed(lobbyHeaderId)

        tapWhenDisplayed(lobbyHeaderOpenParticipantListButton)
        // one local + one remote + 2 texts (calling, lobby)
        assertViewHasChild(bottomDrawer, 4)
    }

    @Test
    fun testOnLobbyHeaderCloseButtonPressLobbyHeaderIsClosed() = runTest {
        injectDependencies(testScheduler)
        // launch the UI.
        joinTeamsCall()

        callingSDK.setParticipantRoleSharedFlow(CallCompositeParticipantRole.PRESENTER)

        callingSDK.addRemoteParticipant(
            CommunicationIdentifier.CommunicationUserIdentifier("ACS User 2"),
            displayName = "ACS User 2",
            isMuted = false,
            isSpeaking = true,
            videoStreams = listOf(MediaStreamType.VIDEO),
            state = ParticipantState.IN_LOBBY
        )

        waitUntilDisplayed(lobbyHeaderId)
        tapWhenDisplayed(lobbyHeaderCloseButton)
        assertNotDisplayed(lobbyHeaderId)
    }

    @Test
    fun testOnAdmitErrorLobbyErrorHeaderIsDisplayed() = runTest {
        injectDependencies(testScheduler)

        val buttonTextToClick = "Admit"

        // Launch the UI.
        val appContext = joinTeamsCall()

        lobbyButtonActionsTest(buttonTextToClick, appContext)
    }

    @Test
    fun testOnDeclineErrorLobbyErrorHeaderIsDisplayed() = runTest {
        injectDependencies(testScheduler)

        val buttonTextToClick = "Decline"

        // Launch the UI.
        val appContext = joinTeamsCall()

        lobbyButtonActionsTest(buttonTextToClick, appContext)
    }

    @Test
    fun testOnAdmitAllErrorLobbyErrorHeaderIsDisplayed() = runTest {
        injectDependencies(testScheduler)
        // Launch the UI.
        val appContext = joinTeamsCall()

        callingSDK.setParticipantRoleSharedFlow(CallCompositeParticipantRole.PRESENTER)

        callingSDK.addRemoteParticipant(
            CommunicationIdentifier.CommunicationUserIdentifier("ACS User 2"),
            displayName = "ACS User 2",
            isMuted = false,
            isSpeaking = true,
            videoStreams = listOf(MediaStreamType.VIDEO),
            state = ParticipantState.IN_LOBBY
        )

        waitUntilDisplayed(lobbyHeaderId)

        tapWhenDisplayed(lobbyHeaderOpenParticipantListButton)
        // one local + one remote + 2 texts (calling, lobby)
        assertViewHasChild(bottomDrawer, 4)

        val lobbyActionResult = CompletableFuture<CallCompositeLobbyErrorCode?>()
        lobbyActionResult.complete(CallCompositeLobbyErrorCode.LOBBY_MEETING_ROLE_NOT_ALLOWED)
        callingSDK.setLobbyResultCompletableFuture(lobbyActionResult)

        // click on admit all
        tapWithTextWhenDisplayed("Admit all")

        // to close participant list
        tapOnScreen()

        waitUntilDisplayed(lobbyErrorHeaderId)

        // assert error text
        assertViewText(
            lobbyErrorHeaderText,
            appContext!!.getString(R.string.azure_communication_ui_calling_error_lobby_meeting_role_not_allowded)
        )

        // close lobby error header
        tapWhenDisplayed(lobbyErrorHeaderCloseButton)

        // assert lobby error header is not displayed
        assertNotDisplayed(lobbyErrorHeaderId)
    }

    @Test
    fun testOnAdmitAllSuccess() = runTest {
        injectDependencies(testScheduler)
        // Launch the UI.
        joinTeamsCall()

        callingSDK.setParticipantRoleSharedFlow(CallCompositeParticipantRole.PRESENTER)

        callingSDK.addRemoteParticipant(
            CommunicationIdentifier.CommunicationUserIdentifier("ACS User 2"),
            displayName = "ACS User 2",
            isMuted = false,
            isSpeaking = true,
            videoStreams = listOf(MediaStreamType.VIDEO),
            state = ParticipantState.IN_LOBBY
        )

        waitUntilDisplayed(lobbyHeaderId)

        tapWhenDisplayed(lobbyHeaderOpenParticipantListButton)
        // one local + one remote + 2 texts (calling, lobby)
        assertViewHasChild(bottomDrawer, 4)

        val lobbyActionResult = CompletableFuture<CallCompositeLobbyErrorCode?>()
        callingSDK.setLobbyResultCompletableFuture(lobbyActionResult)

        // click on admit all
        tapWithTextWhenDisplayed("Admit all")

        // to close participant list
        tapOnScreen()

        callingSDK.changeParticipantState("ACS User 2", ParticipantState.CONNECTED)
        lobbyActionResult.complete(null)

        // assert lobby error header is not displayed
        assertNotDisplayed(lobbyErrorHeaderId)

        // tap on screen
        tapOnScreen()
        tapWhenDisplayed(participantListOpenButton)

        // one local + one remote + 1 texts (calling)
        assertViewHasChild(bottomDrawer, 3)
    }

    @Test
    fun testOnLobbyErrorMessageUIForRoleNotPermitted() = runTest {
        injectDependencies(testScheduler)

        val buttonTextToClick = "Admit"

        // Launch the UI.
        val appContext = joinTeamsCall()

        lobbyButtonActionsTest(
            buttonTextToClick, appContext,
            CallCompositeLobbyErrorCode.LOBBY_MEETING_ROLE_NOT_ALLOWED,
            R.string.azure_communication_ui_calling_error_lobby_meeting_role_not_allowded
        )
    }

    @Test
    fun testOnLobbyErrorMessageUIForMeetingTypeNotSupported() = runTest {
        injectDependencies(testScheduler)

        val buttonTextToClick = "Admit"

        // Launch the UI.
        val appContext = joinTeamsCall()

        lobbyButtonActionsTest(
            buttonTextToClick, appContext,
            CallCompositeLobbyErrorCode.LOBBY_CONVERSATION_TYPE_NOT_SUPPORTED,
            R.string.azure_communication_ui_calling_error_lobby_conversation_type_not_supported
        )
    }

    @Test
    fun testOnLobbyErrorMessageUIForFailedToRemove() = runTest {
        injectDependencies(testScheduler)

        val buttonTextToClick = "Admit"

        // Launch the UI.
        val appContext = joinTeamsCall()

        lobbyButtonActionsTest(
            buttonTextToClick, appContext,
            CallCompositeLobbyErrorCode.REMOVE_PARTICIPANT_OPERATION_FAILURE,
            R.string.azure_communication_ui_calling_error_lobby_failed_to_remove_participant
        )
    }

    @Test
    fun testOnLobbyErrorMessageUIForUnknownError() = runTest {
        injectDependencies(testScheduler)

        val buttonTextToClick = "Admit"

        // Launch the UI.
        val appContext = joinTeamsCall()

        lobbyButtonActionsTest(
            buttonTextToClick, appContext,
            CallCompositeLobbyErrorCode.UNKNOWN_ERROR,
            R.string.azure_communication_ui_calling_error_lobby_unknown
        )
    }

    @Test
    fun testOnLobbyErrorMessageUIForLobbyDisabledError() = runTest {
        injectDependencies(testScheduler)

        val buttonTextToClick = "Admit"

        // Launch the UI.
        val appContext = joinTeamsCall()

        lobbyButtonActionsTest(
            buttonTextToClick, appContext,
            CallCompositeLobbyErrorCode.LOBBY_DISABLED_BY_CONFIGURATIONS,
            R.string.azure_communication_ui_calling_error_lobby_disabled_by_configuration
        )
    }

    private suspend fun lobbyButtonActionsTest(
        buttonTextToClick: String,
        appContext: Context?,
        errorCode: CallCompositeLobbyErrorCode = CallCompositeLobbyErrorCode.LOBBY_MEETING_ROLE_NOT_ALLOWED,
        errorUIId: Int = R.string.azure_communication_ui_calling_error_lobby_meeting_role_not_allowded

    ) {
        callingSDK.setParticipantRoleSharedFlow(CallCompositeParticipantRole.PRESENTER)

        callingSDK.addRemoteParticipant(
            CommunicationIdentifier.CommunicationUserIdentifier("ACS User 2"),
            displayName = "ACS User 2",
            isMuted = false,
            isSpeaking = true,
            videoStreams = listOf(MediaStreamType.VIDEO),
            state = ParticipantState.IN_LOBBY
        )

        waitUntilDisplayed(lobbyHeaderId)

        tapWhenDisplayed(lobbyHeaderOpenParticipantListButton)
        // one local + one remote + 2 texts (calling, lobby)
        assertViewHasChild(bottomDrawer, 4)

        val lobbyActionResult = CompletableFuture<CallCompositeLobbyErrorCode?>()
        lobbyActionResult.complete(errorCode)
        callingSDK.setLobbyResultCompletableFuture(lobbyActionResult)

        tapWithTextWhenDisplayed("ACS User 2")

        tapWithTextWhenDisplayed(buttonTextToClick)

        // to close participant list
        tapOnScreen()

        waitUntilDisplayed(lobbyErrorHeaderId)

        // assert error text
        assertViewText(
            lobbyErrorHeaderText,
            appContext!!.getString(errorUIId)
        )

        // close lobby error header
        tapWhenDisplayed(lobbyErrorHeaderCloseButton)

        // assert lobby error header is not displayed
        assertNotDisplayed(lobbyErrorHeaderId)
    }

    private fun joinTeamsCall(): Context? {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val callComposite = CallCompositeBuilder().build()
        val communicationTokenRefreshOptions =
            CommunicationTokenRefreshOptions({ "token" }, true)
        val communicationTokenCredential =
            CommunicationTokenCredential(communicationTokenRefreshOptions)
        val remoteOptions =
            CallCompositeRemoteOptions(
                CallCompositeTeamsMeetingLinkLocator("https:teams.meeting"),
                communicationTokenCredential,
                "test"
            )

        callComposite.launchTest(appContext, remoteOptions, null)

        tapWhenDisplayed(joinCallId)
        waitUntilDisplayed(endCallId)
        return appContext
    }

    private suspend fun lobbyParticipantsVisibilityTests(
        expectedParticipantCountOnFloatingHeader: Int,
        expectedParticipantCountOnGridView: Int,
        expectedParticipantCountOnParticipantList: Int,
        addLobbyUser: Boolean = true
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

        callingSDK.addRemoteParticipant(
            CommunicationIdentifier.CommunicationUserIdentifier("ACS User 1"),
            displayName = "ACS User 1",
            isMuted = false,
            isSpeaking = true,
            videoStreams = listOf(MediaStreamType.VIDEO)
        )

        callingSDK.addRemoteParticipant(
            CommunicationIdentifier.CommunicationUserIdentifier("ACS User 2"),
            displayName = "ACS User 2",
            isMuted = false,
            isSpeaking = true,
            videoStreams = listOf(MediaStreamType.VIDEO)
        )

        if (addLobbyUser) {
            callingSDK.addRemoteParticipant(
                CommunicationIdentifier.CommunicationUserIdentifier("Lobby State"),
                displayName = "Lobby State",
                state = ParticipantState.IN_LOBBY,
                isMuted = false,
                isSpeaking = true,
                videoStreams = listOf(MediaStreamType.VIDEO)
            )
        }

        waitUntilDisplayed(participantContainerId)

        assertViewText(
            participantCountId,
            "Call with $expectedParticipantCountOnFloatingHeader people"
        )

        assertViewHasChild(participantContainerId, expectedParticipantCountOnGridView)

        tapWhenDisplayed(participantListOpenButton)

        // 1 local
        assertViewHasChild(bottomDrawer, expectedParticipantCountOnParticipantList + 1)
    }
}
