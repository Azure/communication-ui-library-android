// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.redux.state

import com.azure.android.communication.ui.calling.models.CallCompositeAudioVideoMode
import com.azure.android.communication.ui.calling.models.CallCompositeLocalOptions
import com.azure.android.communication.ui.calling.models.ParticipantCapabilityType

internal class AppReduxState(
    displayName: String?,
    cameraOnByDefault: Boolean = false,
    microphoneOnByDefault: Boolean = false,
    skipSetupScreen: Boolean = false,
    avMode: CallCompositeAudioVideoMode = CallCompositeAudioVideoMode.AUDIO_AND_VIDEO,
    showCaptionsUI: Boolean = true,
    private val localOptions: CallCompositeLocalOptions? = null
) : ReduxState {

    fun copy(): AppReduxState {
        return AppReduxState(
            displayName = localParticipantState.displayName,
            cameraOnByDefault = localParticipantState.initialCallJoinState.startWithCameraOn,
            microphoneOnByDefault = localParticipantState.initialCallJoinState.startWithMicrophoneOn,
            skipSetupScreen = localParticipantState.initialCallJoinState.skipSetupScreen,
            avMode = localParticipantState.audioVideoMode,
            showCaptionsUI = captionsState.isCaptionsUIEnabled,
            localOptions = localOptions
        )
    }

    override var callState: CallingState = CallingState()

    override var remoteParticipantState: RemoteParticipantsState = RemoteParticipantsState(
        participantMap = HashMap(),
        participantMapModifiedTimestamp = 0,
        dominantSpeakersInfo = emptyList(),
        dominantSpeakersModifiedTimestamp = 0,
        lobbyErrorCode = null,
        totalParticipantCount = 0,
    )

    override var localParticipantState: LocalUserState =
        LocalUserState(
            CameraState(
                operation = CameraOperationalStatus.OFF,
                device = CameraDeviceSelectionStatus.FRONT,
                transmission = CameraTransmissionStatus.LOCAL
            ),
            AudioState(
                operation = AudioOperationalStatus.OFF,
                device = AudioDeviceSelectionStatus.SPEAKER_SELECTED,
                bluetoothState = BluetoothState(
                    available = false,
                    deviceName = ""
                )
            ),
            videoStreamID = null,
            displayName = displayName,
            initialCallJoinState = InitialCallJoinState(
                startWithCameraOn = cameraOnByDefault,
                startWithMicrophoneOn = microphoneOnByDefault,
                skipSetupScreen = skipSetupScreen,
            ),
            localParticipantRole = null,
            audioVideoMode = avMode,
            capabilities = setOf(
                ParticipantCapabilityType.TURN_VIDEO_ON,
                ParticipantCapabilityType.UNMUTE_MICROPHONE
            ),
            currentCapabilitiesAreDefault = true,
        )

    override var permissionState: PermissionState =
        PermissionState(PermissionStatus.UNKNOWN, PermissionStatus.UNKNOWN)

    override var lifecycleState = LifecycleState(LifecycleStatus.FOREGROUND)

    override var errorState = ErrorState(fatalError = null, callStateError = null)

    override var navigationState = NavigationState(NavigationStatus.NONE)

    override var audioSessionState = AudioSessionState(audioFocusStatus = null)

    override var visibilityState = VisibilityState(status = VisibilityStatus.VISIBLE)

    override var callDiagnosticsState = CallDiagnosticsState(
        networkQualityCallDiagnostic = null,
        networkCallDiagnostic = null,
        mediaCallDiagnostic = null
    )

    override var toastNotificationState: ToastNotificationState = ToastNotificationState(null)

    override var captionsState: CaptionsState = CaptionsState(isCaptionsUIEnabled = showCaptionsUI)

    override var callScreenInfoHeaderState: CallScreenInfoHeaderState = CallScreenInfoHeaderState(
        title = localOptions?.callScreenOptions?.headerViewData?.title,
        subtitle = localOptions?.callScreenOptions?.headerViewData?.subtitle,
    )

    /* <RTT_POC>
    override var rttState = RttState()
    </RTT_POC> */

    override var buttonState: ButtonState = ButtonState(
        callScreenCameraButtonState = DefaultButtonState(
            isEnabled = localOptions?.callScreenOptions?.controlBarOptions?.cameraButton?.isEnabled,
            isVisible = localOptions?.callScreenOptions?.controlBarOptions?.cameraButton?.isVisible,
        ),
        callScreenMicButtonState = DefaultButtonState(
            isEnabled = localOptions?.callScreenOptions?.controlBarOptions?.microphoneButton?.isEnabled,
            isVisible = localOptions?.callScreenOptions?.controlBarOptions?.microphoneButton?.isVisible,
        ),
        callScreenAudioDeviceButtonState = DefaultButtonState(
            isEnabled = localOptions?.callScreenOptions?.controlBarOptions?.audioDeviceButton?.isEnabled,
            isVisible = localOptions?.callScreenOptions?.controlBarOptions?.audioDeviceButton?.isVisible,
        ),
        liveCaptionsButton = DefaultButtonState(
            isEnabled = localOptions?.callScreenOptions?.controlBarOptions?.liveCaptionsButton?.isEnabled,
            isVisible = localOptions?.callScreenOptions?.controlBarOptions?.liveCaptionsButton?.isVisible,
        ),
        liveCaptionsToggleButton = DefaultButtonState(
            isEnabled = localOptions?.callScreenOptions?.controlBarOptions?.liveCaptionsButton?.isEnabled,
            isVisible = localOptions?.callScreenOptions?.controlBarOptions?.liveCaptionsToggleButton?.isVisible,
        ),
        spokenLanguageButton = DefaultButtonState(
            isEnabled = localOptions?.callScreenOptions?.controlBarOptions?.spokenLanguageButton?.isEnabled,
            isVisible = localOptions?.callScreenOptions?.controlBarOptions?.spokenLanguageButton?.isVisible,
        ),
        captionsLanguageButton = DefaultButtonState(
            isEnabled = localOptions?.callScreenOptions?.controlBarOptions?.captionsLanguageButton?.isEnabled,
            isVisible = localOptions?.callScreenOptions?.controlBarOptions?.captionsLanguageButton?.isVisible,
        ),
        shareDiagnosticsButton = DefaultButtonState(
            isEnabled = localOptions?.callScreenOptions?.controlBarOptions?.shareDiagnosticsButton?.isEnabled,
            isVisible = localOptions?.callScreenOptions?.controlBarOptions?.shareDiagnosticsButton?.isVisible,
        ),
        reportIssueButton = DefaultButtonState(
            isEnabled = localOptions?.callScreenOptions?.controlBarOptions?.reportIssueButton?.isEnabled,
            isVisible = localOptions?.callScreenOptions?.controlBarOptions?.reportIssueButton?.isVisible,
        ),
        setupScreenAudioDeviceButtonState = DefaultButtonState(
            isEnabled = localOptions?.setupScreenOptions?.audioDeviceButton?.isEnabled,
            isVisible = localOptions?.setupScreenOptions?.audioDeviceButton?.isVisible,
        ),
        setupScreenCameraButtonState = DefaultButtonState(
            isEnabled = localOptions?.setupScreenOptions?.cameraButton?.isEnabled,
            isVisible = localOptions?.setupScreenOptions?.cameraButton?.isVisible,
        ),
        setupScreenMicButtonState = DefaultButtonState(
            isEnabled = localOptions?.setupScreenOptions?.microphoneButton?.isEnabled,
            isVisible = localOptions?.setupScreenOptions?.microphoneButton?.isVisible,
        ),
        callScreenCustomButtonsState = localOptions?.callScreenOptions?.controlBarOptions?.customButtons?.map {
            CustomButtonState(
                id = it.id,
                isEnabled = it.isEnabled,
                isVisible = it.isVisible,
                title = it.title,
                drawableId = it.drawableId,
            )
        } ?: emptyList(),
        callScreenHeaderCustomButtonsState = localOptions?.callScreenOptions?.headerViewData?.customButtons?.map {
            CustomButtonState(
                id = it.id,
                isEnabled = it.isEnabled,
                isVisible = it.isVisible,
                title = it.title,
                drawableId = it.drawableId,
            )
        } ?: emptyList()
    )
}
