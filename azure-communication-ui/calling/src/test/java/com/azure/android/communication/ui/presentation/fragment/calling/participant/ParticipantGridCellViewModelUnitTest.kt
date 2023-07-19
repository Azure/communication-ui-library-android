// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.presentation.fragment.calling.participant

import com.azure.android.communication.ui.calling.models.ParticipantInfoModel
import com.azure.android.communication.ui.calling.models.StreamType
import com.azure.android.communication.ui.calling.models.VideoStreamModel
import com.azure.android.communication.ui.calling.presentation.fragment.calling.participant.grid.ParticipantGridCellViewModel
import com.azure.android.communication.ui.calling.presentation.fragment.calling.participant.grid.ParticipantGridViewModel
import com.azure.android.communication.ui.calling.presentation.fragment.calling.participant.grid.VideoViewModel
import com.azure.android.communication.ui.calling.presentation.fragment.factories.ParticipantGridCellViewModelFactory
import com.azure.android.communication.ui.ACSBaseTestCoroutine
import com.azure.android.communication.ui.calling.models.ParticipantStatus
import com.azure.android.communication.ui.calling.redux.state.PictureInPictureStatus
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
internal class ParticipantGridCellViewModelUnitTest : ACSBaseTestCoroutine() {

    @ExperimentalCoroutinesApi
    @Test
    fun participantViewModel_update_when_isCalledWithParticipantInfoModel_then_participantGridViewModelReceiveStateChange() =
        runScopedTest {

            // arrange
            val participantGridViewModel = getParticipantGridViewModel()
            val remoteParticipantsMap: MutableMap<String, ParticipantInfoModel> = mutableMapOf()
            remoteParticipantsMap["user1"] = getParticipantInfoModel(
                "user one",
                "user1",
                isMuted = true,
                isSpeaking = true,
                cameraVideoStreamModel = VideoStreamModel("video", StreamType.VIDEO),
                modifiedTimestamp = 456,
                speakingTimestamp = 567
            )

            val emitResultFromRemoteParticipantsSharedFlow =
                mutableListOf<List<ParticipantGridCellViewModel>>()

            val flowJob = launch {
                participantGridViewModel.getRemoteParticipantsUpdateStateFlow()
                    .toList(emitResultFromRemoteParticipantsSharedFlow)
            }

            val pipStatus = PictureInPictureStatus.NONE

            // act
            participantGridViewModel.update(234, remoteParticipantsMap.toMutableMap(), listOf(), 0, pipStatus)

            // assert
            val participantViewModel = emitResultFromRemoteParticipantsSharedFlow[1][0]

            Assert.assertEquals("user one", participantViewModel.getDisplayNameStateFlow().value)
            Assert.assertEquals("user1", participantViewModel.getParticipantUserIdentifier())
            Assert.assertEquals(
                "video",
                participantViewModel.getVideoViewModelStateFlow().value!!.videoStreamID
            )
            Assert.assertEquals(true, participantViewModel.getIsMutedStateFlow().value)
            Assert.assertEquals(false, participantViewModel.getIsSpeakingStateFlow().value)
            Assert.assertEquals(456, participantViewModel.getParticipantModifiedTimestamp())

            flowJob.cancel()
        }

