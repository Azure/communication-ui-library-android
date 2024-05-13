// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.calling.controlbar

import com.azure.android.communication.ui.calling.models.CallCompositeAudioVideoMode
import com.azure.android.communication.ui.calling.models.ParticipantCapabilityType
import com.azure.android.communication.ui.calling.models.ParticipantRole
import com.azure.android.communication.ui.calling.presentation.manager.CapabilitiesManager
import com.azure.android.communication.ui.calling.redux.action.Action
import com.azure.android.communication.ui.calling.redux.action.LocalParticipantAction
import com.azure.android.communication.ui.calling.redux.state.AudioDeviceSelectionStatus
import com.azure.android.communication.ui.calling.redux.state.CallingState
import com.azure.android.communication.ui.calling.redux.state.AudioOperationalStatus
import com.azure.android.communication.ui.calling.redux.state.AudioState
import com.azure.android.communication.ui.calling.redux.state.CallingStatus
import com.azure.android.communication.ui.calling.redux.state.CameraOperationalStatus
import com.azure.android.communication.ui.calling.redux.state.CameraState
import com.azure.android.communication.ui.calling.redux.state.PermissionState
import com.azure.android.communication.ui.calling.redux.state.PermissionStatus
import com.azure.android.communication.ui.calling.redux.state.VisibilityState
import com.azure.android.communication.ui.calling.redux.state.VisibilityStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

