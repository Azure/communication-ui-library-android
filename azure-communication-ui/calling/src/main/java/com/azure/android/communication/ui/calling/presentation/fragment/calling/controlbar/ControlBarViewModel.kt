// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.calling.controlbar

import android.content.Context
import com.azure.android.communication.ui.calling.logger.Logger
import com.azure.android.communication.ui.calling.models.CallCompositeAudioVideoMode
import com.azure.android.communication.ui.calling.models.CallCompositeButtonViewData
import com.azure.android.communication.ui.calling.models.CallCompositeCallScreenControlBarOptions
import com.azure.android.communication.ui.calling.models.ParticipantCapabilityType
import com.azure.android.communication.ui.calling.models.createButtonClickEvent
import com.azure.android.communication.ui.calling.presentation.manager.CapabilitiesManager
import com.azure.android.communication.ui.calling.redux.action.Action
import com.azure.android.communication.ui.calling.redux.action.LocalParticipantAction
import com.azure.android.communication.ui.calling.redux.action.NavigationAction
import com.azure.android.communication.ui.calling.redux.state.AudioDeviceSelectionStatus
import com.azure.android.communication.ui.calling.redux.state.AudioOperationalStatus
import com.azure.android.communication.ui.calling.redux.state.AudioState
import com.azure.android.communication.ui.calling.redux.state.ButtonState
import com.azure.android.communication.ui.calling.redux.state.CallingState
import com.azure.android.communication.ui.calling.redux.state.CallingStatus
import com.azure.android.communication.ui.calling.redux.state.CameraOperationalStatus
import com.azure.android.communication.ui.calling.redux.state.CameraState
import com.azure.android.communication.ui.calling.redux.state.DefaultButtonState
import com.azure.android.communication.ui.calling.redux.state.DeviceConfigurationState
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
    private lateinit var isMicButtonVisibleFlow: MutableStateFlow<Boolean>
    private lateinit var isMicButtonEnabledFlow: MutableStateFlow<Boolean>
    private lateinit var audioOperationalStatusStateFlow: MutableStateFlow<AudioOperationalStatus>
    private lateinit var audioDeviceSelectionStatusStateFlow: MutableStateFlow<AudioDeviceSelectionStatus>

    // Audio device button
    private lateinit var isAudioDeviceButtonVisibleFlow: MutableStateFlow<Boolean>
    private lateinit var isAudioDeviceButtonEnabledFlow: MutableStateFlow<Boolean>

    // More button
    private lateinit var isMoreButtonEnabledFlow: MutableStateFlow<Boolean>

    private var controlBarOptions: CallCompositeCallScreenControlBarOptions? = null

    // Callbacks
    lateinit var requestCallEnd: () -> Unit
    lateinit var openAudioDeviceSelectionMenu: () -> Unit

    private lateinit var isMoreButtonVisibleFlow: MutableStateFlow<Boolean>

    fun init(
        permissionState: PermissionState,
        cameraState: CameraState,
        audioState: AudioState,
        callState: CallingState,
        requestCallEndCallback: () -> Unit,
        openAudioDeviceSelectionMenuCallback: () -> Unit,
        visibilityState: VisibilityState,
        audioVideoMode: CallCompositeAudioVideoMode,
        capabilities: Set<ParticipantCapabilityType>,
        buttonViewDataState: ButtonState,
        controlBarOptions: CallCompositeCallScreenControlBarOptions?,
        deviceConfigurationState: DeviceConfigurationState,
    ) {
        isVisibleStateFlow = MutableStateFlow(shouldBeVisible(visibilityState, deviceConfigurationState))

        isCameraButtonVisibleFlow = MutableStateFlow(
            shouldCameraBeVisibility(
                audioVideoMode,
                buttonViewDataState.callScreenCameraButtonState,
            )
        )

        isCameraButtonEnabledFlow = MutableStateFlow(
            shouldCameraBeEnabled(
                permissionState,
                callState.callingStatus,
                cameraState.operation,
                capabilities,
                buttonViewDataState.callScreenCameraButtonState,
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
                buttonViewDataState.callScreenMicButtonState,
            )
        )

        isAudioDeviceButtonEnabledFlow = MutableStateFlow(
            shouldAudioDeviceButtonBeEnable(
                callState.callingStatus,
                buttonViewDataState.callScreenAudioDeviceButtonState,
            )
        )

        isMicButtonVisibleFlow = MutableStateFlow(shouldMicBeVisible(buttonViewDataState.callScreenMicButtonState))
        isAudioDeviceButtonVisibleFlow = MutableStateFlow(shouldAudioDeviceBeVisible(buttonViewDataState.callScreenMicButtonState))

        isMoreButtonVisibleFlow = MutableStateFlow(shouldMoreButtonBeVisible(buttonViewDataState))

        isMoreButtonEnabledFlow = MutableStateFlow(shouldMoreButtonBeEnabled(callState.callingStatus))

        requestCallEnd = requestCallEndCallback
        openAudioDeviceSelectionMenu = openAudioDeviceSelectionMenuCallback

        this.controlBarOptions = controlBarOptions
    }

    fun update(
        permissionState: PermissionState,
        cameraState: CameraState,
        audioState: AudioState,
        callingStatus: CallingStatus,
        visibilityState: VisibilityState,
        audioVideoMode: CallCompositeAudioVideoMode,
        capabilities: Set<ParticipantCapabilityType>,
        buttonViewDataState: ButtonState,
        deviceConfigurationState: DeviceConfigurationState,
    ) {

        isVisibleStateFlow.value = shouldBeVisible(visibilityState, deviceConfigurationState)

        isCameraButtonVisibleFlow.value = shouldCameraBeVisibility(
            audioVideoMode,
            buttonViewDataState.callScreenCameraButtonState,
        )
        isCameraButtonEnabledFlow.value = shouldCameraBeEnabled(
            permissionState,
            callingStatus,
            cameraState.operation,
            capabilities,
            buttonViewDataState.callScreenCameraButtonState
        )
        cameraStatusFlow.value = cameraState.operation

        audioOperationalStatusStateFlow.value = audioState.operation
        audioDeviceSelectionStatusStateFlow.value = audioState.device

        isMicButtonEnabledFlow.value = shouldMicBeEnabled(
            audioState,
            callingStatus,
            capabilities,
            buttonViewDataState.callScreenMicButtonState,
        )
        isMicButtonVisibleFlow.value = shouldMicBeVisible(buttonViewDataState.callScreenMicButtonState)

        isAudioDeviceButtonEnabledFlow.value = shouldAudioDeviceButtonBeEnable(
            callingStatus, buttonViewDataState.callScreenAudioDeviceButtonState
        )

        isAudioDeviceButtonVisibleFlow.value = shouldAudioDeviceBeVisible(buttonViewDataState.callScreenAudioDeviceButtonState)
        isMoreButtonEnabledFlow.value = shouldMoreButtonBeEnabled(callingStatus)
        isMoreButtonVisibleFlow.value = shouldMoreButtonBeVisible(buttonViewDataState)
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

    val isMicButtonVisible: StateFlow<Boolean> get() = isMicButtonVisibleFlow

    val isAudioDeviceButtonVisible: StateFlow<Boolean> get() = isAudioDeviceButtonVisibleFlow

    val isMoreButtonVisible: StateFlow<Boolean> get() = isMoreButtonVisibleFlow

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
        callOnClickHandler(context, controlBarOptions?.cameraButton)
    }

    fun micButtonClicked(context: Context) {
        callOnClickHandler(context, controlBarOptions?.microphoneButton)
    }

    fun onAudioDeviceClick(context: Context) {
        callOnClickHandler(context, controlBarOptions?.audioDeviceButton)
    }

    fun openMoreMenu() {
        dispatch(NavigationAction.ShowMoreMenu())
    }

    private fun shouldBeVisible(
        visibilityState: VisibilityState,
        deviceConfigurationState: DeviceConfigurationState,
    ): Boolean {
        if (!deviceConfigurationState.isTablet &&
            deviceConfigurationState.isPortrait &&
            deviceConfigurationState.isSoftwareKeyboardVisible
        ) {
            return false
        }
        return visibilityState.status != VisibilityStatus.PIP_MODE_ENTERED
    }

    private fun shouldCameraBeVisibility(
        audioVideoMode: CallCompositeAudioVideoMode,
        cameraButton: DefaultButtonState?,
    ): Boolean {
        return cameraButton?.isVisible ?: true &&
            audioVideoMode != CallCompositeAudioVideoMode.AUDIO_ONLY
    }

    private fun shouldCameraBeEnabled(
        permissionState: PermissionState,
        callingStatus: CallingStatus,
        operation: CameraOperationalStatus,
        capabilities: Set<ParticipantCapabilityType>,
        cameraButton: DefaultButtonState?,
    ): Boolean {
        return cameraButton?.isEnabled ?: true &&
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
        micButton: DefaultButtonState?,
    ): Boolean {
        return micButton?.isEnabled ?: true &&
            audioState.operation != AudioOperationalStatus.PENDING &&
            callingStatus == CallingStatus.CONNECTED &&
            capabilitiesManager.hasCapability(
                capabilities,
                ParticipantCapabilityType.UNMUTE_MICROPHONE,
            )
    }

    private fun shouldMicBeVisible(
        micButton: DefaultButtonState?,
    ): Boolean {
        return micButton?.isVisible ?: true
    }

    private fun shouldAudioDeviceButtonBeEnable(
        callingStatus: CallingStatus,
        audioDeviceButton: DefaultButtonState?,
    ): Boolean {
        return audioDeviceButton?.isEnabled ?: true &&
            callingStatus == CallingStatus.CONNECTED
    }

    private fun shouldAudioDeviceBeVisible(
        audioDeviceButton: DefaultButtonState?,
    ): Boolean {
        return audioDeviceButton?.isVisible ?: true
    }

    private fun shouldMoreButtonBeEnabled(callingStatus: CallingStatus): Boolean {
        return callingStatus == CallingStatus.CONNECTED
    }

    private fun shouldMoreButtonBeVisible(buttonViewDataState: ButtonState): Boolean {
        return buttonViewDataState.callScreenCustomButtonsState.any { it.isVisible != false } ||
            buttonViewDataState.liveCaptionsToggleButton?.isVisible ?: true ||
            buttonViewDataState.spokenLanguageButton?.isVisible ?: true ||
            buttonViewDataState.captionsLanguageButton?.isVisible ?: true ||
            buttonViewDataState.shareDiagnosticsButton?.isVisible ?: true ||
            buttonViewDataState.reportIssueButton?.isVisible ?: true
    }

    private fun dispatchAction(action: Action) {
        dispatch(action)
    }

    private fun callOnClickHandler(
        context: Context,
        buttonOptions: CallCompositeButtonViewData?,
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
