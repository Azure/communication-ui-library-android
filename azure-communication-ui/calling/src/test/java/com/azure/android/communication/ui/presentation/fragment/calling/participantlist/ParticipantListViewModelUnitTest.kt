// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.presentation.fragment.calling.participantlist

import com.azure.android.communication.ui.calling.models.ParticipantInfoModel
import com.azure.android.communication.ui.calling.models.StreamType
import com.azure.android.communication.ui.calling.models.VideoStreamModel
import com.azure.android.communication.ui.calling.presentation.fragment.calling.participantlist.ParticipantListCellModel
import com.azure.android.communication.ui.calling.presentation.fragment.calling.participantlist.ParticipantListViewModel
import com.azure.android.communication.ui.calling.redux.state.AudioDeviceSelectionStatus
import com.azure.android.communication.ui.calling.redux.state.AudioOperationalStatus
import com.azure.android.communication.ui.calling.redux.state.AudioState
import com.azure.android.communication.ui.calling.redux.state.BluetoothState
import com.azure.android.communication.ui.calling.redux.state.CameraDeviceSelectionStatus
import com.azure.android.communication.ui.calling.redux.state.CameraOperationalStatus
import com.azure.android.communication.ui.calling.redux.state.CameraState
import com.azure.android.communication.ui.calling.redux.state.CameraTransmissionStatus
import com.azure.android.communication.ui.calling.redux.state.LocalUserState
import com.azure.android.communication.ui.ACSBaseTestCoroutine
import com.azure.android.communication.ui.calling.models.ParticipantStatus
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
internal class ParticipantListViewModelUnitTest : ACSBaseTestCoroutine() {

    @Test
    fun participantListViewModel_update_then_remoteParticipantListCellStateFlowReflectsUpdate() {
        runScopedTest {

            // arrange
            val initialRemoteParticipantsMap: MutableMap<String, ParticipantInfoModel> =
                mutableMapOf()
            initialRemoteParticipantsMap["user1"] = getParticipantInfoModel(
                "user one",
                "user1",
                isMuted = true,
                isSpeaking = true,
                cameraVideoStreamModel = VideoStreamModel("video_stream_1", StreamType.VIDEO),
                modifiedTimestamp = 456,
                speakingTimestamp = 567
            )
            val expectedInitialRemoteParticipantList: List<ParticipantListCellModel> =
                initialRemoteParticipantsMap.values.map {
                    ParticipantListCellModel(
                        it.displayName,
                        it.isMuted,
                        it.userIdentifier,
                        false,
                    )
                }

            val updatedRemoteParticipantsMap: MutableMap<String, ParticipantInfoModel> =
                mutableMapOf()
            updatedRemoteParticipantsMap["user2"] = getParticipantInfoModel(
                "user two",
                "user2",
                isMuted = true,
                isSpeaking = true,
                cameraVideoStreamModel = VideoStreamModel("video_stream_2", StreamType.VIDEO),
                modifiedTimestamp = 111,
                speakingTimestamp = 222
            )
            updatedRemoteParticipantsMap["user3"] = getParticipantInfoModel(
                "user three",
                "user3",
                isMuted = true,
                isSpeaking = true,
                cameraVideoStreamModel = VideoStreamModel("video_stream_3", StreamType.VIDEO),
                modifiedTimestamp = 2121,
                speakingTimestamp = 3232
            )
            val expectedUpdatedRemoteParticipantList: List<ParticipantListCellModel> =
                updatedRemoteParticipantsMap.values.map {
                    ParticipantListCellModel(
                        it.displayName,
                        it.isMuted,
                        it.userIdentifier,
                        false,
                    )
                }

            val localUserState = LocalUserState(
                CameraState(
                    CameraOperationalStatus.OFF,
                    CameraDeviceSelectionStatus.BACK,
                    CameraTransmissionStatus.LOCAL
                ),
                AudioState(
                    AudioOperationalStatus.OFF, AudioDeviceSelectionStatus.SPEAKER_SELECTED,
                    BluetoothState(available = false, deviceName = "bluetooth")
                ),
                "video_stream_id",
                "local_user"
            )

            val participantListViewModel = ParticipantListViewModel()
            participantListViewModel.init(initialRemoteParticipantsMap, localUserState)

            val resultListFromRemoteParticipantListCellStateFlow =
                mutableListOf<List<ParticipantListCellModel>>()

            val flowJob = launch {
                participantListViewModel.getRemoteParticipantListCellStateFlow()
                    .toList(resultListFromRemoteParticipantListCellStateFlow)
            }

            // act
            participantListViewModel.update(updatedRemoteParticipantsMap, localUserState)

            // assert
            Assert.assertEquals(
                expectedInitialRemoteParticipantList,
                resultListFromRemoteParticipantListCellStateFlow[0]
            )

            Assert.assertEquals(
                expectedUpdatedRemoteParticipantList,
                resultListFromRemoteParticipantListCellStateFlow[1]
            )

            flowJob.cancel()
        }
    }

