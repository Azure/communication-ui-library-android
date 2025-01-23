// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.calling.localuser

import com.azure.android.communication.ui.calling.ACSBaseTestCoroutine
import com.azure.android.communication.ui.calling.models.CallCompositeAudioVideoMode
import com.azure.android.communication.ui.calling.redux.AppStore
import com.azure.android.communication.ui.calling.redux.state.AudioOperationalStatus
import com.azure.android.communication.ui.calling.redux.state.CallingStatus
import com.azure.android.communication.ui.calling.redux.state.CameraDeviceSelectionStatus
import com.azure.android.communication.ui.calling.redux.state.ReduxState
import com.azure.android.communication.ui.calling.redux.state.VisibilityStatus
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import org.junit.Assert
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.mock

@RunWith(MockitoJUnitRunner::class)
internal class LocalParticipantGridCellViewModelTest : ACSBaseTestCoroutine() {

    @Test
    fun localParticipantViewModel_update_when_turnMic_then_audioState_update() =
        runScopedTest {

            // arrange
            val mockAppStore = mock<AppStore<ReduxState>> {}
            val viewModel =
                LocalParticipantViewModel(
                    mockAppStore::dispatch,
                )

            viewModel.init(
                displayName = "username",
                AudioOperationalStatus.PENDING,
                videoStreamID = null,
                numberOfRemoteParticipants = 0,
                CallingStatus.CONNECTED,
                CameraDeviceSelectionStatus.FRONT,
                2,
                VisibilityStatus.VISIBLE,
                CallCompositeAudioVideoMode.AUDIO_AND_VIDEO,
                isOverlayDisplayedOverGrid = false,
            )

            val isMutedFlow = mutableListOf<Boolean>()
            val mutedJob = launch {
                viewModel.getLocalUserMutedStateFlow().toList(isMutedFlow)
            }

            // act
            viewModel.update(
                displayName = "",
                AudioOperationalStatus.PENDING,
                videoStreamID = null,
                numberOfRemoteParticipants = 1,
                CallingStatus.CONNECTED,
                CameraDeviceSelectionStatus.FRONT,
                2,
                VisibilityStatus.VISIBLE,
                CallCompositeAudioVideoMode.AUDIO_AND_VIDEO,
                isOverlayDisplayedOverGrid = false,
            )

            viewModel.update(
                displayName = "",
                AudioOperationalStatus.OFF,
                videoStreamID = null,
                numberOfRemoteParticipants = 1,
                CallingStatus.CONNECTED,
                CameraDeviceSelectionStatus.FRONT,
                2,
                VisibilityStatus.VISIBLE,
                CallCompositeAudioVideoMode.AUDIO_AND_VIDEO,
                isOverlayDisplayedOverGrid = false,
            )

            viewModel.update(
                displayName = "",
                AudioOperationalStatus.PENDING,
                videoStreamID = null,
                numberOfRemoteParticipants = 1,
                CallingStatus.CONNECTED,
                CameraDeviceSelectionStatus.FRONT,
                2,
                VisibilityStatus.VISIBLE,
                CallCompositeAudioVideoMode.AUDIO_AND_VIDEO,
                isOverlayDisplayedOverGrid = false,
            )

            viewModel.update(
                displayName = "",
                AudioOperationalStatus.ON,
                videoStreamID = null,
                numberOfRemoteParticipants = 1,
                CallingStatus.CONNECTED,
                CameraDeviceSelectionStatus.FRONT,
                2,
                VisibilityStatus.VISIBLE,
                CallCompositeAudioVideoMode.AUDIO_AND_VIDEO,
                isOverlayDisplayedOverGrid = false,
            )

            // assert
            Assert.assertEquals(3, isMutedFlow.count())
            Assert.assertEquals(false, isMutedFlow[0])
            Assert.assertEquals(true, isMutedFlow[1])
            Assert.assertEquals(false, isMutedFlow[2])

            mutedJob.cancel()
        }