internal class ControlBarViewModel(
    private val dispatch: (Action) -> Unit,
    private val capabilitiesManager: CapabilitiesManager,
) {
    private lateinit var isVisibleStateFlow: MutableStateFlow<Boolean>

    // Camera button
    private lateinit var isCameraButtonVisibleFlow: MutableStateFlow<Boolean>
    private lateinit var isCameraButtonEnabledFlow: MutableStateFlow<Boolean>
    private lateinit var cameraStatusFlow: MutableStateFlow<CameraOperationalStatus>

    // Mic button
    private lateinit var isMicButtonVisibleStateFlow: MutableStateFlow<Boolean>
    private lateinit var isMicButtonEnabledFlow: MutableStateFlow<Boolean>
    private lateinit var audioOperationalStatusStateFlow: MutableStateFlow<AudioOperationalStatus>
    private lateinit var audioDeviceSelectionStatusStateFlow: MutableStateFlow<AudioDeviceSelectionStatus>

    // Audio device button
    private lateinit var isAudioDeviceButtonEnabledFlow: MutableStateFlow<Boolean>

    // More button
    private lateinit var isMoreButtonEnabledFlow: MutableStateFlow<Boolean>

    // Callbacks
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
        visibilityState: VisibilityState,
        audioVideoMode: CallCompositeAudioVideoMode,
        capabilities: Set<ParticipantCapabilityType>,
        localParticipantRole: ParticipantRole?,
    ) {
        isVisibleStateFlow = MutableStateFlow(shouldBeVisible(visibilityState))

        isCameraButtonVisibleFlow = MutableStateFlow(
            shouldCameraBeVisibility(
                visibilityState,
                audioVideoMode,
                capabilities,
                localParticipantRole,
            )
        )

        isCameraButtonEnabledFlow = MutableStateFlow(
            shouldCameraBeEnabled(
                permissionState,
                callState.callingStatus,
                cameraState.operation,
            )
        )

        cameraStatusFlow = MutableStateFlow(cameraState.operation)

        audioOperationalStatusStateFlow = MutableStateFlow(audioState.operation)
        audioDeviceSelectionStatusStateFlow = MutableStateFlow(audioState.device)

        isMicButtonVisibleStateFlow = MutableStateFlow(shouldMicBeVisible(capabilities, localParticipantRole))
        isMicButtonEnabledFlow = MutableStateFlow(
            shouldMicBeEnabled(
                audioState,
                callState.callingStatus,
            )
        )

        isAudioDeviceButtonEnabledFlow = MutableStateFlow(shouldAudioDeviceButtonBeEnable(callState.callingStatus))
        isMoreButtonEnabledFlow = MutableStateFlow(shouldMoreButtonBeEnabled(callState.callingStatus))

        requestCallEnd = requestCallEndCallback
        openAudioDeviceSelectionMenu = openAudioDeviceSelectionMenuCallback
        openMoreMenu = openMoreMenuCallback
    }

    fun update(
        permissionState: PermissionState,
        cameraState: CameraState,
        audioState: AudioState,
        callingStatus: CallingStatus,
        visibilityState: VisibilityState,
        audioVideoMode: CallCompositeAudioVideoMode,
        capabilities: Set<ParticipantCapabilityType>,
        localParticipantRole: ParticipantRole?,
    ) {

        isVisibleStateFlow.value = shouldBeVisible(visibilityState)

        isCameraButtonVisibleFlow.value = shouldCameraBeVisibility(
            visibilityState,
            audioVideoMode,
            capabilities,
            localParticipantRole,
        )
        isCameraButtonEnabledFlow.value = shouldCameraBeEnabled(
            permissionState,
            callingStatus,
            cameraState.operation,
        )
        cameraStatusFlow.value = cameraState.operation

        audioOperationalStatusStateFlow.value = audioState.operation
        audioDeviceSelectionStatusStateFlow.value = audioState.device

        isMicButtonVisibleStateFlow.value = shouldMicBeVisible(capabilities, localParticipantRole)
        isMicButtonEnabledFlow.value = shouldMicBeEnabled(
            audioState,
            callingStatus
        )

        isAudioDeviceButtonEnabledFlow.value = shouldAudioDeviceButtonBeEnable(callingStatus)
        isMoreButtonEnabledFlow.value = shouldMoreButtonBeEnabled(callingStatus)
    }

    val isVisible: StateFlow<Boolean> get() = isVisibleStateFlow

    val isCameraButtonVisible: StateFlow<Boolean> get() = isCameraButtonVisibleFlow
    val isCameraButtonEnabled: StateFlow<Boolean> get() = isCameraButtonEnabledFlow
    val cameraStatus: StateFlow<CameraOperationalStatus> get() = cameraStatusFlow

    val isMicButtonVisibleState: StateFlow<Boolean> get() = isMicButtonVisibleStateFlow
    val isMicButtonEnabled: StateFlow<Boolean> get() = isMicButtonEnabledFlow
    val audioOperationalStatus: StateFlow<AudioOperationalStatus> get() = audioOperationalStatusStateFlow

    val isAudioDeviceButtonEnabled: StateFlow<Boolean> get() = isAudioDeviceButtonEnabledFlow
    val audioDeviceSelection: StateFlow<AudioDeviceSelectionStatus> get() = audioDeviceSelectionStatusStateFlow

    val isMoreButtonEnabled: StateFlow<Boolean> get() = isMoreButtonEnabledFlow

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

    private fun shouldBeVisible(
        visibilityState: VisibilityState,
    ): Boolean {
        return visibilityState.status != VisibilityStatus.PIP_MODE_ENTERED
    }

    private fun shouldCameraBeVisibility(
        visibilityState: VisibilityState,
        audioVideoMode: CallCompositeAudioVideoMode,
        capabilities: Set<ParticipantCapabilityType>,
        localParticipantRole: ParticipantRole?,
    ): Boolean {
        return visibilityState.status != VisibilityStatus.PIP_MODE_ENTERED &&
            audioVideoMode != CallCompositeAudioVideoMode.AUDIO_ONLY &&
            capabilitiesManager.hasCapability(
                capabilities,
                localParticipantRole,
                ParticipantCapabilityType.TURN_VIDEO_ON,
                )
    }

    private fun shouldCameraBeEnabled(
        permissionState: PermissionState,
        callingStatus: CallingStatus,
        operation: CameraOperationalStatus,
    ): Boolean {
        return permissionState.cameraPermissionState != PermissionStatus.DENIED &&
            callingStatus == CallingStatus.CONNECTED &&
            operation != CameraOperationalStatus.PENDING
    }

    private fun shouldMicBeVisible(capabilities: Set<ParticipantCapabilityType>, localParticipantRole: ParticipantRole?): Boolean {
        return capabilitiesManager.hasCapability(
            capabilities,
            localParticipantRole,
            ParticipantCapabilityType.UNMUTE_MICROPHONE,
            )
    }
    private fun shouldMicBeEnabled(
        audioState: AudioState,
        callingStatus: CallingStatus,
    ): Boolean {
        return audioState.operation != AudioOperationalStatus.PENDING &&
            callingStatus == CallingStatus.CONNECTED
    }

    private fun shouldAudioDeviceButtonBeEnable(callingStatus: CallingStatus): Boolean {
        return callingStatus == CallingStatus.CONNECTED
    }

    private fun shouldMoreButtonBeEnabled(callingStatus: CallingStatus): Boolean {
        return callingStatus == CallingStatus.CONNECTED
    }

    private fun dispatchAction(action: Action) {
        dispatch(action)
    }
}
