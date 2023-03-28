// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.setup.components

import com.azure.android.communication.ui.calling.redux.action.Action
import com.azure.android.communication.ui.calling.redux.action.LocalParticipantAction
import com.azure.android.communication.ui.calling.redux.action.PermissionAction
import com.azure.android.communication.ui.calling.redux.state.AudioOperationalStatus
import com.azure.android.communication.ui.calling.redux.state.AudioState
import com.azure.android.communication.ui.calling.redux.state.CallingState
import com.azure.android.communication.ui.calling.redux.state.CallingStatus
import com.azure.android.communication.ui.calling.redux.state.CameraOperationalStatus
import com.azure.android.communication.ui.calling.redux.state.CameraState
import com.azure.android.communication.ui.calling.redux.state.PermissionState
import com.azure.android.communication.ui.calling.redux.state.PermissionStatus
import com.azure.android.communication.ui.calling.redux.state.isDisconnected

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

internal class SetupControlBarViewModel(private val dispatch: (Action) -> Unit) {
    private lateinit var cameraIsEnabledStateFlow: MutableStateFlow<Boolean>
    private lateinit var micIsEnabledStateFlow: MutableStateFlow<Boolean>
    private lateinit var deviceIsEnabledStateFlow: MutableStateFlow<Boolean>

    private lateinit var visibleStateFlow: MutableStateFlow<Boolean>
    private lateinit var cameraStateFlow: MutableStateFlow<CameraOperationalStatus>
    private lateinit var audioOperationalStatusStateFlow: MutableStateFlow<AudioOperationalStatus>
    private lateinit var audioDeviceSelectionStatusStateFlow: MutableStateFlow<AudioState>
    private lateinit var callingStatusStateFlow: MutableStateFlow<CallingStatus>

    lateinit var openAudioDeviceSelectionMenu: () -> Unit

    fun init(
        permissionState: PermissionState,
        cameraState: CameraState,
        audioState: AudioState,
        callingState: CallingState,
        openAudioDeviceSelectionMenuCallback: () -> Unit,
    ) {
        visibleStateFlow = MutableStateFlow(isVisible(permissionState.audioPermissionState))
        cameraIsEnabledStateFlow = MutableStateFlow(permissionState.cameraPermissionState != PermissionStatus.DENIED)
        micIsEnabledStateFlow = MutableStateFlow(isMicEnabled(callingState, audioState.operation))
        deviceIsEnabledStateFlow = MutableStateFlow(!isControlsDisabled(callingState))

        cameraStateFlow = MutableStateFlow(cameraState.operation)
        audioOperationalStatusStateFlow = MutableStateFlow(audioState.operation)
        openAudioDeviceSelectionMenu = openAudioDeviceSelectionMenuCallback
        callingStatusStateFlow = MutableStateFlow(callingState.callingStatus)
        audioDeviceSelectionStatusStateFlow = MutableStateFlow(audioState)

        if (permissionState.audioPermissionState == PermissionStatus.NOT_ASKED) {
            requestAudioPermission()
        }
    }

    fun update(
        permissionState: PermissionState,
        cameraState: CameraState,
        audioState: AudioState,
        callingState: CallingState,
    ) {
        visibleStateFlow.value = isVisible(permissionState.audioPermissionState)
        cameraIsEnabledStateFlow.value = isCameraEnabled(callingState, permissionState.cameraPermissionState)
        micIsEnabledStateFlow.value = isMicEnabled(callingState, audioState.operation)
        deviceIsEnabledStateFlow.value = !isControlsDisabled(callingState)

        cameraStateFlow.value = cameraState.operation
        audioOperationalStatusStateFlow.value = audioState.operation
        audioDeviceSelectionStatusStateFlow.value = audioState
        callingStatusStateFlow.value = callingState.callingStatus
    }

    private fun isVisible(audioPermissionState: PermissionStatus): Boolean {
        return audioPermissionState != PermissionStatus.DENIED
    }

    fun getCameraIsEnabled(): StateFlow<Boolean> = cameraIsEnabledStateFlow
    fun getMicIsEnabled(): StateFlow<Boolean> = micIsEnabledStateFlow
    fun getDeviceIsEnabled(): StateFlow<Boolean> = deviceIsEnabledStateFlow

    fun getIsVisibleState(): StateFlow<Boolean> {
        return visibleStateFlow
    }

    fun getCameraState(): StateFlow<CameraOperationalStatus> {
        return cameraStateFlow
    }

    fun getAudioOperationalStatusStateFlow(): StateFlow<AudioOperationalStatus> {
        return audioOperationalStatusStateFlow
    }

    fun getAudioDeviceSelectionStatusStateFlow(): StateFlow<AudioState> {
        return audioDeviceSelectionStatusStateFlow
    }

    fun turnCameraOn() {
        dispatchAction(
            action = LocalParticipantAction.CameraPreviewOnRequested()
        )
    }

    fun turnCameraOff() {
        dispatchAction(
            action = LocalParticipantAction.CameraPreviewOffTriggered()
        )
    }

    fun turnMicOn() {
        dispatchAction(
            action = LocalParticipantAction.MicPreviewOnTriggered()
        )
    }

    fun turnMicOff() {
        dispatchAction(
            action = LocalParticipantAction.MicPreviewOffTriggered()
        )
    }

    private fun requestAudioPermission() {
        dispatchAction(action = PermissionAction.AudioPermissionRequested())
    }

    private fun dispatchAction(action: Action) {
        dispatch(action)
    }

    private fun isCameraEnabled(callingState: CallingState, cameraPermissionState: PermissionStatus): Boolean {
        return !(isControlsDisabled(callingState) || cameraPermissionState == PermissionStatus.DENIED)
    }

    private fun isMicEnabled(callingState: CallingState, audioStateOperation: AudioOperationalStatus): Boolean {
        return !(isControlsDisabled(callingState) || audioStateOperation == AudioOperationalStatus.PENDING)
    }

    private fun isControlsDisabled(callingState: CallingState): Boolean {
        if (callingState.isDisconnected())
            return false
        return callingState.joinCallIsRequested || callingState.callingStatus != CallingStatus.NONE
    }
}