    @Test
    fun localParticipantViewModel_update_when_displayNameChanged_then_displayNameFlowUpdated() =
        runScopedTest {

            // arrange
            val mockAppStore = mock<AppStore<ReduxState>> {}
            val viewModel =
                LocalParticipantViewModel(
                    mockAppStore::dispatch,
                )

            viewModel.init(
                displayName = "username",
                AudioOperationalStatus.PENDING,
                videoStreamID = null,
                numberOfRemoteParticipants = 0,
                CallingStatus.CONNECTED,
                CameraDeviceSelectionStatus.FRONT,
                2,
                VisibilityStatus.VISIBLE,
                CallCompositeAudioVideoMode.AUDIO_AND_VIDEO,
                isOverlayDisplayedOverGrid = false,
            )

            val displayNameFlow = mutableListOf<String?>()
            val displayNameJob = launch {
                viewModel.getDisplayNameStateFlow().toList(displayNameFlow)
            }

            // act
            viewModel.update(
                displayName = "a new name",
                AudioOperationalStatus.ON,
                videoStreamID = null,
                numberOfRemoteParticipants = 1,
                CallingStatus.CONNECTED,
                CameraDeviceSelectionStatus.FRONT,
                2,
                VisibilityStatus.VISIBLE,
                CallCompositeAudioVideoMode.AUDIO_AND_VIDEO,
                isOverlayDisplayedOverGrid = false,
            )

            // assert
            Assert.assertEquals(2, displayNameFlow.count())
            Assert.assertEquals("a new name", displayNameFlow[1])

            displayNameJob.cancel()
        }

    @Test
    fun localParticipantViewModel_update_when_videoStreamIdOViewModeUpdated_Then_modelFlowUpdated() =
        runScopedTest {

            // arrange
            val mockAppStore = mock<AppStore<ReduxState>> {}
            val viewModel =
                LocalParticipantViewModel(
                    mockAppStore::dispatch,
                )

            viewModel.init(
                displayName = "username",
                AudioOperationalStatus.PENDING,
                videoStreamID = null,
                numberOfRemoteParticipants = 0,
                CallingStatus.CONNECTED,
                CameraDeviceSelectionStatus.FRONT,
                2,
                VisibilityStatus.VISIBLE,
                CallCompositeAudioVideoMode.AUDIO_AND_VIDEO,
                isOverlayDisplayedOverGrid = false,
            )

            val modelFlow = mutableListOf<LocalParticipantViewModel.VideoModel>()
            val displayNameJob = launch {
                viewModel.getVideoStatusFlow().toList(modelFlow)
            }

            // act
            viewModel.update(
                displayName = "a new name",
                AudioOperationalStatus.ON,
                videoStreamID = "videoStreamID",
                numberOfRemoteParticipants = 1,
                CallingStatus.CONNECTED,
                CameraDeviceSelectionStatus.FRONT,
                2,
                VisibilityStatus.VISIBLE,
                CallCompositeAudioVideoMode.AUDIO_AND_VIDEO,
                isOverlayDisplayedOverGrid = false,
            )

            viewModel.update(
                displayName = "a new name",
                AudioOperationalStatus.ON,
                videoStreamID = "videoStreamID",
                numberOfRemoteParticipants = 0,
                CallingStatus.CONNECTED,
                CameraDeviceSelectionStatus.FRONT,
                2,
                VisibilityStatus.VISIBLE,
                CallCompositeAudioVideoMode.AUDIO_AND_VIDEO,
                isOverlayDisplayedOverGrid = false,
            )

            viewModel.update(
                displayName = "a new name",
                AudioOperationalStatus.ON,
                videoStreamID = null,
                numberOfRemoteParticipants = 1,
                CallingStatus.CONNECTED,
                CameraDeviceSelectionStatus.FRONT,
                2,
                VisibilityStatus.VISIBLE,
                CallCompositeAudioVideoMode.AUDIO_AND_VIDEO,
                isOverlayDisplayedOverGrid = false,
            )

            // assert
            Assert.assertEquals(4, modelFlow.count())
            Assert.assertEquals("videoStreamID", modelFlow[1].videoStreamID)
            Assert.assertEquals(true, modelFlow[1].shouldDisplayVideo)
            Assert.assertEquals(LocalParticipantViewMode.SELFIE_PIP, modelFlow[1].viewMode)

            Assert.assertEquals("videoStreamID", modelFlow[2].videoStreamID)
            Assert.assertEquals(true, modelFlow[2].shouldDisplayVideo)
            Assert.assertEquals(LocalParticipantViewMode.FULL_SCREEN, modelFlow[2].viewMode)

            Assert.assertEquals(null, modelFlow[3].videoStreamID)
            Assert.assertEquals(false, modelFlow[3].shouldDisplayVideo)
            Assert.assertEquals(LocalParticipantViewMode.SELFIE_PIP, modelFlow[3].viewMode)

            displayNameJob.cancel()
        }

