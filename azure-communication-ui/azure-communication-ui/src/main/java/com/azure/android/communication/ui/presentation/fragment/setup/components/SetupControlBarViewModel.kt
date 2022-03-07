// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.presentation.fragment.setup.components

import com.azure.android.communication.ui.redux.action.Action
import com.azure.android.communication.ui.redux.action.LocalParticipantAction
import com.azure.android.communication.ui.redux.action.PermissionAction
import com.azure.android.communication.ui.redux.state.AudioDeviceSelectionStatus
import com.azure.android.communication.ui.redux.state.AudioOperationalStatus
import com.azure.android.communication.ui.redux.state.AudioState
import com.azure.android.communication.ui.redux.state.CallingStatus
import com.azure.android.communication.ui.redux.state.CameraOperationalStatus
import com.azure.android.communication.ui.redux.state.CameraState
import com.azure.android.communication.ui.redux.state.PermissionState
import com.azure.android.communication.ui.redux.state.PermissionStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

internal class SetupControlBarViewModel(
    private val dispatch: (Action) -> Unit,
) {
    private lateinit var cameraIsEnabledStateFlow: MutableStateFlow<Boolean>
    private lateinit var visibleStateFlow: MutableStateFlow<Boolean>
    private lateinit var cameraStateFlow: MutableStateFlow<CameraOperationalStatus>
    private lateinit var audioOperationalStatusStateFlow: MutableStateFlow<AudioOperationalStatus>
    private lateinit var audioDeviceSelectionStatusStateFlow: MutableStateFlow<AudioDeviceSelectionStatus>
//    private lateinit var callingStatusStateFlow: MutableStateFlow<CallingStatus>
    lateinit var openAudioDeviceSelectionMenu: () -> Unit

    fun init(
        permissionState: PermissionState,
        cameraState: CameraState,
        audioState: AudioState,
        callingStatus: CallingStatus,
        openAudioDeviceSelectionMenuCallback: () -> Unit,
    ) {
        cameraIsEnabledStateFlow = MutableStateFlow(permissionState.cameraPermissionState != PermissionStatus.DENIED)
        visibleStateFlow = MutableStateFlow(isVisible(permissionState.audioPermissionState))

        cameraStateFlow = MutableStateFlow(cameraState.operation)
        audioOperationalStatusStateFlow = MutableStateFlow(audioState.operation)
        audioDeviceSelectionStatusStateFlow = MutableStateFlow(audioState.device)
//        callingStatusStateFlow = MutableStateFlow(callingStatus)
        this.openAudioDeviceSelectionMenu = openAudioDeviceSelectionMenuCallback

        if (permissionState.audioPermissionState == PermissionStatus.NOT_ASKED) {
            requestAudioPermission()
        }
    }

    fun update(
        permissionState: PermissionState,
        cameraState: CameraState,
        audioState: AudioState,
        callingStatus: CallingStatus,
    ) {
        //cameraPermissionStateFlow.value = permissionState.cameraPermissionState
        cameraIsEnabledStateFlow.value =permissionState.cameraPermissionState != PermissionStatus.DENIED
        visibleStateFlow.value = isVisible(permissionState.audioPermissionState)

        cameraStateFlow.value = cameraState.operation
        audioOperationalStatusStateFlow.value = audioState.operation
        audioDeviceSelectionStatusStateFlow.value = audioState.device
//        callingStatusStateFlow.value = callingStatus
    }

    private fun isVisible(audioPermissionState: PermissionStatus): Boolean {
        return audioPermissionState != PermissionStatus.DENIED
    }

    fun getCameraIsEnabled(): StateFlow<Boolean> {
        return cameraIsEnabledStateFlow
    }

    fun getIsVisibleState(): StateFlow<Boolean> {
        return visibleStateFlow
    }

    fun getCameraState(): StateFlow<CameraOperationalStatus> {
        return cameraStateFlow
    }

    fun getAudioOperationalStatusStateFlow(): StateFlow<AudioOperationalStatus> {
        return audioOperationalStatusStateFlow
    }

    fun getAudioDeviceSelectionStatusStateFlow(): StateFlow<AudioDeviceSelectionStatus> {
        return audioDeviceSelectionStatusStateFlow
    }

    private fun requestAudioPermission() {
        dispatchAction(action = PermissionAction.AudioPermissionRequested())
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



    private fun dispatchAction(action: Action) {
        dispatch(action)
    }
}
