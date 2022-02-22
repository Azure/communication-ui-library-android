// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.presentation.fragment.setup.components

import com.azure.android.communication.ui.helper.MainCoroutineRule
import com.azure.android.communication.ui.redux.AppStore
import com.azure.android.communication.ui.redux.action.LocalParticipantAction
import com.azure.android.communication.ui.redux.action.PermissionAction
import com.azure.android.communication.ui.redux.state.CallingStatus
import com.azure.android.communication.ui.redux.state.AudioState
import com.azure.android.communication.ui.redux.state.CameraState
import com.azure.android.communication.ui.redux.state.CameraOperationalStatus
import com.azure.android.communication.ui.redux.state.CameraTransmissionStatus
import com.azure.android.communication.ui.redux.state.AudioDeviceSelectionStatus
import com.azure.android.communication.ui.redux.state.PermissionState
import com.azure.android.communication.ui.redux.state.CameraDeviceSelectionStatus
import com.azure.android.communication.ui.redux.state.PermissionStatus
import com.azure.android.communication.ui.redux.state.AudioOperationalStatus
import com.azure.android.communication.ui.redux.state.ReduxState

import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.argThat
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify

@RunWith(MockitoJUnitRunner::class)
internal class SetupControlBarViewModelUnitTest {
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Test
    fun setupControlBarViewModel_requestAudioPermission_then_dispatchAudioPermissionRequested() {
        // Arrange
        val mockAppStore = mock<AppStore<ReduxState>> {
            on { dispatch(any()) } doAnswer { }
        }
        val setupControlBarViewModel =
            SetupControlBarViewModel(mockAppStore::dispatch)
        initViewModel(setupControlBarViewModel)

        // Act
        setupControlBarViewModel.requestAudioPermission()

        // Assert
        verify(mockAppStore, times(1)).dispatch(
            argThat { action ->
                action is PermissionAction.AudioPermissionRequested
            }
        )
    }

    @Test
    fun setupControlBarViewModel_turnCameraOn_then_dispatchCameraPreviewOnRequested() {
        // Arrange
        val mockAppStore = mock<AppStore<ReduxState>> {
            on { dispatch(any()) } doAnswer { }
        }
        val setupControlBarViewModel =
            SetupControlBarViewModel(mockAppStore::dispatch)
        initViewModel(setupControlBarViewModel)

        // Act
        setupControlBarViewModel.turnCameraOn()

        // Assert
        verify(mockAppStore, times(1)).dispatch(
            argThat { action ->
                action is LocalParticipantAction.CameraPreviewOnRequested
            }
        )

        updateViewModel(setupControlBarViewModel, CallingStatus.CONNECTING)

        // Act
        setupControlBarViewModel.turnCameraOn()

        // Assert
        verify(mockAppStore, times(1)).dispatch(
            argThat { action ->
                action is LocalParticipantAction.CameraOnRequested
            }
        )
    }

    @Test
    fun setupControlBarViewModel_turnCameraOff_then_dispatchCameraPreviewOffTriggered() {
        // Arrange
        val mockAppStore = mock<AppStore<ReduxState>> {
            on { dispatch(any()) } doAnswer { }
        }
        val setupControlBarViewModel =
            SetupControlBarViewModel(mockAppStore::dispatch)

        initViewModel(setupControlBarViewModel)

        // Act
        setupControlBarViewModel.turnCameraOff()

        // Assert
        verify(mockAppStore, times(1)).dispatch(
            argThat { action ->
                action is LocalParticipantAction.CameraPreviewOffTriggered
            }
        )

        updateViewModel(setupControlBarViewModel, CallingStatus.CONNECTING)

        // Act
        setupControlBarViewModel.turnCameraOff()

        // Assert
        verify(mockAppStore, times(1)).dispatch(
            argThat { action ->
                action is LocalParticipantAction.CameraOffTriggered
            }
        )
    }

    @Test
    fun setupControlBarViewModel_turnMicOn_then_dispatchMicPreviewOnTriggered() {
        // Arrange
        val mockAppStore = mock<AppStore<ReduxState>> {
            on { dispatch(any()) } doAnswer { }
        }
        val setupControlBarViewModel =
            SetupControlBarViewModel(mockAppStore::dispatch)
        initViewModel(setupControlBarViewModel)

        // Act
        setupControlBarViewModel.turnMicOn()

        // Assert
        verify(mockAppStore, times(1)).dispatch(
            argThat { action ->
                action is LocalParticipantAction.MicPreviewOnTriggered
            }
        )

        updateViewModel(setupControlBarViewModel, CallingStatus.CONNECTING)

        // Act
        setupControlBarViewModel.turnMicOn()

        // Assert
        verify(mockAppStore, times(1)).dispatch(
            argThat { action ->
                action is LocalParticipantAction.MicOnTriggered
            }
        )
    }

    @Test
    fun setupControlBarViewModel_turnMicOff_then_dispatchMicPreviewOffTriggered() {
        // Arrange
        val mockAppStore = mock<AppStore<ReduxState>> {
            on { dispatch(any()) } doAnswer { }
        }
        val setupControlBarViewModel =
            SetupControlBarViewModel(mockAppStore::dispatch)
        initViewModel(setupControlBarViewModel)

        // Act
        setupControlBarViewModel.turnMicOff()

        // Assert
        verify(mockAppStore, times(1)).dispatch(
            argThat { action ->
                action is LocalParticipantAction.MicPreviewOffTriggered
            }
        )

        updateViewModel(setupControlBarViewModel, CallingStatus.CONNECTING)

        // Act
        setupControlBarViewModel.turnMicOff()

        // Assert
        verify(mockAppStore, times(1)).dispatch(
            argThat { action -> action is LocalParticipantAction.MicOffTriggered }
        )
    }

    private fun initViewModel(
        setupControlBarViewModel: SetupControlBarViewModel,
        callingStatus: CallingStatus = CallingStatus.NONE,
    ) {
        setupControlBarViewModel.init(
            PermissionState(PermissionStatus.GRANTED, PermissionStatus.GRANTED),
            CameraState(
                CameraOperationalStatus.OFF,
                CameraDeviceSelectionStatus.FRONT,
                CameraTransmissionStatus.LOCAL
            ),
            AudioState(AudioOperationalStatus.OFF, AudioDeviceSelectionStatus.SPEAKER_REQUESTED),
            callingStatus
        )
    }

    private fun updateViewModel(
        setupControlBarViewModel: SetupControlBarViewModel,
        callingStatus: CallingStatus = CallingStatus.NONE,
    ) {
        setupControlBarViewModel.update(
            PermissionState(PermissionStatus.GRANTED, PermissionStatus.GRANTED),
            CameraState(
                CameraOperationalStatus.OFF,
                CameraDeviceSelectionStatus.FRONT,
                CameraTransmissionStatus.LOCAL
            ),
            AudioState(AudioOperationalStatus.OFF, AudioDeviceSelectionStatus.SPEAKER_REQUESTED),
            callingStatus
        )
    }
}
