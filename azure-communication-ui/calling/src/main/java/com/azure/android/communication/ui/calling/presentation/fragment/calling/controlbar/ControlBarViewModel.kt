// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.calling.controlbar

import com.azure.android.communication.ui.calling.redux.action.Action
import com.azure.android.communication.ui.calling.redux.action.LocalParticipantAction
import com.azure.android.communication.ui.calling.redux.state.CallingState
import com.azure.android.communication.ui.calling.redux.state.AudioDeviceSelectionStatus
import com.azure.android.communication.ui.calling.redux.state.AudioOperationalStatus
import com.azure.android.communication.ui.calling.redux.state.AudioState
import com.azure.android.communication.ui.calling.redux.state.CallingStatus
import com.azure.android.communication.ui.calling.redux.state.CameraState
import com.azure.android.communication.ui.calling.redux.state.PermissionState
import com.azure.android.communication.ui.calling.redux.state.PermissionStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

internal class ControlBarViewModel(private val dispatch: (Action) -> Unit) {
    private lateinit var cameraStateFlow: MutableStateFlow<CameraModel>
    private lateinit var audioOperationalStatusStateFlow: MutableStateFlow<AudioOperationalStatus>
    private lateinit var audioDeviceSelectionStatusStateFlow: MutableStateFlow<AudioDeviceSelectionStatus>
    private lateinit var shouldEnableMicButtonStateFlow: MutableStateFlow<Boolean>
    private lateinit var onHoldCallStatusStateFlow: MutableStateFlow<Boolean>
    private lateinit var callStateFlow: MutableStateFlow<CallingStatus>
    lateinit var requestCallEnd: () -> Unit
    lateinit var openAudioDeviceSelectionMenu: () -> Unit
    lateinit var openMoreMenu: () -> Unit

    fun init(
        permissionState: PermissionState,
        cameraState: CameraState,
        audioState: AudioState,
        callState: CallingState,
        requestCallEndCallback: () -> Unit,
        openAudioDeviceSelectionMenuCallback: () -> Unit,
        openMoreMenuCallback: () -> Unit
    ) {
        callStateFlow = MutableStateFlow(callState.callingStatus)
        cameraStateFlow =
            MutableStateFlow(CameraModel(permissionState.cameraPermissionState, cameraState))
        audioOperationalStatusStateFlow = MutableStateFlow(audioState.operation)
        audioDeviceSelectionStatusStateFlow = MutableStateFlow(audioState.device)
        shouldEnableMicButtonStateFlow =
            MutableStateFlow(shouldEnableMicButton(audioState))
        onHoldCallStatusStateFlow = MutableStateFlow(false)
        requestCallEnd = requestCallEndCallback
        openAudioDeviceSelectionMenu = openAudioDeviceSelectionMenuCallback
        openMoreMenu = openMoreMenuCallback
    }

    fun update(
        permissionState: PermissionState,
        cameraState: CameraState,
        audioState: AudioState,
        callingStatus: CallingStatus,
    ) {
        callStateFlow.value = callingStatus
        cameraStateFlow.value = CameraModel(permissionState.cameraPermissionState, cameraState)
        audioOperationalStatusStateFlow.value = audioState.operation
        audioDeviceSelectionStatusStateFlow.value = audioState.device
        shouldEnableMicButtonStateFlow.value = shouldEnableMicButton(audioState)
        onHoldCallStatusStateFlow.value = callingStatus == CallingStatus.LOCAL_HOLD
    }

    fun getCallStateFlow(): StateFlow<CallingStatus> {
        return callStateFlow
    }

    fun getAudioOperationalStatusStateFlow(): StateFlow<AudioOperationalStatus> {
        return audioOperationalStatusStateFlow
    }

    fun getCameraStateFlow(): StateFlow<CameraModel> {
        return cameraStateFlow
    }

    fun getAudioDeviceSelectionStatusStateFlow(): StateFlow<AudioDeviceSelectionStatus> {
        return audioDeviceSelectionStatusStateFlow
    }

    fun getShouldEnableMicButtonStateFlow(): StateFlow<Boolean> {
        return shouldEnableMicButtonStateFlow
    }

    fun getOnHoldCallStatusStateFlowStateFlow(): StateFlow<Boolean> {
        return onHoldCallStatusStateFlow
    }

    fun turnMicOff() {
        dispatchAction(action = LocalParticipantAction.MicOffTriggered())
    }

    fun turnMicOn() {
        dispatchAction(action = LocalParticipantAction.MicOnTriggered())
    }

    fun turnCameraOn() {
        dispatchAction(action = LocalParticipantAction.CameraOnRequested())
    }

    fun turnCameraOff() {
        dispatchAction(action = LocalParticipantAction.CameraOffTriggered())
    }

    private fun shouldEnableMicButton(audioState: AudioState): Boolean {
        return (audioState.operation != AudioOperationalStatus.PENDING)
    }

    private fun dispatchAction(action: Action) {
        dispatch(action)
    }

    internal data class CameraModel(
        val cameraPermissionState: PermissionStatus,
        val cameraState: CameraState,
    )
}
