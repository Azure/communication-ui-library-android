// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.presentation.fragment.setup.components

import com.azure.android.communication.ui.redux.action.Action
import com.azure.android.communication.ui.redux.action.LocalParticipantAction
import com.azure.android.communication.ui.redux.action.PermissionAction
import com.azure.android.communication.ui.redux.state.AudioDeviceSelectionStatus
import com.azure.android.communication.ui.redux.state.AudioOperationalStatus
import com.azure.android.communication.ui.redux.state.AudioState
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
    private lateinit var audioDeviceSelectionStatusStateFlow: MutableStateFlow<AudioDeviceSelectionStatus>

    fun init(
        permissionState: PermissionState,
        cameraState: CameraState,
        audioState: AudioState,
    ) {
        cameraPermissionStateFlow = MutableStateFlow(permissionState.cameraPermissionState)
        micPermissionStateFlow = MutableStateFlow(permissionState.audioPermissionState)
        cameraStateFlow = MutableStateFlow(cameraState)
        audioOperationalStatusStateFlow = MutableStateFlow(audioState.operation)
        audioDeviceSelectionStatusStateFlow = MutableStateFlow(audioState.device)
    }

    fun update(
        permissionState: PermissionState,
        cameraState: CameraState,
        audioState: AudioState,
    ) {
        cameraPermissionStateFlow.value = permissionState.cameraPermissionState
        micPermissionStateFlow.value = permissionState.audioPermissionState
        cameraStateFlow.value = cameraState
        audioOperationalStatusStateFlow.value = audioState.operation
        audioDeviceSelectionStatusStateFlow.value = audioState.device
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

    fun getAudioDeviceSelectionStatusStateFlow(): StateFlow<AudioDeviceSelectionStatus> {
        return audioDeviceSelectionStatusStateFlow
    }

    fun requestAudioPermission() {
        dispatchAction(action = PermissionAction.AudioPermissionRequested())
    }

    fun turnCameraOn() {
        dispatchAction(action = LocalParticipantAction.CameraPreviewOnRequested())
    }

    fun turnCameraOff() {
        dispatchAction(action = LocalParticipantAction.CameraPreviewOffTriggered())
    }

    fun turnMicOn() {
        dispatchAction(action = LocalParticipantAction.MicPreviewOnTriggered())
    }

    fun turnMicOff() {
        dispatchAction(action = LocalParticipantAction.MicPreviewOffTriggered())
    }

    private fun dispatchAction(action: Action) {
        dispatch(action)
    }
}