    @ExperimentalCoroutinesApi
    @Test
    fun participantViewModel_update_when_isCalledWithParticipantInfoModel_then_participantGridViewModelReceiveStateChangeForSameParticipantWithNewModifiedTimestamp() =
        runScopedTest {

            // arrange
            val participantGridViewModel =
                getParticipantGridViewModel()
            val remoteParticipantsMap: MutableMap<String, ParticipantInfoModel> = mutableMapOf()
            remoteParticipantsMap["user1"] = getParticipantInfoModel(
                "user one",
                "user1",
                isMuted = true,
                isSpeaking = true,
                cameraVideoStreamModel = VideoStreamModel("video", StreamType.VIDEO),
                modifiedTimestamp = 456,
                speakingTimestamp = 567
            )

            val emitResultFromRemoteParticipantsSharedFlow =
                mutableListOf<List<ParticipantGridCellViewModel>>()

            val flowJob = launch {
                participantGridViewModel.getRemoteParticipantsUpdateStateFlow()
                    .toList(emitResultFromRemoteParticipantsSharedFlow)
            }
            val pipStatus = PictureInPictureStatus.NONE

            // act
            participantGridViewModel.update(234, remoteParticipantsMap.toMutableMap(), listOf(), 0, pipStatus)
            remoteParticipantsMap["user1"]!!.modifiedTimestamp = 555
            remoteParticipantsMap["user1"]!!.isMuted = false

            // assert
            val participantViewModel = emitResultFromRemoteParticipantsSharedFlow[1][0]

            Assert.assertEquals("user one", participantViewModel.getDisplayNameStateFlow().value)
            Assert.assertEquals("user1", participantViewModel.getParticipantUserIdentifier())
            Assert.assertEquals(
                "video",
                participantViewModel.getVideoViewModelStateFlow().value!!.videoStreamID
            )
            Assert.assertEquals(true, participantViewModel.getIsMutedStateFlow().value)
            Assert.assertEquals(false, participantViewModel.getIsSpeakingStateFlow().value)
            Assert.assertEquals(456, participantViewModel.getParticipantModifiedTimestamp())

            participantGridViewModel.update(236, remoteParticipantsMap.toMutableMap(), listOf(), 0, pipStatus)

            // assert state flows
            Assert.assertEquals("user one", participantViewModel.getDisplayNameStateFlow().value)
            Assert.assertEquals(
                "video",
                participantViewModel.getVideoViewModelStateFlow().value?.videoStreamID
            )
            Assert.assertEquals(false, participantViewModel.getIsMutedStateFlow().value)
            Assert.assertEquals(true, participantViewModel.getIsSpeakingStateFlow().value)

            flowJob.cancel()
        }

    @ExperimentalCoroutinesApi
    @Test
    fun participantViewModel_update_when_isCalledWithParticipantInfoModel_then_participantGridViewModelReceiveStateChangeForSameParticipantWithSameModifiedTimestamp() =
        runScopedTest {
            // arrange
            val participantGridViewModel = getParticipantGridViewModel()
            val remoteParticipantsMap: MutableMap<String, ParticipantInfoModel> = mutableMapOf()
            remoteParticipantsMap["user1"] = getParticipantInfoModel(
                "user one",
                "user1",
                isMuted = true,
                isSpeaking = true,
                cameraVideoStreamModel = VideoStreamModel("video", StreamType.VIDEO),
                modifiedTimestamp = 456,
                speakingTimestamp = 567
            )

            val emitResultFromRemoteParticipantsSharedFlow =
                mutableListOf<List<ParticipantGridCellViewModel>>()

            val flowJob = launch {
                participantGridViewModel.getRemoteParticipantsUpdateStateFlow()
                    .toList(emitResultFromRemoteParticipantsSharedFlow)
            }
            val pipStatus = PictureInPictureStatus.NONE

            // act
            participantGridViewModel.update(234, remoteParticipantsMap.toMutableMap(), listOf(), 0, pipStatus)
            remoteParticipantsMap["user1"]!!.modifiedTimestamp = 456
            remoteParticipantsMap["user1"]!!.isSpeaking = false
            participantGridViewModel.update(236, remoteParticipantsMap.toMutableMap(), listOf(), 0, pipStatus)

            // assert
            val participantViewModel = emitResultFromRemoteParticipantsSharedFlow[1][0]

            Assert.assertEquals("user one", participantViewModel.getDisplayNameStateFlow().value)
            Assert.assertEquals("user1", participantViewModel.getParticipantUserIdentifier())
            Assert.assertEquals(
                "video",
                participantViewModel.getVideoViewModelStateFlow().value!!.videoStreamID
            )
            Assert.assertEquals(true, participantViewModel.getIsMutedStateFlow().value)
            Assert.assertEquals(false, participantViewModel.getIsSpeakingStateFlow().value)
            Assert.assertEquals(456, participantViewModel.getParticipantModifiedTimestamp())

            // assert state flows
            Assert.assertEquals("user one", participantViewModel.getDisplayNameStateFlow().value)
            Assert.assertEquals(
                "video",
                participantViewModel.getVideoViewModelStateFlow().value?.videoStreamID
            )
            Assert.assertEquals(true, participantViewModel.getIsMutedStateFlow().value)
            Assert.assertEquals(false, participantViewModel.getIsSpeakingStateFlow().value)

            flowJob.cancel()
        }

