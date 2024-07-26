// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.calling.controlbar

import android.content.Context
import com.azure.android.communication.ui.calling.logger.Logger
import com.azure.android.communication.ui.calling.models.CallCompositeAudioVideoMode
import com.azure.android.communication.ui.calling.models.CallCompositeButtonOptions
import com.azure.android.communication.ui.calling.models.CallCompositeCallScreenControlBarOptions
import com.azure.android.communication.ui.calling.models.ParticipantCapabilityType
import com.azure.android.communication.ui.calling.models.createButtonClickEvent
import com.azure.android.communication.ui.calling.presentation.manager.CapabilitiesManager
import com.azure.android.communication.ui.calling.redux.action.Action
import com.azure.android.communication.ui.calling.redux.action.LocalParticipantAction
import com.azure.android.communication.ui.calling.redux.state.AudioDeviceSelectionStatus
import com.azure.android.communication.ui.calling.redux.state.AudioOperationalStatus
import com.azure.android.communication.ui.calling.redux.state.AudioState
import com.azure.android.communication.ui.calling.redux.state.CallingState
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
    private val logger: Logger,
) {
    private lateinit var isVisibleStateFlow: MutableStateFlow<Boolean>

    // Camera button
    private lateinit var isCameraButtonVisibleFlow: MutableStateFlow<Boolean>
    private lateinit var isCameraButtonEnabledFlow: MutableStateFlow<Boolean>
    private lateinit var cameraStatusFlow: MutableStateFlow<CameraOperationalStatus>

    // Mic button
    var isMicButtonVisible: Boolean = true
    private lateinit var isMicButtonEnabledFlow: MutableStateFlow<Boolean>
    private lateinit var audioOperationalStatusStateFlow: MutableStateFlow<AudioOperationalStatus>
    private lateinit var audioDeviceSelectionStatusStateFlow: MutableStateFlow<AudioDeviceSelectionStatus>

    // Audio device button
    var isAudioDeviceButtonVisible: Boolean = true
    private lateinit var isAudioDeviceButtonEnabledFlow: MutableStateFlow<Boolean>

    // More button
    private lateinit var isMoreButtonEnabledFlow: MutableStateFlow<Boolean>

    // Callbacks
    lateinit var requestCallEnd: () -> Unit
    lateinit var openAudioDeviceSelectionMenu: () -> Unit
    lateinit var openMoreMenu: () -> Unit

    var isMoreButtonVisible: Boolean = true

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
        controlBarOptions: CallCompositeCallScreenControlBarOptions?
    ) {
        isVisibleStateFlow = MutableStateFlow(shouldBeVisible(visibilityState))

        isCameraButtonVisibleFlow = MutableStateFlow(
            shouldCameraBeVisibility(
                audioVideoMode,
            )
        )

        isCameraButtonEnabledFlow = MutableStateFlow(
            shouldCameraBeEnabled(
                permissionState,
                callState.callingStatus,
                cameraState.operation,
                capabilities,
            )
        )

        cameraStatusFlow = MutableStateFlow(cameraState.operation)

        audioOperationalStatusStateFlow = MutableStateFlow(audioState.operation)
        audioDeviceSelectionStatusStateFlow = MutableStateFlow(audioState.device)

        isMicButtonEnabledFlow = MutableStateFlow(
            shouldMicBeEnabled(
                audioState,
                callState.callingStatus,
                capabilities,
            )
        )

        isAudioDeviceButtonEnabledFlow =
            MutableStateFlow(shouldAudioDeviceButtonBeEnable(callState.callingStatus))

        isMicButtonVisible = controlBarOptions?.microphoneButton?.isVisible ?: true
        isAudioDeviceButtonVisible = controlBarOptions?.audioDeviceButton?.isVisible ?: true

        isMoreButtonVisible = (
            controlBarOptions?.getCustomButtons()?.any() == true ||
                controlBarOptions?.liveCaptionsToggleButton?.isVisible ?: true ||
                controlBarOptions?.spokenLanguageButton?.isVisible ?: true ||
                controlBarOptions?.captionsLanguageButton?.isVisible ?: true ||
                controlBarOptions?.shareDiagnosticsButton?.isVisible ?: true ||
                controlBarOptions?.reportIssueButton?.isVisible ?: true
            )

        isMoreButtonEnabledFlow = MutableStateFlow(shouldMoreButtonBeEnabled(callState.callingStatus))

        requestCallEnd = requestCallEndCallback
        openAudioDeviceSelectionMenu = openAudioDeviceSelectionMenuCallback
        openMoreMenu = openMoreMenuCallback

        this.cameraButtonOptions = controlBarOptions?.cameraButton
        this.micButtonOptions = controlBarOptions?.microphoneButton
        this.audioDeviceButtonOptions = controlBarOptions?.audioDeviceButton
    }

    fun update(
        permissionState: PermissionState,
        cameraState: CameraState,
        audioState: AudioState,
        callingStatus: CallingStatus,
        visibilityState: VisibilityState,
        audioVideoMode: CallCompositeAudioVideoMode,
        capabilities: Set<ParticipantCapabilityType>,
    ) {

        isVisibleStateFlow.value = shouldBeVisible(visibilityState)

        isCameraButtonVisibleFlow.value = shouldCameraBeVisibility(
            audioVideoMode,
        )
        isCameraButtonEnabledFlow.value = shouldCameraBeEnabled(
            permissionState,
            callingStatus,
            cameraState.operation,
            capabilities,
        )
        cameraStatusFlow.value = cameraState.operation

        audioOperationalStatusStateFlow.value = audioState.operation
        audioDeviceSelectionStatusStateFlow.value = audioState.device

        isMicButtonEnabledFlow.value = shouldMicBeEnabled(
            audioState,
            callingStatus,
            capabilities,
        )

        isAudioDeviceButtonEnabledFlow.value = shouldAudioDeviceButtonBeEnable(callingStatus)
        isMoreButtonEnabledFlow.value = shouldMoreButtonBeEnabled(callingStatus)
    }

    val isVisible: StateFlow<Boolean> get() = isVisibleStateFlow

    val isCameraButtonVisible: StateFlow<Boolean> get() = isCameraButtonVisibleFlow
    val isCameraButtonEnabled: StateFlow<Boolean> get() = isCameraButtonEnabledFlow
    val cameraStatus: StateFlow<CameraOperationalStatus> get() = cameraStatusFlow

    val isMicButtonEnabled: StateFlow<Boolean> get() = isMicButtonEnabledFlow
    val audioOperationalStatus: StateFlow<AudioOperationalStatus> get() = audioOperationalStatusStateFlow

    val isAudioDeviceButtonEnabled: StateFlow<Boolean> get() = isAudioDeviceButtonEnabledFlow
    val audioDeviceSelection: StateFlow<AudioDeviceSelectionStatus> get() = audioDeviceSelectionStatusStateFlow

    val isMoreButtonEnabled: StateFlow<Boolean> get() = isMoreButtonEnabledFlow

    var cameraButtonOptions: CallCompositeButtonOptions? = null
        private set

    var micButtonOptions: CallCompositeButtonOptions? = null
        private set

    var audioDeviceButtonOptions: CallCompositeButtonOptions? = null
        private set

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

    fun cameraButtonClicked(context: Context) {
        callOnClickHandler(context, cameraButtonOptions)
    }

    fun micButtonClicked(context: Context) {
        callOnClickHandler(context, micButtonOptions)
    }

    fun onAudioDeviceClick(context: Context) {
        callOnClickHandler(context, audioDeviceButtonOptions)
    }

    private fun shouldBeVisible(
        visibilityState: VisibilityState,
    ): Boolean {
        return visibilityState.status != VisibilityStatus.PIP_MODE_ENTERED
    }

    private fun shouldCameraBeVisibility(
        audioVideoMode: CallCompositeAudioVideoMode,
    ): Boolean {
        return cameraButtonOptions?.isVisible ?: true &&
            audioVideoMode != CallCompositeAudioVideoMode.AUDIO_ONLY
    }

    private fun shouldCameraBeEnabled(
        permissionState: PermissionState,
        callingStatus: CallingStatus,
        operation: CameraOperationalStatus,
        capabilities: Set<ParticipantCapabilityType>,
    ): Boolean {
        return cameraButtonOptions?.isEnabled ?: true &&
            permissionState.cameraPermissionState != PermissionStatus.DENIED &&
            callingStatus == CallingStatus.CONNECTED &&
            operation != CameraOperationalStatus.PENDING &&
            capabilitiesManager.hasCapability(
                capabilities,
                ParticipantCapabilityType.TURN_VIDEO_ON,
            )
    }

    private fun shouldMicBeEnabled(
        audioState: AudioState,
        callingStatus: CallingStatus,
        capabilities: Set<ParticipantCapabilityType>,
    ): Boolean {
        return micButtonOptions?.isEnabled ?: true &&
            audioState.operation != AudioOperationalStatus.PENDING &&
            callingStatus == CallingStatus.CONNECTED &&
            capabilitiesManager.hasCapability(
                capabilities,
                ParticipantCapabilityType.UNMUTE_MICROPHONE,
            )
    }

    private fun shouldAudioDeviceButtonBeEnable(callingStatus: CallingStatus): Boolean {
        return audioDeviceButtonOptions?.isEnabled ?: true &&
            callingStatus == CallingStatus.CONNECTED
    }

    private fun shouldMoreButtonBeEnabled(callingStatus: CallingStatus): Boolean {
        return callingStatus == CallingStatus.CONNECTED
    }

    private fun dispatchAction(action: Action) {
        dispatch(action)
    }

    private fun callOnClickHandler(
        context: Context,
        buttonOptions: CallCompositeButtonOptions?,
    ) {
        try {
            buttonOptions?.onClickHandler?.handle(
                createButtonClickEvent(context, buttonOptions)
            )
        } catch (e: Exception) {
            logger.error("Call screen control bar button custom onClick exception.", e)
        }
    }
}
