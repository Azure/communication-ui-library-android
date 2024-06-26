// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.setup.components

import com.azure.android.communication.ui.calling.models.CallCompositeAudioVideoMode
import com.azure.android.communication.ui.calling.models.CallCompositeSetupScreenOptions
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

internal class SetupControlBarViewModel(
    private val dispatch: (Action) -> Unit,
) {
    private lateinit var cameraIsEnabledStateFlow: MutableStateFlow<Boolean>
    private lateinit var cameraIsVisibleStateFlow: MutableStateFlow<Boolean>

    private lateinit var micIsEnabledStateFlow: MutableStateFlow<Boolean>
    private lateinit var deviceIsEnabledStateFlow: MutableStateFlow<Boolean>

    private lateinit var visibleStateFlow: MutableStateFlow<Boolean>
    private lateinit var cameraStateFlow: MutableStateFlow<CameraOperationalStatus>
    private lateinit var audioOperationalStatusStateFlow: MutableStateFlow<AudioOperationalStatus>
    private lateinit var audioDeviceSelectionStatusStateFlow: MutableStateFlow<AudioState>

    lateinit var openAudioDeviceSelectionMenu: () -> Unit

    fun init(
        permissionState: PermissionState,
        cameraState: CameraState,
        audioVideoMode: CallCompositeAudioVideoMode,
        audioState: AudioState,
        callingState: CallingState,
        openAudioDeviceSelectionMenuCallback: () -> Unit,
        setupScreenOptions: CallCompositeSetupScreenOptions?,
    ) {
        visibleStateFlow = MutableStateFlow(isVisible(permissionState.audioPermissionState))
        cameraIsEnabledStateFlow = MutableStateFlow(shouldCameraButtonBeEnabled(callingState, permissionState.cameraPermissionState, setupScreenOptions))
        cameraIsVisibleStateFlow = MutableStateFlow(shouldCameraButtonBeVisible(audioVideoMode))

        micIsEnabledStateFlow = MutableStateFlow(shouldMicButtonBeEnabled(callingState, audioState.operation, setupScreenOptions))
        deviceIsEnabledStateFlow = MutableStateFlow(!shouldControlsBeDisabled(callingState))

        cameraStateFlow = MutableStateFlow(cameraState.operation)
        audioOperationalStatusStateFlow = MutableStateFlow(audioState.operation)
        openAudioDeviceSelectionMenu = openAudioDeviceSelectionMenuCallback
        audioDeviceSelectionStatusStateFlow = MutableStateFlow(audioState)

        if (permissionState.audioPermissionState == PermissionStatus.NOT_ASKED) {
            requestAudioPermission()
        }
    }

    fun update(
        permissionState: PermissionState,
        cameraState: CameraState,
        audioVideoMode: CallCompositeAudioVideoMode,
        audioState: AudioState,
        callingState: CallingState,
        setupScreenOptions: CallCompositeSetupScreenOptions?,
    ) {
        visibleStateFlow.value = isVisible(permissionState.audioPermissionState)
        cameraIsEnabledStateFlow.value = shouldCameraButtonBeEnabled(callingState, permissionState.cameraPermissionState, setupScreenOptions)
        cameraIsVisibleStateFlow.value = shouldCameraButtonBeVisible(audioVideoMode)

        micIsEnabledStateFlow.value = shouldMicButtonBeEnabled(callingState, audioState.operation, setupScreenOptions)
        deviceIsEnabledStateFlow.value = !shouldControlsBeDisabled(callingState)

        cameraStateFlow.value = cameraState.operation
        audioOperationalStatusStateFlow.value = audioState.operation
        audioDeviceSelectionStatusStateFlow.value = audioState
    }

    private fun isVisible(audioPermissionState: PermissionStatus): Boolean {
        return audioPermissionState != PermissionStatus.DENIED
    }

    val cameraIsEnabled: StateFlow<Boolean> get() = cameraIsEnabledStateFlow
    val cameraIsVisible: StateFlow<Boolean> get() = cameraIsVisibleStateFlow
    val micIsEnabled: StateFlow<Boolean> get() = micIsEnabledStateFlow

    val deviceIsEnabled: StateFlow<Boolean> get() = deviceIsEnabledStateFlow
    val isVisibleState: StateFlow<Boolean> get() = visibleStateFlow
    val cameraState: StateFlow<CameraOperationalStatus> get() = cameraStateFlow

    val audioOperationalStatusStat: StateFlow<AudioOperationalStatus> get() = audioOperationalStatusStateFlow
    val audioDeviceSelectionStatusState: StateFlow<AudioState> get() = audioDeviceSelectionStatusStateFlow

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

    private fun shouldCameraButtonBeVisible(
        audioVideoMode: CallCompositeAudioVideoMode,
    ): Boolean {
        return audioVideoMode == CallCompositeAudioVideoMode.AUDIO_AND_VIDEO
    }

    private fun shouldCameraButtonBeEnabled(
        callingState: CallingState,
        cameraPermissionState: PermissionStatus,
        setupScreenOptions: CallCompositeSetupScreenOptions?,
    ): Boolean {
        return !shouldControlsBeDisabled(callingState) &&
            cameraPermissionState != PermissionStatus.DENIED &&
            setupScreenOptions?.isCameraButtonEnabled != false
    }

    private fun shouldMicButtonBeEnabled(
        callingState: CallingState,
        audioStateOperation: AudioOperationalStatus,
        setupScreenOptions: CallCompositeSetupScreenOptions?
    ): Boolean {
        return !shouldControlsBeDisabled(callingState) &&
            audioStateOperation != AudioOperationalStatus.PENDING &&
            setupScreenOptions?.isMicrophoneButtonEnabled != false
    }

    private fun shouldControlsBeDisabled(callingState: CallingState): Boolean {
        if (callingState.isDisconnected())
            return false
        return callingState.joinCallIsRequested || callingState.callingStatus != CallingStatus.NONE
    }
}