    private fun getParticipantGridViewModel() = ParticipantGridViewModel(
        ParticipantGridCellViewModelFactory(),
        6
    )

    @ExperimentalCoroutinesApi
    @Test
    fun participantViewModel_when_created_then_notifyAllStateChange() =
        runScopedTest {
            // arrange
            val emitResultDisplayName = mutableListOf<String>()
            val emitResultIsMuted = mutableListOf<Boolean>()
            val emitResultIsSpeaking = mutableListOf<Boolean>()
            val emitResultVideoStreamModel = mutableListOf<VideoViewModel?>()

            // act
            val participantViewModel = ParticipantGridCellViewModel(
                "user one",
                "user1",
                isMuted = true,
                isSpeaking = true,
                cameraVideoStreamModel = VideoStreamModel("video", StreamType.VIDEO),
                screenShareVideoStreamModel = null,
                modifiedTimestamp = 456,
                participantStatus = null,
            )

            val flowJobDisplayName = launch {
                participantViewModel.getDisplayNameStateFlow()
                    .toList(emitResultDisplayName)
            }

            val flowJobMuted = launch {
                participantViewModel.getIsMutedStateFlow()
                    .toList(emitResultIsMuted)
            }

            val flowJobSpeaking = launch {
                participantViewModel.getIsSpeakingStateFlow()
                    .toList(emitResultIsSpeaking)
            }

            val flowJobVideoStream = launch {
                participantViewModel.getVideoViewModelStateFlow()
                    .toList(emitResultVideoStreamModel)
            }

            // assert
            Assert.assertEquals("user1", emitResultDisplayName[0])
            Assert.assertEquals(true, emitResultIsMuted[0])
            Assert.assertEquals(false, emitResultIsSpeaking[0])
            Assert.assertEquals("video", emitResultVideoStreamModel[0]!!.videoStreamID)

            flowJobDisplayName.cancel()
            flowJobMuted.cancel()
            flowJobSpeaking.cancel()
            flowJobVideoStream.cancel()
        }

    @ExperimentalCoroutinesApi
    @Test
    fun participantViewModel_when_created_then_notifyStateChangeWithAudioViewIfParticipantHasNoVideo() =
        runScopedTest {
            // arrange
            val emitResultDisplayName = mutableListOf<String>()
            val emitResultIsMuted = mutableListOf<Boolean>()
            val emitResultIsSpeaking = mutableListOf<Boolean>()
            val emitResultVideoStreamModel = mutableListOf<VideoViewModel?>()

            // act
            val participantViewModel = ParticipantGridCellViewModel(
                "user one",
                "user1",
                isMuted = true,
                isSpeaking = true,
                cameraVideoStreamModel = null,
                screenShareVideoStreamModel = null,
                modifiedTimestamp = 456,
                participantStatus = null,
            )

            val flowJobDisplayName = launch {
                participantViewModel.getDisplayNameStateFlow()
                    .toList(emitResultDisplayName)
            }

            val flowJobMuted = launch {
                participantViewModel.getIsMutedStateFlow()
                    .toList(emitResultIsMuted)
            }

            val flowJobSpeaking = launch {
                participantViewModel.getIsSpeakingStateFlow()
                    .toList(emitResultIsSpeaking)
            }

            val flowJobVideoStream = launch {
                participantViewModel.getVideoViewModelStateFlow()
                    .toList(emitResultVideoStreamModel)
            }

            // assert
            Assert.assertEquals("user1", emitResultDisplayName[0])
            Assert.assertEquals(true, emitResultIsMuted[0])
            Assert.assertEquals(false, emitResultIsSpeaking[0])
            Assert.assertEquals(null, emitResultVideoStreamModel[0])

            flowJobDisplayName.cancel()
            flowJobMuted.cancel()
            flowJobSpeaking.cancel()
            flowJobVideoStream.cancel()
        }