    @Test
    fun localParticipantViewModel_update_when_remoteParticipantNumber_Then_fullScreenAvatarUpdated() =
        runScopedTest {

            // arrange
            val displayName = "username"
            val audioState = AudioOperationalStatus.ON
            val videoStreamID = null

            // arrange
            val mockAppStore = mock<AppStore<ReduxState>> {}
            val viewModel =
                LocalParticipantViewModel(
                    mockAppStore::dispatch,
                )

            viewModel.init(
                displayName = displayName,
                audioState,
                videoStreamID = videoStreamID,
                numberOfRemoteParticipants = 0,
                CallingStatus.CONNECTED,
                CameraDeviceSelectionStatus.FRONT,
                2,
                VisibilityStatus.VISIBLE,
                CallCompositeAudioVideoMode.AUDIO_AND_VIDEO,
                isOverlayDisplayedOverGrid = false,
            )

            val modelFlow = mutableListOf<Boolean>()
            val displayLobbyJob = launch {
                viewModel.getDisplayFullScreenAvatarFlow().toList(modelFlow)
            }

            // act
            viewModel.update(
                displayName,
                audioState,
                videoStreamID,
                0,
                CallingStatus.CONNECTED,
                CameraDeviceSelectionStatus.FRONT,
                2,
                VisibilityStatus.VISIBLE,
                CallCompositeAudioVideoMode.AUDIO_AND_VIDEO,
                isOverlayDisplayedOverGrid = false,
            )
            viewModel.update(
                displayName,
                audioState,
                videoStreamID,
                1,
                CallingStatus.CONNECTED,
                CameraDeviceSelectionStatus.FRONT,
                2,
                VisibilityStatus.VISIBLE,
                CallCompositeAudioVideoMode.AUDIO_AND_VIDEO,
                isOverlayDisplayedOverGrid = false,
            )
            viewModel.update(
                displayName,
                audioState,
                videoStreamID,
                2,
                CallingStatus.CONNECTED,
                CameraDeviceSelectionStatus.FRONT,
                2,
                VisibilityStatus.VISIBLE,
                CallCompositeAudioVideoMode.AUDIO_AND_VIDEO,
                isOverlayDisplayedOverGrid = false,
            )
            viewModel.update(
                displayName,
                audioState,
                videoStreamID,
                0,
                CallingStatus.CONNECTED,
                CameraDeviceSelectionStatus.FRONT,
                2,
                VisibilityStatus.VISIBLE,
                CallCompositeAudioVideoMode.AUDIO_AND_VIDEO,
                isOverlayDisplayedOverGrid = false,
            )

            // assert
            Assert.assertEquals(3, modelFlow.count())
            Assert.assertEquals(true, modelFlow[0])
            Assert.assertEquals(false, modelFlow[1])
            Assert.assertEquals(true, modelFlow[2])

            displayLobbyJob.cancel()
        }

