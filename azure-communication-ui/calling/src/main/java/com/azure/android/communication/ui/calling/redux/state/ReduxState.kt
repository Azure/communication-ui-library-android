// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.redux.state

internal data class ReduxState(
    val callState: CallingState,
    val remoteParticipantState: RemoteParticipantsState,
    val localParticipantState: LocalUserState,
    val permissionState: PermissionState,
    val lifecycleState: LifecycleState,
    val errorState: ErrorState,
    val navigationState: NavigationState,
    val audioSessionState: AudioSessionState,
    val callDiagnosticsState: CallDiagnosticsState
) {
    companion object {
        fun createWithParams(
            displayName: String?,
            cameraOnByDefault: Boolean,
            microphoneOnByDefault: Boolean
        ): ReduxState {
            return ReduxState(
                callState = CallingState(CallingStatus.NONE, OperationStatus.NONE),
                remoteParticipantState = RemoteParticipantsState(
                    participantMap = HashMap(),
                    participantMapModifiedTimestamp = 0,
                    dominantSpeakersInfo = emptyList(),
                    dominantSpeakersModifiedTimestamp = 0,
                ),
                localParticipantState = LocalUserState(
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
                    initialCallJoinState = InitialCallControllerState(
                        cameraOnByDefault,
                        microphoneOnByDefault
                    )
                ),
                permissionState = PermissionState(PermissionStatus.UNKNOWN, PermissionStatus.UNKNOWN),
                lifecycleState = LifecycleState(LifecycleStatus.FOREGROUND),
                errorState = ErrorState(fatalError = null, callStateError = null),
                navigationState = NavigationState(NavigationStatus.NONE),
                audioSessionState = AudioSessionState(audioFocusStatus = null),
                callDiagnosticsState = CallDiagnosticsState(networkQualityCallDiagnostic = null, networkCallDiagnostic = null, mediaCallDiagnostic = null)
            )
        }
    }
}