    @ExperimentalCoroutinesApi
    @Test
    fun participantViewModel_when_created_then_notifyStateChangeWithVideoViewIfParticipantHasVideo() =
        runScopedTest {
            // arrange
            val participantViewModel = ParticipantGridCellViewModel(
                "user one",
                "user1",
                isMuted = true,
                isSpeaking = true,
                cameraVideoStreamModel = null,
                screenShareVideoStreamModel = null,
                modifiedTimestamp = 456,
                participantStatus = null,
            )

            val emitResultDisplayName = mutableListOf<String>()
            val emitResultIsMuted = mutableListOf<Boolean>()
            val emitResultIsSpeaking = mutableListOf<Boolean>()
            val emitResultVideoStreamModel = mutableListOf<VideoViewModel?>()

            val flowJobDisplayName = launch {
                participantViewModel.getDisplayNameStateFlow()
                    .toList(emitResultDisplayName)
            }

            val flowJobMuted = launch {
                participantViewModel.getIsMutedStateFlow()
                    .toList(emitResultIsMuted)
            }

            val flowJobSpeaking = launch {
                participantViewModel.getIsSpeakingStateFlow()
                    .toList(emitResultIsSpeaking)
            }

            val flowJobVideoStream = launch {
                participantViewModel.getVideoViewModelStateFlow()
                    .toList(emitResultVideoStreamModel)
            }

            // act
            participantViewModel.update(
                getParticipantInfoModel(
                    "user1",
                    "user1",
                    isMuted = false,
                    isSpeaking = false,
                    screenShareVideoStreamModel = null,
                    cameraVideoStreamModel = VideoStreamModel(
                        "video",
                        StreamType.VIDEO
                    ),
                    modifiedTimestamp = 456,
                    speakingTimestamp = 567
                ),
            )

            // assert
            Assert.assertEquals("user1", emitResultDisplayName[0])
            Assert.assertEquals(true, emitResultIsMuted[0])
            Assert.assertEquals(false, emitResultIsSpeaking[0])
            Assert.assertEquals(null, emitResultVideoStreamModel[0])

            // assert updates
            Assert.assertEquals(1, emitResultDisplayName.size)
            Assert.assertEquals(false, emitResultIsMuted[1])
            Assert.assertEquals(false, emitResultIsSpeaking[0])
            Assert.assertEquals("video", emitResultVideoStreamModel[1]?.videoStreamID)

            flowJobDisplayName.cancel()
            flowJobMuted.cancel()
            flowJobSpeaking.cancel()
            flowJobVideoStream.cancel()
        }

