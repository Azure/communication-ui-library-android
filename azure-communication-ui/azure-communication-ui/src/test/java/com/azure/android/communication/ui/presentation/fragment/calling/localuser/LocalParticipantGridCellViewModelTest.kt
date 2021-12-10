// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.presentation.fragment.calling.localuser

import com.azure.android.communication.ui.helper.MainCoroutineRule
import com.azure.android.communication.ui.redux.AppStore
import com.azure.android.communication.ui.redux.state.AudioOperationalStatus
import com.azure.android.communication.ui.redux.state.CallingStatus
import com.azure.android.communication.ui.redux.state.CameraDeviceSelectionStatus
import com.azure.android.communication.ui.redux.state.ReduxState
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.mock

@RunWith(MockitoJUnitRunner::class)
internal class LocalParticipantGridCellViewModelTest {

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Test
    fun localParticipantViewModel_update_when_turnMic_then_audioState_update() =
        mainCoroutineRule.testDispatcher.runBlockingTest {

            // arrange
            val mockAppStore = mock<AppStore<ReduxState>> {}
            val viewModel = LocalParticipantViewModel(mockAppStore::dispatch)
            viewModel.init(
                displayName = "username",
                AudioOperationalStatus.PENDING,
                videoStreamID = null,
                numberOfRemoteParticipants = 0,
                CallingStatus.CONNECTED,
                CameraDeviceSelectionStatus.FRONT
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
                CameraDeviceSelectionStatus.FRONT
            )

            viewModel.update(
                displayName = "",
                AudioOperationalStatus.OFF,
                videoStreamID = null,
                numberOfRemoteParticipants = 1,
                CallingStatus.CONNECTED,
                CameraDeviceSelectionStatus.FRONT
            )

            viewModel.update(
                displayName = "",
                AudioOperationalStatus.PENDING,
                videoStreamID = null,
                numberOfRemoteParticipants = 1,
                CallingStatus.CONNECTED,
                CameraDeviceSelectionStatus.FRONT
            )

            viewModel.update(
                displayName = "",
                AudioOperationalStatus.ON,
                videoStreamID = null,
                numberOfRemoteParticipants = 1,
                CallingStatus.CONNECTED,
                CameraDeviceSelectionStatus.FRONT
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
        mainCoroutineRule.testDispatcher.runBlockingTest {

            // arrange
            val mockAppStore = mock<AppStore<ReduxState>> {}
            val viewModel = LocalParticipantViewModel(mockAppStore::dispatch)
            viewModel.init(
                displayName = "username",
                AudioOperationalStatus.PENDING,
                videoStreamID = null,
                numberOfRemoteParticipants = 0,
                CallingStatus.CONNECTED,
                CameraDeviceSelectionStatus.FRONT
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
                CameraDeviceSelectionStatus.FRONT
            )

            // assert
            Assert.assertEquals(2, displayNameFlow.count())
            Assert.assertEquals("a new name", displayNameFlow[1])

            displayNameJob.cancel()
        }

    @Test
    fun localParticipantViewModel_update_when_videoStreamIdOViewModeUpdated_Then_modelFlowUpdated() =
        mainCoroutineRule.testDispatcher.runBlockingTest {

            // arrange
            val mockAppStore = mock<AppStore<ReduxState>> {}
            val viewModel = LocalParticipantViewModel(mockAppStore::dispatch)
            viewModel.init(
                displayName = "username",
                AudioOperationalStatus.PENDING,
                videoStreamID = null,
                numberOfRemoteParticipants = 0,
                CallingStatus.CONNECTED,
                CameraDeviceSelectionStatus.FRONT
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
                CameraDeviceSelectionStatus.FRONT
            )

            viewModel.update(
                displayName = "a new name",
                AudioOperationalStatus.ON,
                videoStreamID = "videoStreamID",
                numberOfRemoteParticipants = 0,
                CallingStatus.CONNECTED,
                CameraDeviceSelectionStatus.FRONT
            )

            viewModel.update(
                displayName = "a new name",
                AudioOperationalStatus.ON,
                videoStreamID = null,
                numberOfRemoteParticipants = 1,
                CallingStatus.CONNECTED,
                CameraDeviceSelectionStatus.FRONT
            )

            // assert
            Assert.assertEquals(4, modelFlow.count())
            Assert.assertEquals("videoStreamID", modelFlow[1].videoStreamID)
            Assert.assertEquals(true, modelFlow[1].shouldDisplayVideo)
            Assert.assertEquals(LocalParticipantViewMode.PIP, modelFlow[1].viewMode)

            Assert.assertEquals("videoStreamID", modelFlow[2].videoStreamID)
            Assert.assertEquals(true, modelFlow[2].shouldDisplayVideo)
            Assert.assertEquals(LocalParticipantViewMode.FULL_SCREEN, modelFlow[2].viewMode)

            Assert.assertEquals(null, modelFlow[3].videoStreamID)
            Assert.assertEquals(false, modelFlow[3].shouldDisplayVideo)
            Assert.assertEquals(LocalParticipantViewMode.PIP, modelFlow[3].viewMode)

            displayNameJob.cancel()
        }

    @Test
    fun localParticipantViewModel_update_when_remoteParticipantNumber_Then_fullScreenAvatarUpdated() =
        mainCoroutineRule.testDispatcher.runBlockingTest {

            // arrange
            val displayName = "username"
            val audioState = AudioOperationalStatus.ON
            val videoStreamID = null

            // arrange
            val mockAppStore = mock<AppStore<ReduxState>> {}
            val viewModel = LocalParticipantViewModel(mockAppStore::dispatch)
            viewModel.init(
                displayName = displayName,
                audioState,
                videoStreamID = videoStreamID,
                numberOfRemoteParticipants = 0,
                CallingStatus.CONNECTED,
                CameraDeviceSelectionStatus.FRONT
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
                CameraDeviceSelectionStatus.FRONT
            )
            viewModel.update(
                displayName,
                audioState,
                videoStreamID,
                1,
                CallingStatus.CONNECTED,
                CameraDeviceSelectionStatus.FRONT
            )
            viewModel.update(
                displayName,
                audioState,
                videoStreamID,
                2,
                CallingStatus.CONNECTED,
                CameraDeviceSelectionStatus.FRONT
            )
            viewModel.update(
                displayName,
                audioState,
                videoStreamID,
                0,
                CallingStatus.CONNECTED,
                CameraDeviceSelectionStatus.FRONT
            )

            // assert
            Assert.assertEquals(3, modelFlow.count())
            Assert.assertEquals(true, modelFlow[0])
            Assert.assertEquals(false, modelFlow[1])
            Assert.assertEquals(true, modelFlow[2])

            displayLobbyJob.cancel()
        }

    @Test
    fun localParticipantViewModel_update_when_cameraDeviceSelectionStatuss_Then_enableCameraSwitchUpdated() =
        mainCoroutineRule.testDispatcher.runBlockingTest {

            // arrange
            val displayName = "username"
            val audioState = AudioOperationalStatus.ON
            val videoStreamID = null

            // arrange
            val mockAppStore = mock<AppStore<ReduxState>> {}
            val viewModel = LocalParticipantViewModel(mockAppStore::dispatch)
            viewModel.init(
                displayName = displayName,
                audioState,
                videoStreamID = videoStreamID,
                numberOfRemoteParticipants = 0,
                CallingStatus.CONNECTED,
                CameraDeviceSelectionStatus.FRONT
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
                CameraDeviceSelectionStatus.FRONT
            )
            viewModel.update(
                displayName,
                audioState,
                videoStreamID,
                0,
                CallingStatus.CONNECTED,
                CameraDeviceSelectionStatus.SWITCHING
            )
            viewModel.update(
                displayName,
                audioState,
                videoStreamID,
                0,
                CallingStatus.CONNECTED,
                CameraDeviceSelectionStatus.FRONT
            )
            viewModel.update(
                displayName,
                audioState,
                videoStreamID,
                0,
                CallingStatus.CONNECTED,
                CameraDeviceSelectionStatus.SWITCHING
            )
            viewModel.update(
                displayName,
                audioState,
                videoStreamID,
                0,
                CallingStatus.CONNECTED,
                CameraDeviceSelectionStatus.FRONT
            )
            viewModel.update(
                displayName,
                audioState,
                videoStreamID,
                0,
                CallingStatus.CONNECTED,
                CameraDeviceSelectionStatus.FRONT
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
}