    @Test
    fun localParticipantViewModel_update_when_callStateNotConnected_Then_fullScreenAvatarNotDisplayed() =
        runScopedTest {

            // arrange
            val displayName = "username"
            val audioState = AudioOperationalStatus.ON
            val videoStreamID = null

            // arrange
            val mockAppStore = mock<AppStore<ReduxState>> {}
            val viewModel =
                LocalParticipantViewModel(
                    mockAppStore::dispatch,
                )

            viewModel.init(
                displayName = displayName,
                audioState,
                videoStreamID = videoStreamID,
                numberOfRemoteParticipants = 0,
                CallingStatus.CONNECTING,
                CameraDeviceSelectionStatus.FRONT,
                2,
                VisibilityStatus.VISIBLE,
                CallCompositeAudioVideoMode.AUDIO_AND_VIDEO,
                isOverlayDisplayedOverGrid = false,
            )

            val modelFlow = mutableListOf<Boolean>()
            val displayLobbyJob = launch {
                viewModel.getDisplayFullScreenAvatarFlow().toList(modelFlow)
            }

            // act
            viewModel.update(
                displayName,
                audioState,
                videoStreamID,
                0,
                CallingStatus.CONNECTING,
                CameraDeviceSelectionStatus.FRONT,
                2,
                VisibilityStatus.VISIBLE,
                CallCompositeAudioVideoMode.AUDIO_AND_VIDEO,
                isOverlayDisplayedOverGrid = false,
            )
            viewModel.update(
                displayName,
                audioState,
                videoStreamID,
                1,
                CallingStatus.DISCONNECTING,
                CameraDeviceSelectionStatus.FRONT,
                2,
                VisibilityStatus.VISIBLE,
                CallCompositeAudioVideoMode.AUDIO_AND_VIDEO,
                isOverlayDisplayedOverGrid = false,
            )
            viewModel.update(
                displayName,
                audioState,
                videoStreamID,
                2,
                CallingStatus.DISCONNECTED,
                CameraDeviceSelectionStatus.FRONT,
                2,
                VisibilityStatus.VISIBLE,
                CallCompositeAudioVideoMode.AUDIO_AND_VIDEO,
                isOverlayDisplayedOverGrid = false,
            )
            viewModel.update(
                displayName,
                audioState,
                videoStreamID,
                0,
                CallingStatus.CONNECTING,
                CameraDeviceSelectionStatus.FRONT,
                2,
                VisibilityStatus.VISIBLE,
                CallCompositeAudioVideoMode.AUDIO_AND_VIDEO,
                isOverlayDisplayedOverGrid = false,
            )

            // assert
            Assert.assertEquals(1, modelFlow.count())
            Assert.assertEquals(false, modelFlow[0])

            displayLobbyJob.cancel()
        }