    @Test
    fun participantListViewModel_update_then_localParticipantListCellStateFlowReflectsUpdate() {
        runScopedTest {
            // arrange
            val initialRemoteParticipantsMap: MutableMap<String, ParticipantInfoModel> =
                mutableMapOf()
            initialRemoteParticipantsMap["user1"] = getParticipantInfoModel(
                "user one",
                "user1",
                isMuted = true,
                isSpeaking = true,
                cameraVideoStreamModel = VideoStreamModel("video_stream_1", StreamType.VIDEO),
                modifiedTimestamp = 456,
                speakingTimestamp = 567
            )

            val initialExpectedLocalUserState = LocalUserState(
                CameraState(
                    CameraOperationalStatus.OFF,
                    CameraDeviceSelectionStatus.BACK,
                    CameraTransmissionStatus.LOCAL
                ),
                AudioState(
                    AudioOperationalStatus.OFF, AudioDeviceSelectionStatus.SPEAKER_SELECTED,
                    BluetoothState(available = false, deviceName = "bluetooth")
                ),
                "video_stream_id",
                "local_user"
            )

            val expectedInitialLocalParticipantListCellModel =
                initialExpectedLocalUserState.displayName?.let {
                    ParticipantListCellModel(
                        it,
                        initialExpectedLocalUserState.audioState.operation == AudioOperationalStatus.OFF,
                        "",
                        false,
                    )
                }

            val updatedExpectedLocalUserState = LocalUserState(
                CameraState(
                    CameraOperationalStatus.OFF,
                    CameraDeviceSelectionStatus.BACK,
                    CameraTransmissionStatus.LOCAL
                ),
                AudioState(
                    AudioOperationalStatus.ON,
                    AudioDeviceSelectionStatus.SPEAKER_SELECTED,
                    BluetoothState(available = false, deviceName = "bluetooth")
                ),
                "video_stream_id",
                "local_user"
            )

            val expectedUpdatedLocalParticipantListCellModel =
                initialExpectedLocalUserState.displayName?.let {
                    ParticipantListCellModel(
                        it,
                        initialExpectedLocalUserState.audioState.operation == AudioOperationalStatus.ON,
                        "",
                        false,
                    )
                }

            val participantListViewModel = ParticipantListViewModel()
            participantListViewModel.init(
                initialRemoteParticipantsMap,
                initialExpectedLocalUserState
            )

            val resultListFromLocalParticipantListCellStateFlow =
                mutableListOf<ParticipantListCellModel>()

            val flowJob = launch {
                participantListViewModel.getLocalParticipantListCellStateFlow()
                    .toList(resultListFromLocalParticipantListCellStateFlow)
            }

            // act
            participantListViewModel.update(
                initialRemoteParticipantsMap,
                updatedExpectedLocalUserState
            )

            // assert
            Assert.assertEquals(
                expectedInitialLocalParticipantListCellModel,
                resultListFromLocalParticipantListCellStateFlow[0]
            )

            Assert.assertEquals(
                expectedUpdatedLocalParticipantListCellModel,
                resultListFromLocalParticipantListCellStateFlow[1]
            )

            flowJob.cancel()
        }
    }

