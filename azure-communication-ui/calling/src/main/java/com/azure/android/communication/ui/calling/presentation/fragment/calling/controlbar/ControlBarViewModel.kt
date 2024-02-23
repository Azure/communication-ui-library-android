// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.calling.controlbar

import com.azure.android.communication.ui.calling.redux.action.Action
import com.azure.android.communication.ui.calling.redux.action.LocalParticipantAction
import com.azure.android.communication.ui.calling.redux.state.CallingState
import com.azure.android.communication.ui.calling.redux.state.AudioDeviceSelectionStatus
import com.azure.android.communication.ui.calling.redux.state.AudioOperationalStatus
import com.azure.android.communication.ui.calling.redux.state.AudioState
import com.azure.android.communication.ui.calling.redux.state.CallStatus
import com.azure.android.communication.ui.calling.redux.state.CameraState
import com.azure.android.communication.ui.calling.redux.state.PermissionState
import com.azure.android.communication.ui.calling.redux.state.PermissionStatus
import com.azure.android.communication.ui.calling.redux.state.VisibilityState
import com.azure.android.communication.ui.calling.redux.state.VisibilityStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

internal class ControlBarViewModel(private val dispatch: (Action) -> Unit) {
    private lateinit var cameraStateFlow: MutableStateFlow<CameraModel>
    private lateinit var audioOperationalStatusStateFlow: MutableStateFlow<AudioOperationalStatus>
    private lateinit var audioDeviceSelectionStatusStateFlow: MutableStateFlow<AudioDeviceSelectionStatus>
    private lateinit var shouldEnableMicButtonStateFlow: MutableStateFlow<Boolean>
    private lateinit var onHoldCallStatusStateFlow: MutableStateFlow<Boolean>
    private lateinit var callStateFlow: MutableStateFlow<CallStatus>
    private lateinit var isVisibleStateFlow: MutableStateFlow<Boolean>

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
        openMoreMenuCallback: () -> Unit,
        pipState: VisibilityState,
    ) {
        callStateFlow = MutableStateFlow(callState.callStatus)
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
        isVisibleStateFlow = MutableStateFlow(pipState.status == VisibilityStatus.PIP_MODE_ENTERED)
    }

    fun update(
        permissionState: PermissionState,
        cameraState: CameraState,
        audioState: AudioState,
        callStatus: CallStatus,
        visibilityState: VisibilityState,
    ) {
        callStateFlow.value = callStatus
        cameraStateFlow.value = CameraModel(permissionState.cameraPermissionState, cameraState)
        audioOperationalStatusStateFlow.value = audioState.operation
        audioDeviceSelectionStatusStateFlow.value = audioState.device
        shouldEnableMicButtonStateFlow.value = shouldEnableMicButton(audioState)
        onHoldCallStatusStateFlow.value = callStatus == CallStatus.LOCAL_HOLD
        isVisibleStateFlow.value = visibilityState.status == VisibilityStatus.PIP_MODE_ENTERED
    }

    val isVisible: StateFlow<Boolean> get() = isVisibleStateFlow

    fun getCallStateFlow(): StateFlow<CallStatus> {
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
