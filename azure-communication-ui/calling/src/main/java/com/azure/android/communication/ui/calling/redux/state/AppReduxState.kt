// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.redux.state

import com.azure.android.communication.ui.calling.models.CallCompositeAvMode

internal class AppReduxState(
    displayName: String?,
    cameraOnByDefault: Boolean,
    microphoneOnByDefault: Boolean,
    avMode: CallCompositeAvMode,
) : ReduxState {

    override var callState: CallingState = CallingState(CallingStatus.NONE, OperationStatus.NONE)

    override var remoteParticipantState: RemoteParticipantsState = RemoteParticipantsState(
        participantMap = HashMap(),
        participantMapModifiedTimestamp = 0,
        dominantSpeakersInfo = emptyList(),
        dominantSpeakersModifiedTimestamp = 0,
        lobbyErrorCode = null
    )

    override var localParticipantState: LocalUserState =
        LocalUserState(
            CameraState(
                operation = if (avMode == CallCompositeAvMode.AUDIO_ONLY)
                    CameraOperationalStatus.DISABLED else CameraOperationalStatus.OFF,
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
            initialCallJoinState = InitialCallControllerState(
                cameraOnByDefault,
                microphoneOnByDefault
            ),
            localParticipantRole = null
        )

    override var permissionState: PermissionState =
        PermissionState(PermissionStatus.UNKNOWN, PermissionStatus.UNKNOWN)

    override var lifecycleState: LifecycleState = LifecycleState(LifecycleStatus.FOREGROUND)

    override var errorState: ErrorState = ErrorState(fatalError = null, callStateError = null)

    override var navigationState: NavigationState = NavigationState(NavigationStatus.NONE)

    override var audioSessionState: AudioSessionState = AudioSessionState(audioFocusStatus = null)

    override var pipState: PictureInPictureState = PictureInPictureState(status = PictureInPictureStatus.VISIBLE)

    override var callDiagnosticsState: CallDiagnosticsState = CallDiagnosticsState(networkQualityCallDiagnostic = null, networkCallDiagnostic = null, mediaCallDiagnostic = null)
}
