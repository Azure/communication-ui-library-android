// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.presentation.fragment.calling.participantlist

import com.azure.android.communication.ui.ACSBaseTestCoroutine
import com.azure.android.communication.ui.calling.models.ParticipantInfoModel
import com.azure.android.communication.ui.calling.models.ParticipantStatus
import com.azure.android.communication.ui.calling.models.StreamType
import com.azure.android.communication.ui.calling.models.VideoStreamModel
import com.azure.android.communication.ui.calling.presentation.fragment.calling.lobby.LobbyHeaderViewModel
import com.azure.android.communication.ui.calling.redux.state.CallingStatus
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
internal class LobbyHeaderViewModelUnitTest : ACSBaseTestCoroutine() {

    @Test
    fun lobbyHeaderViewModelUnitTest_update_then_showHeaderIfStateIsConnectedAndParticipantExists() {
        runScopedTest {
            // arrange
            val lobbyHeaderViewModel = LobbyHeaderViewModel()
            val initialRemoteParticipantsMap: MutableMap<String, ParticipantInfoModel> =
                mutableMapOf()
            initialRemoteParticipantsMap["user1"] = getParticipantInfoModel(
                "user one",
                "user1",
                isMuted = true,
                isSpeaking = true,
                cameraVideoStreamModel = VideoStreamModel("video_stream_1", StreamType.VIDEO),
                modifiedTimestamp = 456,
                speakingTimestamp = 567,
            )
            val resultHeaderStateFlow =
                mutableListOf<Boolean?>()

            lobbyHeaderViewModel.init(
                CallingStatus.CONNECTED,
                initialRemoteParticipantsMap,
                true
            )

            val displayJob = launch {
                lobbyHeaderViewModel.getDisplayLobbyHeaderFlow()
                    .toList(resultHeaderStateFlow)
            }

            // act
            lobbyHeaderViewModel.update(
                CallingStatus.CONNECTED,
                initialRemoteParticipantsMap,
                true
            )
            lobbyHeaderViewModel.update(
                CallingStatus.CONNECTED,
                initialRemoteParticipantsMap,
                true
            )

            // assert
            Assert.assertEquals(
                true,
                resultHeaderStateFlow[0]
            )

            Assert.assertEquals(
                1,
                resultHeaderStateFlow.size
            )

            displayJob.cancel()
        }
    }

    @Test
    fun lobbyHeaderViewModelUnitTest_update_then_showHeaderIfStateIsConnected_andNewParticipantIsAdded() {
        runScopedTest {

            // arrange
            val lobbyHeaderViewModel = LobbyHeaderViewModel()
            val initialRemoteParticipantsMap: MutableMap<String, ParticipantInfoModel> =
                mutableMapOf()
            initialRemoteParticipantsMap["user1"] = getParticipantInfoModel(
                "user one",
                "user1",
                isMuted = true,
                isSpeaking = true,
                cameraVideoStreamModel = VideoStreamModel("video_stream_1", StreamType.VIDEO),
                modifiedTimestamp = 456,
                speakingTimestamp = 567,
            )
            val resultHeaderStateFlow =
                mutableListOf<Boolean?>()

            lobbyHeaderViewModel.init(
                CallingStatus.CONNECTED,
                initialRemoteParticipantsMap,
                true
            )

            val displayJob = launch {
                lobbyHeaderViewModel.getDisplayLobbyHeaderFlow()
                    .toList(resultHeaderStateFlow)
            }

            // act
            lobbyHeaderViewModel.close()
            lobbyHeaderViewModel.update(
                CallingStatus.CONNECTED,
                initialRemoteParticipantsMap.toMutableMap(),
                true
            )
            initialRemoteParticipantsMap["user2"] = getParticipantInfoModel(
                "user 2",
                "user2",
                isMuted = true,
                isSpeaking = true,
                cameraVideoStreamModel = VideoStreamModel("video_stream_1", StreamType.VIDEO),
                modifiedTimestamp = 456,
                speakingTimestamp = 567,
            )
            lobbyHeaderViewModel.update(
                CallingStatus.CONNECTED,
                initialRemoteParticipantsMap.toMutableMap(),
                true
            )

            // assert
            Assert.assertEquals(
                true,
                resultHeaderStateFlow[0]
            )

            Assert.assertEquals(
                false,
                resultHeaderStateFlow[1]
            )

            Assert.assertEquals(
                true,
                resultHeaderStateFlow[2]
            )

            Assert.assertEquals(
                3,
                resultHeaderStateFlow.size
            )

            displayJob.cancel()
        }
    }