    @Test
    fun localParticipantViewModel_update_when_cameraDeviceSelectionStatus_Then_enableCameraSwitchUpdated() =
        runScopedTest {

            // arrange
            val displayName = "username"
            val audioState = AudioOperationalStatus.ON
            val videoStreamID = null

            // arrange
            val mockAppStore = mock<AppStore<ReduxState>> {}
            val viewModel =
                LocalParticipantViewModel(
                    mockAppStore::dispatch,
                )

            viewModel.init(
                displayName = displayName,
                audioState,
                videoStreamID = videoStreamID,
                numberOfRemoteParticipants = 0,
                CallingStatus.CONNECTED,
                CameraDeviceSelectionStatus.FRONT,
                2,
                VisibilityStatus.VISIBLE,
                CallCompositeAudioVideoMode.AUDIO_AND_VIDEO,
                isOverlayDisplayedOverGrid = false,
            )

            val modelFlow = mutableListOf<Boolean>()
            val displayLobbyJob = launch {
                viewModel.getEnableCameraSwitchFlow().toList(modelFlow)
            }

            // act
            viewModel.update(
                displayName,
                audioState,
                videoStreamID,
                0,
                CallingStatus.CONNECTED,
                CameraDeviceSelectionStatus.FRONT,
                2,
                VisibilityStatus.VISIBLE,
                CallCompositeAudioVideoMode.AUDIO_AND_VIDEO,
                isOverlayDisplayedOverGrid = false,
            )
            viewModel.update(
                displayName,
                audioState,
                videoStreamID,
                0,
                CallingStatus.CONNECTED,
                CameraDeviceSelectionStatus.SWITCHING,
                2,
                VisibilityStatus.VISIBLE,
                CallCompositeAudioVideoMode.AUDIO_AND_VIDEO,
                isOverlayDisplayedOverGrid = false,
            )
            viewModel.update(
                displayName,
                audioState,
                videoStreamID,
                0,
                CallingStatus.CONNECTED,
                CameraDeviceSelectionStatus.FRONT,
                2,
                VisibilityStatus.VISIBLE,
                CallCompositeAudioVideoMode.AUDIO_AND_VIDEO,
                isOverlayDisplayedOverGrid = false,
            )
            viewModel.update(
                displayName,
                audioState,
                videoStreamID,
                0,
                CallingStatus.CONNECTED,
                CameraDeviceSelectionStatus.SWITCHING,
                2,
                VisibilityStatus.VISIBLE,
                CallCompositeAudioVideoMode.AUDIO_AND_VIDEO,
                isOverlayDisplayedOverGrid = false,
            )
            viewModel.update(
                displayName,
                audioState,
                videoStreamID,
                0,
                CallingStatus.CONNECTED,
                CameraDeviceSelectionStatus.FRONT,
                2,
                VisibilityStatus.VISIBLE,
                CallCompositeAudioVideoMode.AUDIO_AND_VIDEO,
                isOverlayDisplayedOverGrid = false,
            )
            viewModel.update(
                displayName,
                audioState,
                videoStreamID,
                0,
                CallingStatus.CONNECTED,
                CameraDeviceSelectionStatus.FRONT,
                2,
                VisibilityStatus.VISIBLE,
                CallCompositeAudioVideoMode.AUDIO_AND_VIDEO,
                isOverlayDisplayedOverGrid = false,
            )

            // assert
            Assert.assertEquals(5, modelFlow.count())
            Assert.assertEquals(true, modelFlow[0])
            Assert.assertEquals(false, modelFlow[1])
            Assert.assertEquals(true, modelFlow[2])
            Assert.assertEquals(false, modelFlow[3])
            Assert.assertEquals(true, modelFlow[4])

            displayLobbyJob.cancel()
        }

    @Test
    fun `Ensure the Local Participant View is not displayed when in Audio Only mode and multiple participants are displayed`() =
        runScopedTest {

            // arrange
            val displayName = "username"
            val audioState = AudioOperationalStatus.ON
            val videoStreamID = null

            // arrange
            val mockAppStore = mock<AppStore<ReduxState>> {}
            val viewModel =
                LocalParticipantViewModel(
                    mockAppStore::dispatch,
                )

            viewModel.init(
                displayName = displayName,
                audioState,
                videoStreamID = videoStreamID,
                numberOfRemoteParticipants = 3,
                CallingStatus.CONNECTED,
                CameraDeviceSelectionStatus.FRONT,
                2,
                VisibilityStatus.VISIBLE,
                CallCompositeAudioVideoMode.AUDIO_ONLY,
                isOverlayDisplayedOverGrid = false,
            )

            assertFalse(viewModel.getIsVisibleFlow().value)
        }

    @Test
    fun `Ensure the Local Participant View IS displayed when in Audio Only mode and they are alone`() =
        runScopedTest {

            // arrange
            val displayName = "username"
            val audioState = AudioOperationalStatus.ON
            val videoStreamID = null

            // arrange
            val mockAppStore = mock<AppStore<ReduxState>> {}
            val viewModel =
                LocalParticipantViewModel(
                    mockAppStore::dispatch,
                )

            viewModel.init(
                displayName = displayName,
                audioState,
                videoStreamID = videoStreamID,
                numberOfRemoteParticipants = 0,
                CallingStatus.CONNECTED,
                CameraDeviceSelectionStatus.FRONT,
                2,
                VisibilityStatus.VISIBLE,
                CallCompositeAudioVideoMode.AUDIO_ONLY,
                isOverlayDisplayedOverGrid = false,
            )

            assertTrue(viewModel.getIsVisibleFlow().value)
        }