    @Test
    fun participantListViewModel_displayParticipantList_then_displayParticipantListStateFlowReflectsUpdate() {
        runScopedTest {

            // arrange
            val initialRemoteParticipantsMap: MutableMap<String, ParticipantInfoModel> =
                mutableMapOf()
            initialRemoteParticipantsMap["user1"] = getParticipantInfoModel(
                "user one",
                "user1",
                isMuted = true,
                isSpeaking = true,
                cameraVideoStreamModel = VideoStreamModel("video_stream_1", StreamType.VIDEO),
                modifiedTimestamp = 456,
                speakingTimestamp = 567
            )

            val initialExpectedLocalUserState = LocalUserState(
                CameraState(
                    CameraOperationalStatus.OFF,
                    CameraDeviceSelectionStatus.BACK,
                    CameraTransmissionStatus.LOCAL
                ),
                AudioState(
                    AudioOperationalStatus.OFF,
                    AudioDeviceSelectionStatus.SPEAKER_SELECTED,
                    BluetoothState(available = false, deviceName = "bluetooth")
                ),
                "video_stream_id",
                "local_user"
            )

            val participantListViewModel = ParticipantListViewModel()
            participantListViewModel.init(
                initialRemoteParticipantsMap,
                initialExpectedLocalUserState
            )

            val resultListFromDisplayParticipantListStateFlow =
                mutableListOf<Boolean>()

            val flowJob = launch {
                participantListViewModel.getDisplayParticipantListStateFlow()
                    .toList(resultListFromDisplayParticipantListStateFlow)
            }

            // act
            participantListViewModel.displayParticipantList()

            // assert
            Assert.assertEquals(
                true,
                resultListFromDisplayParticipantListStateFlow[1]
            )

            flowJob.cancel()
        }
    }

    @Test
    fun participantListViewModel_closeParticipantList_then_displayParticipantListStateFlowReflectsUpdate() {
        runScopedTest {

            // arrange
            val initialRemoteParticipantsMap: MutableMap<String, ParticipantInfoModel> =
                mutableMapOf()
            initialRemoteParticipantsMap["user1"] = getParticipantInfoModel(
                "user one",
                "user1",
                isMuted = true,
                isSpeaking = true,
                cameraVideoStreamModel = VideoStreamModel("video_stream_1", StreamType.VIDEO),
                modifiedTimestamp = 456,
                speakingTimestamp = 567
            )

            val initialExpectedLocalUserState = LocalUserState(
                CameraState(
                    CameraOperationalStatus.OFF,
                    CameraDeviceSelectionStatus.BACK,
                    CameraTransmissionStatus.LOCAL
                ),
                AudioState(
                    AudioOperationalStatus.OFF,
                    AudioDeviceSelectionStatus.SPEAKER_SELECTED,
                    BluetoothState(available = false, deviceName = "bluetooth")
                ),
                "video_stream_id",
                "local_user"
            )

            val participantListViewModel = ParticipantListViewModel()
            participantListViewModel.init(
                initialRemoteParticipantsMap,
                initialExpectedLocalUserState
            )

            val resultListFromDisplayParticipantListStateFlow =
                mutableListOf<Boolean>()

            val flowJob = launch {
                participantListViewModel.getDisplayParticipantListStateFlow()
                    .toList(resultListFromDisplayParticipantListStateFlow)
            }

            // act
            participantListViewModel.closeParticipantList()

            // assert
            Assert.assertEquals(
                false,
                resultListFromDisplayParticipantListStateFlow[0]
            )

            flowJob.cancel()
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
    ) = ParticipantInfoModel(
        displayName,
        userIdentifier,
        isMuted,
        false,
        isSpeaking,
        ParticipantStatus.CONNECTED,
        screenShareVideoStreamModel,
        cameraVideoStreamModel,
        modifiedTimestamp,
    )
}