    @ExperimentalCoroutinesApi
    @Test
    fun participantViewModel_when_created_then_notifyStateChangeWithVideoViewIfParticipantHasScreenShare() =
        runScopedTest {
            // arrange
            val participantViewModel = ParticipantGridCellViewModel(
                "user one",
                "user1",
                isMuted = true,
                isSpeaking = true,
                screenShareVideoStreamModel = null,
                cameraVideoStreamModel = null,
                modifiedTimestamp = 456,
                participantStatus = null,
            )

            val emitResultDisplayName = mutableListOf<String>()
            val emitResultIsMuted = mutableListOf<Boolean>()
            val emitResultIsSpeaking = mutableListOf<Boolean>()
            val emitResultVideoStreamModel = mutableListOf<VideoViewModel?>()

            val flowJobDisplayName = launch {
                participantViewModel.getDisplayNameStateFlow()
                    .toList(emitResultDisplayName)
            }

            val flowJobMuted = launch {
                participantViewModel.getIsMutedStateFlow()
                    .toList(emitResultIsMuted)
            }

            val flowJobSpeaking = launch {
                participantViewModel.getIsSpeakingStateFlow()
                    .toList(emitResultIsSpeaking)
            }

            val flowJobVideoStream = launch {
                participantViewModel.getVideoViewModelStateFlow()
                    .toList(emitResultVideoStreamModel)
            }

            // act
            participantViewModel.update(
                getParticipantInfoModel(
                    "user1",
                    "user1",
                    isMuted = false,
                    isSpeaking = false,
                    VideoStreamModel(
                        "screen",
                        StreamType.SCREEN_SHARING
                    ),
                    VideoStreamModel(
                        "video",
                        StreamType.VIDEO
                    ),
                    modifiedTimestamp = 456,
                    speakingTimestamp = 567
                ),
            )

            // assert
            Assert.assertEquals("user1", emitResultDisplayName[0])
            Assert.assertEquals(true, emitResultIsMuted[0])
            Assert.assertEquals(false, emitResultIsSpeaking[0])
            Assert.assertEquals(null, emitResultVideoStreamModel[0])

            // assert updates
            Assert.assertEquals(1, emitResultDisplayName.size)
            Assert.assertEquals(false, emitResultIsMuted[1])
            Assert.assertEquals(false, emitResultIsSpeaking[0])
            Assert.assertEquals("screen", emitResultVideoStreamModel[1]?.videoStreamID)

            flowJobDisplayName.cancel()
            flowJobMuted.cancel()
            flowJobSpeaking.cancel()
            flowJobVideoStream.cancel()
        }

    @ExperimentalCoroutinesApi
    @Test
    fun participantViewModel_created_with_blankUsername_checkIfNameAndMicIndicator_should_be_displayed() =
        runScopedTest {
            // arrange
            val emitResultDisplayName = mutableListOf<String>()
            val emitResultIsMuted = mutableListOf<Boolean>()
            val emitResultIsNameIndicatorVisibleStateFlow = mutableListOf<Boolean>()

            // act
            val participantViewModel = ParticipantGridCellViewModel(
                "user one",
                "",
                isMuted = true,
                isSpeaking = true,
                cameraVideoStreamModel = VideoStreamModel("video", StreamType.VIDEO),
                screenShareVideoStreamModel = null,
                modifiedTimestamp = 456,
                participantStatus = null,
            )

            val flowJobDisplayName = launch {
                participantViewModel.getDisplayNameStateFlow()
                    .toList(emitResultDisplayName)
            }

            val flowJobMuted = launch {
                participantViewModel.getIsMutedStateFlow()
                    .toList(emitResultIsMuted)
            }

            val flowJobNameIndicator = launch {
                participantViewModel.getIsNameIndicatorVisibleStateFlow()
                    .toList(emitResultIsNameIndicatorVisibleStateFlow)
            }

            // act
            participantViewModel.update(
                getParticipantInfoModel(
                    "",
                    "user1",
                    isMuted = false,
                    isSpeaking = false,
                    null,
                    VideoStreamModel(
                        "video",
                        StreamType.VIDEO
                    ),
                    modifiedTimestamp = 456,
                    speakingTimestamp = 567
                ),
            )

            // assert
            Assert.assertEquals("", emitResultDisplayName[0])
            Assert.assertEquals(true, emitResultIsMuted[0])
            Assert.assertEquals(true, emitResultIsNameIndicatorVisibleStateFlow[0])

            // assert updates
            Assert.assertEquals(1, emitResultDisplayName.size)
            Assert.assertEquals(false, emitResultIsMuted[1])
            Assert.assertEquals(false, emitResultIsNameIndicatorVisibleStateFlow[1])

            flowJobDisplayName.cancel()
            flowJobMuted.cancel()
            flowJobNameIndicator.cancel()
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
    ) = ParticipantInfoModel(
        displayName,
        userIdentifier,
        isMuted,
        isSpeaking,
        ParticipantStatus.CONNECTED,
        screenShareVideoStreamModel,
        cameraVideoStreamModel,
        modifiedTimestamp,

    )
}
