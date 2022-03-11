// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.presentation.fragment.setup.components

import com.azure.android.communication.ui.helper.MainCoroutineRule
import com.azure.android.communication.ui.redux.AppStore
import com.azure.android.communication.ui.redux.action.PermissionAction
import com.azure.android.communication.ui.redux.state.AudioDeviceSelectionStatus
import com.azure.android.communication.ui.redux.state.AudioOperationalStatus
import com.azure.android.communication.ui.redux.state.AudioState
import com.azure.android.communication.ui.redux.state.CallingState
import com.azure.android.communication.ui.redux.state.CallingStatus
import com.azure.android.communication.ui.redux.state.CameraState
import com.azure.android.communication.ui.redux.state.CameraDeviceSelectionStatus
import com.azure.android.communication.ui.redux.state.CameraOperationalStatus
import com.azure.android.communication.ui.redux.state.CameraTransmissionStatus
import com.azure.android.communication.ui.redux.state.PermissionState
import com.azure.android.communication.ui.redux.state.PermissionStatus
import com.azure.android.communication.ui.redux.state.ReduxState
import org.junit.Assert

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
    fun setupControlBarViewModel_init_audioPermission_notAsked_then_dispatchAudioPermissionRequested() {
        // Arrange
        val mockAppStore = mock<AppStore<ReduxState>> {
            on { dispatch(any()) } doAnswer { }
        }
        val setupControlBarViewModel = SetupControlBarViewModel(mockAppStore::dispatch)

        // Act
        setupControlBarViewModel.init(
            PermissionState(
                audioPermissionState = PermissionStatus.NOT_ASKED,
                cameraPermissionState = PermissionStatus.NOT_ASKED
            ),
            CameraState(CameraOperationalStatus.OFF, CameraDeviceSelectionStatus.FRONT, CameraTransmissionStatus.LOCAL),
            AudioState(AudioOperationalStatus.OFF, AudioDeviceSelectionStatus.SPEAKER_SELECTED),
            CallingState(CallingStatus.NONE),
            openAudioDeviceSelectionMenuCallback = { }
        )

        // Assert
        verify(mockAppStore, times(1)).dispatch(
            argThat { action ->
                action is PermissionAction.AudioPermissionRequested
            }
        )
    }

    @Test
    fun setupControlBarViewModel_init_audioPermission_asked_then_dispatchAudioPermissionRequested() {
        // Arrange
        val mockAppStore = mock<AppStore<ReduxState>> {
        }
        val setupControlBarViewModel = SetupControlBarViewModel(mockAppStore::dispatch)

        // Act
        setupControlBarViewModel.init(
            PermissionState(
                audioPermissionState = PermissionStatus.DENIED,
                cameraPermissionState = PermissionStatus.NOT_ASKED
            ),
            CameraState(CameraOperationalStatus.OFF, CameraDeviceSelectionStatus.FRONT, CameraTransmissionStatus.LOCAL),
            AudioState(AudioOperationalStatus.OFF, AudioDeviceSelectionStatus.SPEAKER_SELECTED),
            CallingState(CallingStatus.NONE),
            openAudioDeviceSelectionMenuCallback = { }
        )

        // Assert
        verify(mockAppStore, times(0)).dispatch(
            argThat { action ->
                action is PermissionAction.AudioPermissionRequested
            }
        )
    }

    @Test
    fun setupControlBarViewModel_init_audioPermission_denied_then_isVisible_false() {
        // Arrange
        val mockAppStore = mock<AppStore<ReduxState>> { }
        val setupControlBarViewModel = SetupControlBarViewModel(mockAppStore::dispatch)

        // Act
        setupControlBarViewModel.init(
            PermissionState(
                audioPermissionState = PermissionStatus.NOT_ASKED,
                cameraPermissionState = PermissionStatus.NOT_ASKED
            ),
            CameraState(CameraOperationalStatus.OFF, CameraDeviceSelectionStatus.FRONT, CameraTransmissionStatus.LOCAL),
            AudioState(AudioOperationalStatus.OFF, AudioDeviceSelectionStatus.SPEAKER_SELECTED),
            CallingState(CallingStatus.NONE),
            openAudioDeviceSelectionMenuCallback = { }
        )

        Assert.assertTrue(setupControlBarViewModel.getIsVisibleState().value)

        setupControlBarViewModel.update(
            PermissionState(
                audioPermissionState = PermissionStatus.DENIED,
                cameraPermissionState = PermissionStatus.NOT_ASKED
            ),
            CameraState(CameraOperationalStatus.OFF, CameraDeviceSelectionStatus.FRONT, CameraTransmissionStatus.LOCAL),
            AudioState(AudioOperationalStatus.OFF, AudioDeviceSelectionStatus.SPEAKER_SELECTED),
            CallingState(CallingStatus.NONE),
        )

        Assert.assertFalse(setupControlBarViewModel.getIsVisibleState().value)
    }

    @Test
    fun setupControlBarViewModel_init_cameraPermission_granted_then_isEnabled_true() {
        // Arrange
        val mockAppStore = mock<AppStore<ReduxState>> { }
        val setupControlBarViewModel = SetupControlBarViewModel(mockAppStore::dispatch)

        // Act
        setupControlBarViewModel.init(
            PermissionState(
                audioPermissionState = PermissionStatus.NOT_ASKED,
                cameraPermissionState = PermissionStatus.NOT_ASKED
            ),
            CameraState(CameraOperationalStatus.OFF, CameraDeviceSelectionStatus.FRONT, CameraTransmissionStatus.LOCAL),
            AudioState(AudioOperationalStatus.OFF, AudioDeviceSelectionStatus.SPEAKER_SELECTED),
            CallingState(CallingStatus.NONE),
            openAudioDeviceSelectionMenuCallback = { }
        )

        Assert.assertTrue(setupControlBarViewModel.getCameraIsEnabled().value)

        setupControlBarViewModel.update(
            PermissionState(
                audioPermissionState = PermissionStatus.GRANTED,
                cameraPermissionState = PermissionStatus.GRANTED
            ),
            CameraState(CameraOperationalStatus.OFF, CameraDeviceSelectionStatus.FRONT, CameraTransmissionStatus.LOCAL),
            AudioState(AudioOperationalStatus.OFF, AudioDeviceSelectionStatus.SPEAKER_SELECTED),
            CallingState(CallingStatus.NONE),
        )

        Assert.assertTrue(setupControlBarViewModel.getCameraIsEnabled().value)
    }

    @Test
    fun setupControlBarViewModel_init_cameraPermission_denied_then_isEnabled_false() {
        // Arrange
        val mockAppStore = mock<AppStore<ReduxState>> { }
        val setupControlBarViewModel = SetupControlBarViewModel(mockAppStore::dispatch)

        // Act
        setupControlBarViewModel.init(
            PermissionState(
                audioPermissionState = PermissionStatus.NOT_ASKED,
                cameraPermissionState = PermissionStatus.NOT_ASKED
            ),
            CameraState(CameraOperationalStatus.OFF, CameraDeviceSelectionStatus.FRONT, CameraTransmissionStatus.LOCAL),
            AudioState(AudioOperationalStatus.OFF, AudioDeviceSelectionStatus.SPEAKER_SELECTED),
            CallingState(CallingStatus.NONE),
            openAudioDeviceSelectionMenuCallback = { }
        )

        Assert.assertTrue(setupControlBarViewModel.getCameraIsEnabled().value)

        setupControlBarViewModel.update(
            PermissionState(
                audioPermissionState = PermissionStatus.GRANTED,
                cameraPermissionState = PermissionStatus.DENIED
            ),
            CameraState(CameraOperationalStatus.OFF, CameraDeviceSelectionStatus.FRONT, CameraTransmissionStatus.LOCAL),
            AudioState(AudioOperationalStatus.OFF, AudioDeviceSelectionStatus.SPEAKER_SELECTED),
            CallingState(CallingStatus.NONE),
        )

        Assert.assertFalse(setupControlBarViewModel.getCameraIsEnabled().value)
    }

    @Test
    fun setupControlBarViewModel_update_joinCallIsRequested_true_then_isEnabled_false() {
        // Arrange
        val mockAppStore = mock<AppStore<ReduxState>> { }
        val setupControlBarViewModel = SetupControlBarViewModel(mockAppStore::dispatch)

        // Act
        setupControlBarViewModel.init(
            PermissionState(
                audioPermissionState = PermissionStatus.NOT_ASKED,
                cameraPermissionState = PermissionStatus.NOT_ASKED
            ),
            CameraState(CameraOperationalStatus.OFF, CameraDeviceSelectionStatus.FRONT, CameraTransmissionStatus.LOCAL),
            AudioState(AudioOperationalStatus.OFF, AudioDeviceSelectionStatus.SPEAKER_SELECTED),
            CallingState(CallingStatus.NONE, joinCallIsRequested = false),
            openAudioDeviceSelectionMenuCallback = { }
        )

        Assert.assertTrue(setupControlBarViewModel.getCameraIsEnabled().value)
        Assert.assertTrue(setupControlBarViewModel.getMicIsEnabled().value)
        Assert.assertTrue(setupControlBarViewModel.getDeviceIsEnabled().value)

        setupControlBarViewModel.update(
            PermissionState(
                audioPermissionState = PermissionStatus.GRANTED,
                cameraPermissionState = PermissionStatus.GRANTED
            ),
            CameraState(CameraOperationalStatus.OFF, CameraDeviceSelectionStatus.FRONT, CameraTransmissionStatus.LOCAL),
            AudioState(AudioOperationalStatus.OFF, AudioDeviceSelectionStatus.SPEAKER_SELECTED),
            CallingState(CallingStatus.NONE, joinCallIsRequested = true),
        )

        Assert.assertFalse(setupControlBarViewModel.getCameraIsEnabled().value)
        Assert.assertFalse(setupControlBarViewModel.getMicIsEnabled().value)
        Assert.assertFalse(setupControlBarViewModel.getDeviceIsEnabled().value)
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
            CallingState(callingStatus),
        ) { }
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
            CallingState(callingStatus)
        )
    }
}