    @Test
    fun localParticipantViewModel_update_when_cameraCountChange_Then_enableCameraSwitchUpdated() =
        runScopedTest {

            // arrange
            val displayName = "username"
            val audioState = AudioOperationalStatus.ON
            val videoStreamID = "123"

            // arrange
            val mockAppStore = mock<AppStore<ReduxState>> {}
            val viewModel =
                LocalParticipantViewModel(
                    mockAppStore::dispatch,
                )

            viewModel.init(
                displayName = displayName,
                audioState,
                videoStreamID = videoStreamID,
                numberOfRemoteParticipants = 2,
                CallingStatus.CONNECTED,
                CameraDeviceSelectionStatus.FRONT,
                0,
                VisibilityStatus.VISIBLE,
                CallCompositeAudioVideoMode.AUDIO_AND_VIDEO,
                isOverlayDisplayedOverGrid = false,
            )

            val getDisplayPipSwitchCameraButtonFlow = mutableListOf<Boolean>()
            val getDisplaySwitchCameraButtonFlow = mutableListOf<Boolean>()

            val displayPipSwitchCameraButtonFlow = launch {
                viewModel.getDisplayPipSwitchCameraButtonFlow()
                    .toList(getDisplayPipSwitchCameraButtonFlow)
            }

            val displaySwitchCameraButtonFlow = launch {
                viewModel.getDisplaySwitchCameraButtonFlow()
                    .toList(getDisplaySwitchCameraButtonFlow)
            }

            // act
            viewModel.update(
                displayName,
                audioState,
                videoStreamID,
                2,
                CallingStatus.CONNECTED,
                CameraDeviceSelectionStatus.FRONT,
                2,
                VisibilityStatus.VISIBLE,
                CallCompositeAudioVideoMode.AUDIO_AND_VIDEO,
                isOverlayDisplayedOverGrid = false,
            )
            viewModel.update(
                displayName,
                audioState,
                videoStreamID,
                2,
                CallingStatus.CONNECTED,
                CameraDeviceSelectionStatus.FRONT,
                0,
                VisibilityStatus.VISIBLE,
                CallCompositeAudioVideoMode.AUDIO_AND_VIDEO,
                isOverlayDisplayedOverGrid = false,
            )
            viewModel.update(
                displayName,
                audioState,
                videoStreamID,
                0,
                CallingStatus.CONNECTED,
                CameraDeviceSelectionStatus.FRONT,
                2,
                VisibilityStatus.VISIBLE,
                CallCompositeAudioVideoMode.AUDIO_AND_VIDEO,
                isOverlayDisplayedOverGrid = false,
            )
            viewModel.update(
                displayName,
                audioState,
                videoStreamID,
                0,
                CallingStatus.CONNECTED,
                CameraDeviceSelectionStatus.FRONT,
                0,
                VisibilityStatus.VISIBLE,
                CallCompositeAudioVideoMode.AUDIO_AND_VIDEO,
                isOverlayDisplayedOverGrid = false,
            )

            // assert
            Assert.assertEquals(3, getDisplaySwitchCameraButtonFlow.count())
            Assert.assertEquals(false, getDisplaySwitchCameraButtonFlow[0])
            Assert.assertEquals(true, getDisplaySwitchCameraButtonFlow[1])
            Assert.assertEquals(false, getDisplaySwitchCameraButtonFlow[2])

            Assert.assertEquals(3, getDisplayPipSwitchCameraButtonFlow.count())
            Assert.assertEquals(false, getDisplayPipSwitchCameraButtonFlow[0])
            Assert.assertEquals(true, getDisplayPipSwitchCameraButtonFlow[1])
            Assert.assertEquals(false, getDisplayPipSwitchCameraButtonFlow[2])

            displayPipSwitchCameraButtonFlow.cancel()
            displaySwitchCameraButtonFlow.cancel()
        }
}
