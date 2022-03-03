// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.presentation.fragment.setup.components

import com.azure.android.communication.ui.redux.action.Action
import com.azure.android.communication.ui.redux.action.LocalParticipantAction
import com.azure.android.communication.ui.redux.action.PermissionAction
import com.azure.android.communication.ui.redux.state.AudioOperationalStatus
import com.azure.android.communication.ui.redux.state.AudioState
import com.azure.android.communication.ui.redux.state.CallingStatus
import com.azure.android.communication.ui.redux.state.CameraState
import com.azure.android.communication.ui.redux.state.PermissionState
import com.azure.android.communication.ui.redux.state.PermissionStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

internal class SetupControlBarViewModel(
    private val dispatch: (Action) -> Unit,
) {
    private lateinit var cameraPermissionStateFlow: MutableStateFlow<PermissionStatus>
    private lateinit var micPermissionStateFlow: MutableStateFlow<PermissionStatus>
    private lateinit var cameraStateFlow: MutableStateFlow<CameraState>
    private lateinit var audioOperationalStatusStateFlow: MutableStateFlow<AudioOperationalStatus>
    private lateinit var audioDeviceSelectionStatusStateFlow: MutableStateFlow<AudioState>
    private lateinit var callingStatusStateFlow: MutableStateFlow<CallingStatus>

    // Does not need StateFlow as tied to AudioDeviceSelectionStatus updates
    lateinit var audioDeviceName: String

    fun init(
        permissionState: PermissionState,
        cameraState: CameraState,
        audioState: AudioState,
        callingStatus: CallingStatus,
    ) {
        cameraPermissionStateFlow = MutableStateFlow(permissionState.cameraPermissionState)
        micPermissionStateFlow = MutableStateFlow(permissionState.audioPermissionState)
        cameraStateFlow = MutableStateFlow(cameraState)
        audioOperationalStatusStateFlow = MutableStateFlow(audioState.operation)
        audioDeviceSelectionStatusStateFlow = MutableStateFlow(audioState)
        callingStatusStateFlow = MutableStateFlow(callingStatus)
        audioDeviceName = ""
    }

    fun update(
        permissionState: PermissionState,
        cameraState: CameraState,
        audioState: AudioState,
        callingStatus: CallingStatus,
    ) {
        audioDeviceName = audioState.bluetoothState.bluetoothDeviceName
        cameraPermissionStateFlow.value = permissionState.cameraPermissionState
        micPermissionStateFlow.value = permissionState.audioPermissionState
        cameraStateFlow.value = cameraState
        audioOperationalStatusStateFlow.value = audioState.operation
        audioDeviceSelectionStatusStateFlow.value = audioState
        callingStatusStateFlow.value = callingStatus
    }

    fun getCameraPermissionState(): StateFlow<PermissionStatus> {
        return cameraPermissionStateFlow
    }

    fun getMicPermissionState(): StateFlow<PermissionStatus> {
        return micPermissionStateFlow
    }

    fun getCameraState(): StateFlow<CameraState> {
        return cameraStateFlow
    }

    fun getAudioOperationalStatusStateFlow(): StateFlow<AudioOperationalStatus> {
        return audioOperationalStatusStateFlow
    }

    fun getAudioDeviceSelectionStatusStateFlow(): StateFlow<AudioState> {
        return audioDeviceSelectionStatusStateFlow
    }

    fun requestAudioPermission() {
        dispatchAction(action = PermissionAction.AudioPermissionRequested())
    }

    fun turnCameraOn() {
        dispatchAction(
            action =
            if (isNotJoiningCall)
                LocalParticipantAction.CameraPreviewOnRequested()
            else {
                LocalParticipantAction.CameraOnRequested()
            }
        )
    }

    fun turnCameraOff() {
        dispatchAction(
            action =
            if (isNotJoiningCall)
                LocalParticipantAction.CameraPreviewOffTriggered()
            else {
                LocalParticipantAction.CameraOffTriggered()
            }
        )
    }

    fun turnMicOn() {
        dispatchAction(
            action =
            if (isNotJoiningCall)
                LocalParticipantAction.MicPreviewOnTriggered()
            else
                LocalParticipantAction.MicOnTriggered()
        )
    }

    fun turnMicOff() {
        dispatchAction(
            action =
            if (isNotJoiningCall)
                LocalParticipantAction.MicPreviewOffTriggered()
            else
                LocalParticipantAction.MicOffTriggered()
        )
    }

    private val isNotJoiningCall: Boolean
        get() {
            return callingStatusStateFlow.value == CallingStatus.NONE
        }

    private fun dispatchAction(action: Action) {
        dispatch(action)
    }
}
