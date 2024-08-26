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
    private val localOptions: CallCompositeLocalOptions?
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

    /* <CUSTOM_CALL_HEADER> */
    override var callScreenInfoHeaderState: CallScreenInfoHeaderState = CallScreenInfoHeaderState(
        title = localOptions?.callScreenOptions?.headerOptions?.title,
        subtitle = localOptions?.callScreenOptions?.headerOptions?.subtitle,
    )
    /* </CUSTOM_CALL_HEADER> */

    /* <RTT_POC>
    override var rttState = RttState()
    </RTT_POC> */

    override var buttonOptionsState: ButtonOptionsState = ButtonOptionsState(
        cameraButtonState = DefaultButtonState(
            isEnabled = localOptions?.callScreenOptions?.controlBarOptions?.cameraButton?.isEnabled,
            isVisible = localOptions?.callScreenOptions?.controlBarOptions?.cameraButton?.isVisible,
        )
    )
}