    @Test
    fun lobbyHeaderViewModelUnitTest_update_then_ifStateIsConnected_showLobbyIsFalse_lobbyHeaderNotDisplayed() {
        runScopedTest {

            // arrange
            val lobbyHeaderViewModel = LobbyHeaderViewModel()
            val initialRemoteParticipantsMap: MutableMap<String, ParticipantInfoModel> =
                mutableMapOf()
            initialRemoteParticipantsMap["user1"] = getParticipantInfoModel(
                "user one",
                "user1",
                isMuted = true,
                isSpeaking = true,
                cameraVideoStreamModel = VideoStreamModel("video_stream_1", StreamType.VIDEO),
                modifiedTimestamp = 456,
                speakingTimestamp = 567,
            )
            val resultHeaderStateFlow =
                mutableListOf<Boolean?>()

            lobbyHeaderViewModel.init(
                CallingStatus.CONNECTED,
                initialRemoteParticipantsMap,
                false
            )

            val displayJob = launch {
                lobbyHeaderViewModel.getDisplayLobbyHeaderFlow()
                    .toList(resultHeaderStateFlow)
            }

            // act
            lobbyHeaderViewModel.update(
                CallingStatus.CONNECTED,
                initialRemoteParticipantsMap.toMutableMap(),
                false
            )
            initialRemoteParticipantsMap["user2"] = getParticipantInfoModel(
                "user one",
                "user1",
                isMuted = true,
                isSpeaking = true,
                cameraVideoStreamModel = VideoStreamModel("video_stream_1", StreamType.VIDEO),
                modifiedTimestamp = 456,
                speakingTimestamp = 567,
            )
            lobbyHeaderViewModel.update(
                CallingStatus.CONNECTED,
                initialRemoteParticipantsMap,
                false
            )

            // assert
            Assert.assertEquals(
                false,
                resultHeaderStateFlow[0]
            )

            Assert.assertEquals(
                1,
                resultHeaderStateFlow.size
            )

            displayJob.cancel()
        }
    }

    @Test
    fun lobbyHeaderViewModelUnitTest_update_then_ifStateIsNotConnected_showLobbyIsTrue_lobbyHeaderNotDisplayed() {
        runScopedTest {

            // arrange
            val lobbyHeaderViewModel = LobbyHeaderViewModel()
            val initialRemoteParticipantsMap: MutableMap<String, ParticipantInfoModel> =
                mutableMapOf()
            initialRemoteParticipantsMap["user1"] = getParticipantInfoModel(
                "user one",
                "user1",
                isMuted = true,
                isSpeaking = true,
                cameraVideoStreamModel = VideoStreamModel("video_stream_1", StreamType.VIDEO),
                modifiedTimestamp = 456,
                speakingTimestamp = 567,
            )
            val resultHeaderStateFlow =
                mutableListOf<Boolean?>()

            lobbyHeaderViewModel.init(
                CallingStatus.DISCONNECTED,
                initialRemoteParticipantsMap,
                true
            )

            val displayJob = launch {
                lobbyHeaderViewModel.getDisplayLobbyHeaderFlow()
                    .toList(resultHeaderStateFlow)
            }

            // act
            lobbyHeaderViewModel.update(
                CallingStatus.CONNECTING,
                initialRemoteParticipantsMap.toMutableMap(),
                true
            )
            initialRemoteParticipantsMap["user2"] = getParticipantInfoModel(
                "user one",
                "user1",
                isMuted = true,
                isSpeaking = true,
                cameraVideoStreamModel = VideoStreamModel("video_stream_1", StreamType.VIDEO),
                modifiedTimestamp = 456,
                speakingTimestamp = 567,
            )
            lobbyHeaderViewModel.update(
                CallingStatus.EARLY_MEDIA,
                initialRemoteParticipantsMap,
                true
            )

            // assert
            Assert.assertEquals(
                false,
                resultHeaderStateFlow[0]
            )

            Assert.assertEquals(
                1,
                resultHeaderStateFlow.size
            )

            displayJob.cancel()
        }
    }

    @Test
    fun lobbyHeaderViewModelUnitTest_update_then_ifStateIsConnected_showLobbyIsTrue_participantsEmpty_lobbyHeaderNotDisplayed() {
        runScopedTest {
            // arrange
            val lobbyHeaderViewModel = LobbyHeaderViewModel()
            val initialRemoteParticipantsMap: MutableMap<String, ParticipantInfoModel> =
                mutableMapOf()
            val resultHeaderStateFlow =
                mutableListOf<Boolean?>()

            lobbyHeaderViewModel.init(
                CallingStatus.CONNECTED,
                initialRemoteParticipantsMap,
                true
            )

            val displayJob = launch {
                lobbyHeaderViewModel.getDisplayLobbyHeaderFlow()
                    .toList(resultHeaderStateFlow)
            }

            // act
            lobbyHeaderViewModel.update(
                CallingStatus.CONNECTED,
                initialRemoteParticipantsMap,
                true
            )
            lobbyHeaderViewModel.update(
                CallingStatus.CONNECTED,
                initialRemoteParticipantsMap,
                true
            )

            // assert
            Assert.assertEquals(
                false,
                resultHeaderStateFlow[0]
            )

            Assert.assertEquals(
                1,
                resultHeaderStateFlow.size
            )

            displayJob.cancel()
        }
    }

    private fun getParticipantInfoModel(
        displayName: String,
        userIdentifier: String,
        isMuted: Boolean,
        isSpeaking: Boolean,
        screenShareVideoStreamModel: VideoStreamModel? = null,
        cameraVideoStreamModel: VideoStreamModel? = null,
        modifiedTimestamp: Number,
        speakingTimestamp: Number,
        status: ParticipantStatus = ParticipantStatus.CONNECTED,
    ) = ParticipantInfoModel(
        displayName,
        userIdentifier,
        isMuted,
        isSpeaking,
        status,
        screenShareVideoStreamModel,
        cameraVideoStreamModel,
        modifiedTimestamp,
    )
}
