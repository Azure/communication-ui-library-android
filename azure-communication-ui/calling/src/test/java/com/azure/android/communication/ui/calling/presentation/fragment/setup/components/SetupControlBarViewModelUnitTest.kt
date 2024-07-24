// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.setup.components

import com.azure.android.communication.ui.calling.models.CallCompositeAudioVideoMode
import com.azure.android.communication.ui.calling.redux.AppStore
import com.azure.android.communication.ui.calling.redux.action.PermissionAction
import com.azure.android.communication.ui.calling.redux.state.ReduxState
import com.azure.android.communication.ui.calling.redux.state.PermissionState
import com.azure.android.communication.ui.calling.redux.state.CameraState
import com.azure.android.communication.ui.calling.redux.state.AudioState
import com.azure.android.communication.ui.calling.redux.state.PermissionStatus
import com.azure.android.communication.ui.calling.redux.state.CameraOperationalStatus
import com.azure.android.communication.ui.calling.redux.state.CameraDeviceSelectionStatus
import com.azure.android.communication.ui.calling.redux.state.CameraTransmissionStatus
import com.azure.android.communication.ui.calling.redux.state.AudioOperationalStatus
import com.azure.android.communication.ui.calling.redux.state.AudioDeviceSelectionStatus
import com.azure.android.communication.ui.calling.redux.state.BluetoothState
import com.azure.android.communication.ui.calling.redux.state.CallingStatus
import com.azure.android.communication.ui.calling.redux.state.CallingState
import org.junit.Assert

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

    @Test
    fun setupControlBarViewModel_init_audioPermission_notAsked_then_dispatchAudioPermissionRequested() {
        // Arrange
        val mockAppStore = mock<AppStore<ReduxState>> {
            on { dispatch(any()) } doAnswer { }
        }
        val setupControlBarViewModel =
            SetupControlBarViewModel(mockAppStore::dispatch)

        // Act
        setupControlBarViewModel.init(
            permissionState = PermissionState(
                audioPermissionState = PermissionStatus.NOT_ASKED,
                cameraPermissionState = PermissionStatus.NOT_ASKED
            ),
            cameraState = CameraState(
                CameraOperationalStatus.OFF,
                CameraDeviceSelectionStatus.FRONT,
                CameraTransmissionStatus.LOCAL
            ),
            audioVideoMode = CallCompositeAudioVideoMode.AUDIO_AND_VIDEO,
            audioState = AudioState(
                AudioOperationalStatus.OFF,
                AudioDeviceSelectionStatus.SPEAKER_SELECTED,
                BluetoothState(available = false, deviceName = "bluetooth")
            ),
            callingState = CallingState(CallingStatus.NONE,),
            openAudioDeviceSelectionMenuCallback = { },
            setupScreenOptions = null
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

        val setupControlBarViewModel =
            SetupControlBarViewModel(mockAppStore::dispatch)

        // Act
        setupControlBarViewModel.init(
            PermissionState(
                audioPermissionState = PermissionStatus.DENIED,
                cameraPermissionState = PermissionStatus.NOT_ASKED
            ),
            CameraState(
                CameraOperationalStatus.OFF,
                CameraDeviceSelectionStatus.FRONT,
                CameraTransmissionStatus.LOCAL
            ),
            audioVideoMode = CallCompositeAudioVideoMode.AUDIO_AND_VIDEO,
            AudioState(
                AudioOperationalStatus.OFF,
                AudioDeviceSelectionStatus.SPEAKER_SELECTED,
                BluetoothState(available = false, deviceName = "bluetooth")
            ),
            CallingState(CallingStatus.NONE,),
            openAudioDeviceSelectionMenuCallback = { },
            setupScreenOptions = null
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
        val setupControlBarViewModel =
            SetupControlBarViewModel(mockAppStore::dispatch)

        // Act
        setupControlBarViewModel.init(
            PermissionState(
                audioPermissionState = PermissionStatus.NOT_ASKED,
                cameraPermissionState = PermissionStatus.NOT_ASKED
            ),
            CameraState(
                CameraOperationalStatus.OFF,
                CameraDeviceSelectionStatus.FRONT,
                CameraTransmissionStatus.LOCAL
            ),
            audioVideoMode = CallCompositeAudioVideoMode.AUDIO_AND_VIDEO,
            AudioState(
                AudioOperationalStatus.OFF,
                AudioDeviceSelectionStatus.SPEAKER_SELECTED,
                BluetoothState(available = false, deviceName = "bluetooth")
            ),
            CallingState(CallingStatus.NONE,),
            openAudioDeviceSelectionMenuCallback = { },
            setupScreenOptions = null
        )

        Assert.assertTrue(setupControlBarViewModel.isVisibleState.value)

        setupControlBarViewModel.update(
            PermissionState(
                audioPermissionState = PermissionStatus.DENIED,
                cameraPermissionState = PermissionStatus.NOT_ASKED
            ),
            CameraState(
                CameraOperationalStatus.OFF,
                CameraDeviceSelectionStatus.FRONT,
                CameraTransmissionStatus.LOCAL
            ),
            audioVideoMode = CallCompositeAudioVideoMode.AUDIO_AND_VIDEO,
            AudioState(
                AudioOperationalStatus.OFF,
                AudioDeviceSelectionStatus.SPEAKER_SELECTED,
                BluetoothState(available = false, deviceName = "bluetooth")
            ),
            CallingState(CallingStatus.NONE,),
            setupScreenOptions = null
        )

        Assert.assertFalse(setupControlBarViewModel.isVisibleState.value)
    }

    @Test
    fun setupControlBarViewModel_init_cameraPermission_granted_then_isEnabled_true() {
        // Arrange
        val mockAppStore = mock<AppStore<ReduxState>> { }
        val setupControlBarViewModel =
            SetupControlBarViewModel(mockAppStore::dispatch)

        // Act
        setupControlBarViewModel.init(
            PermissionState(
                audioPermissionState = PermissionStatus.NOT_ASKED,
                cameraPermissionState = PermissionStatus.NOT_ASKED
            ),
            CameraState(
                CameraOperationalStatus.OFF,
                CameraDeviceSelectionStatus.FRONT,
                CameraTransmissionStatus.LOCAL
            ),
            audioVideoMode = CallCompositeAudioVideoMode.AUDIO_AND_VIDEO,
            AudioState(
                AudioOperationalStatus.OFF,
                AudioDeviceSelectionStatus.SPEAKER_SELECTED,
                BluetoothState(available = false, deviceName = "bluetooth")
            ),
            CallingState(CallingStatus.NONE,),
            openAudioDeviceSelectionMenuCallback = { },
            setupScreenOptions = null
        )

        Assert.assertTrue(setupControlBarViewModel.cameraIsEnabled.value)

        setupControlBarViewModel.update(
            PermissionState(
                audioPermissionState = PermissionStatus.GRANTED,
                cameraPermissionState = PermissionStatus.GRANTED
            ),
            CameraState(
                CameraOperationalStatus.OFF,
                CameraDeviceSelectionStatus.FRONT,
                CameraTransmissionStatus.LOCAL
            ),
            audioVideoMode = CallCompositeAudioVideoMode.AUDIO_AND_VIDEO,
            AudioState(
                AudioOperationalStatus.OFF,
                AudioDeviceSelectionStatus.SPEAKER_SELECTED,
                BluetoothState(available = false, deviceName = "bluetooth")
            ),
            CallingState(CallingStatus.NONE,),
            setupScreenOptions = null
        )

        Assert.assertTrue(setupControlBarViewModel.cameraIsEnabled.value)
    }

    @Test
    fun setupControlBarViewModel_init_cameraPermission_denied_then_isEnabled_false() {
        // Arrange
        val mockAppStore = mock<AppStore<ReduxState>> { }
        val setupControlBarViewModel =
            SetupControlBarViewModel(mockAppStore::dispatch)

        // Act
        setupControlBarViewModel.init(
            PermissionState(
                audioPermissionState = PermissionStatus.NOT_ASKED,
                cameraPermissionState = PermissionStatus.NOT_ASKED
            ),
            CameraState(
                CameraOperationalStatus.OFF,
                CameraDeviceSelectionStatus.FRONT,
                CameraTransmissionStatus.LOCAL
            ),
            audioVideoMode = CallCompositeAudioVideoMode.AUDIO_AND_VIDEO,
            AudioState(
                AudioOperationalStatus.OFF,
                AudioDeviceSelectionStatus.SPEAKER_SELECTED,
                BluetoothState(available = false, deviceName = "bluetooth")
            ),
            CallingState(CallingStatus.NONE,),
            openAudioDeviceSelectionMenuCallback = { },
            setupScreenOptions = null
        )

        Assert.assertTrue(setupControlBarViewModel.cameraIsEnabled.value)

        setupControlBarViewModel.update(
            PermissionState(
                audioPermissionState = PermissionStatus.GRANTED,
                cameraPermissionState = PermissionStatus.DENIED
            ),
            CameraState(
                CameraOperationalStatus.OFF,
                CameraDeviceSelectionStatus.FRONT,
                CameraTransmissionStatus.LOCAL
            ),
            audioVideoMode = CallCompositeAudioVideoMode.AUDIO_AND_VIDEO,
            AudioState(
                AudioOperationalStatus.OFF,
                AudioDeviceSelectionStatus.SPEAKER_SELECTED,
                BluetoothState(available = false, deviceName = "bluetooth")
            ),
            CallingState(CallingStatus.NONE,),
            setupScreenOptions = null
        )

        Assert.assertFalse(setupControlBarViewModel.cameraIsEnabled.value)
    }

    @Test
    fun setupControlBarViewModel_update_joinCallIsRequested_true_then_isEnabled_false() {
        // Arrange
        val mockAppStore = mock<AppStore<ReduxState>> { }
        val setupControlBarViewModel =
            SetupControlBarViewModel(mockAppStore::dispatch)

        // Act
        setupControlBarViewModel.init(
            PermissionState(
                audioPermissionState = PermissionStatus.NOT_ASKED,
                cameraPermissionState = PermissionStatus.NOT_ASKED
            ),
            CameraState(
                CameraOperationalStatus.OFF,
                CameraDeviceSelectionStatus.FRONT,
                CameraTransmissionStatus.LOCAL
            ),
            audioVideoMode = CallCompositeAudioVideoMode.AUDIO_AND_VIDEO,
            AudioState(
                AudioOperationalStatus.OFF,
                AudioDeviceSelectionStatus.SPEAKER_SELECTED,
                BluetoothState(available = false, deviceName = "bluetooth")
            ),
            CallingState(CallingStatus.NONE, joinCallIsRequested = false),
            openAudioDeviceSelectionMenuCallback = { },
            setupScreenOptions = null
        )

        Assert.assertTrue(setupControlBarViewModel.cameraIsEnabled.value)
        Assert.assertTrue(setupControlBarViewModel.micIsEnabled.value)
        Assert.assertTrue(setupControlBarViewModel.audioDeviceButtonEnabled.value)

        setupControlBarViewModel.update(
            PermissionState(
                audioPermissionState = PermissionStatus.GRANTED,
                cameraPermissionState = PermissionStatus.GRANTED
            ),
            CameraState(
                CameraOperationalStatus.OFF,
                CameraDeviceSelectionStatus.FRONT,
                CameraTransmissionStatus.LOCAL
            ),
            audioVideoMode = CallCompositeAudioVideoMode.AUDIO_AND_VIDEO,
            AudioState(
                AudioOperationalStatus.OFF,
                AudioDeviceSelectionStatus.SPEAKER_SELECTED,
                BluetoothState(available = false, deviceName = "bluetooth")
            ),
            CallingState(CallingStatus.NONE, joinCallIsRequested = true),
            setupScreenOptions = null,
        )

        Assert.assertFalse(setupControlBarViewModel.cameraIsEnabled.value)
        Assert.assertFalse(setupControlBarViewModel.micIsEnabled.value)
        Assert.assertFalse(setupControlBarViewModel.audioDeviceButtonEnabled.value)
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
            audioVideoMode = CallCompositeAudioVideoMode.AUDIO_AND_VIDEO,
            AudioState(
                AudioOperationalStatus.OFF,
                AudioDeviceSelectionStatus.SPEAKER_REQUESTED,
                BluetoothState(available = false, deviceName = "bluetooth")
            ),
            CallingState(
                CallingStatus.CONNECTING,
            ),
            openAudioDeviceSelectionMenuCallback = { },
            setupScreenOptions = null
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
            audioVideoMode = CallCompositeAudioVideoMode.AUDIO_AND_VIDEO,
            AudioState(
                AudioOperationalStatus.OFF,
                AudioDeviceSelectionStatus.SPEAKER_REQUESTED,
                BluetoothState(available = false, deviceName = "bluetooth")
            ),
            CallingState(
                CallingStatus.CONNECTING,
            ),
            setupScreenOptions = null
        )
    }
}
