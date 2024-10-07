// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.calling.header

import com.azure.android.communication.ui.calling.ACSBaseTestCoroutine
import com.azure.android.communication.ui.calling.models.ParticipantInfoModel
import com.azure.android.communication.ui.calling.redux.state.AppReduxState
import com.azure.android.communication.ui.calling.redux.state.CallScreenInfoHeaderState
import com.azure.android.communication.ui.calling.redux.state.CallingState
import com.azure.android.communication.ui.calling.redux.state.CallingStatus
import com.azure.android.communication.ui.calling.redux.state.RemoteParticipantsState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.mock

@RunWith(MockitoJUnitRunner::class)
internal class InfoHeaderViewModelUnitTest : ACSBaseTestCoroutine() {

    @ExperimentalCoroutinesApi
    @Test
    fun floatingHeaderViewModel_update_then_numberOfParticipantsFlowReflectsUpdate() {
        runScopedTest {

            val appState = AppReduxState("", false, false)

            val participantInfoModel1 = mock<ParticipantInfoModel> {}
            val participantInfoModel2 = mock<ParticipantInfoModel> {}
            val participantInfoModel3 = mock<ParticipantInfoModel> {}
            val expectedParticipantMap: Map<String, ParticipantInfoModel> = mapOf(
                "p1" to participantInfoModel1,
                "p2" to participantInfoModel2,
                "p3" to participantInfoModel3
            )
            val timestamp: Number = System.currentTimeMillis()

            appState.remoteParticipantState = RemoteParticipantsState(
                expectedParticipantMap,
                timestamp,
                listOf(),
                0,
                lobbyErrorCode = null,
                totalParticipantCount = 0,
            )
            appState.callState = CallingState(
                CallingStatus.CONNECTED,
                joinCallIsRequested = false,
                isRecording = false,
                isTranscribing = false
            )

            val floatingHeaderViewModel = InfoHeaderViewModel(false, mock(), mock())
            floatingHeaderViewModel.init(
                appState.callState.callingStatus,
                expectedParticipantMap.count(),
                CallScreenInfoHeaderState(null, null),
                appState.buttonState,
            ) { }

            val resultListFromNumberOfParticipantsFlow =
                mutableListOf<Int>()

            val flowJob = launch {
                floatingHeaderViewModel.getNumberOfParticipantsFlow()
                    .toList(resultListFromNumberOfParticipantsFlow)
            }

            // act
            floatingHeaderViewModel.update(
                expectedParticipantMap.count(),
                CallScreenInfoHeaderState(null, null),
                appState.buttonState,
            )

            // assert
            Assert.assertEquals(
                expectedParticipantMap.size,
                resultListFromNumberOfParticipantsFlow[0]
            )

            flowJob.cancel()
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun floatingHeaderViewModel_updateIsLobbyOverlayDisplayed_then_isLobbyOverlayDisplayedFlowReflectsUpdate() {
        runScopedTest {

            val appState = AppReduxState("", false, false)

            val participantInfoModel1 = mock<ParticipantInfoModel> {}
            val participantInfoModel2 = mock<ParticipantInfoModel> {}
            val participantInfoModel3 = mock<ParticipantInfoModel> {}
            val expectedParticipantMap: Map<String, ParticipantInfoModel> = mapOf(
                "p1" to participantInfoModel1,
                "p2" to participantInfoModel2,
                "p3" to participantInfoModel3
            )
            val timestamp: Number = System.currentTimeMillis()

            appState.remoteParticipantState = RemoteParticipantsState(
                expectedParticipantMap,
                timestamp,
                listOf(),
                3,
                lobbyErrorCode = null,
                totalParticipantCount = 0,
            )
            appState.callState = CallingState(
                CallingStatus.CONNECTED,
                joinCallIsRequested = false,
                isRecording = false,
                isTranscribing = false
            )

            val floatingHeaderViewModel = InfoHeaderViewModel(false, mock(), mock())
            floatingHeaderViewModel.init(
                appState.callState.callingStatus,
                expectedParticipantMap.count(),
                CallScreenInfoHeaderState(null, null),
                appState.buttonState,
            ) {}

            val resultListFromIsLobbyOverlayDisplayedFlow =
                mutableListOf<Boolean>()

            val flowJob = launch {
                floatingHeaderViewModel.getIsOverlayDisplayedFlow()
                    .toList(resultListFromIsLobbyOverlayDisplayedFlow)
            }

            // act
            floatingHeaderViewModel.updateIsOverlayDisplayed(CallingStatus.CONNECTED)
            floatingHeaderViewModel.updateIsOverlayDisplayed(CallingStatus.IN_LOBBY)

            // assert
            Assert.assertEquals(
                false,
                resultListFromIsLobbyOverlayDisplayedFlow[0]
            )

            Assert.assertEquals(
                true,
                resultListFromIsLobbyOverlayDisplayedFlow[1]
            )

            flowJob.cancel()
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun floatingHeaderViewModel_update_then_displayTitleAndSubtitle() {
        runScopedTest {
            val appState = AppReduxState("", false, false)

            val participantInfoModel1 = mock<ParticipantInfoModel> {}
            val participantInfoModel2 = mock<ParticipantInfoModel> {}
            val participantInfoModel3 = mock<ParticipantInfoModel> {}
            val expectedParticipantMap: Map<String, ParticipantInfoModel> = mapOf(
                "p1" to participantInfoModel1,
                "p2" to participantInfoModel2,
                "p3" to participantInfoModel3
            )
            val timestamp: Number = System.currentTimeMillis()

            appState.remoteParticipantState = RemoteParticipantsState(
                expectedParticipantMap,
                timestamp,
                listOf(),
                0,
                lobbyErrorCode = null,
                totalParticipantCount = 0,
            )
            appState.callState = CallingState(
                CallingStatus.CONNECTED,
                joinCallIsRequested = false,
                isRecording = false,
                isTranscribing = false
            )
            val title = "title"
            val subtitle = "subtitle"

            val floatingHeaderViewModel = InfoHeaderViewModel(false, mock(), mock())
            floatingHeaderViewModel.init(
                appState.callState.callingStatus,
                expectedParticipantMap.count(),
                CallScreenInfoHeaderState(title, subtitle),
                appState.buttonState,
            ) { }

            val resultListFromNumberOfParticipantsFlow =
                mutableListOf<Int>()
            val resultListFromTitleStateFlow =
                mutableListOf<String?>()
            val resultListFromSubtitleStateFlow =
                mutableListOf<String?>()

            val flowJobParticipant = launch {
                floatingHeaderViewModel.getNumberOfParticipantsFlow()
                    .toList(resultListFromNumberOfParticipantsFlow)
            }

            val flowJobTitle = launch {
                floatingHeaderViewModel.getTitleStateFlow()
                    .toList(resultListFromTitleStateFlow)
            }

            val flowJobSubtitle = launch {
                floatingHeaderViewModel.getSubtitleStateFlow()
                    .toList(resultListFromSubtitleStateFlow)
            }

            // act
            floatingHeaderViewModel.update(
                expectedParticipantMap.count(),
                CallScreenInfoHeaderState(null, null),
                appState.buttonState,
            )

            // assert
            Assert.assertEquals(
                expectedParticipantMap.size,
                resultListFromNumberOfParticipantsFlow[0]
            )

            Assert.assertEquals(
                title,
                resultListFromTitleStateFlow[0]
            )

            Assert.assertEquals(
                subtitle,
                resultListFromSubtitleStateFlow[0]
            )

            flowJobParticipant.cancel()
            flowJobTitle.cancel()
            flowJobSubtitle.cancel()
        }
    }
    /* </CUSTOM_CALL_HEADER> */
}
