// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.redux.state

internal class AppReduxState(
    displayName: String?,
    cameraOnByDefault: Boolean? = false,
    microphoneOnByDefault: Boolean? = false
) : ReduxState {

    override var callState: CallingState = CallingState(CallingStatus.NONE, OperationStatus.NONE)

    override var remoteParticipantState: RemoteParticipantsState = RemoteParticipantsState(
        HashMap(), 0
    )

    private val cameraOperationStatus = if (cameraOnByDefault == true) {
        CameraOperationalStatus.ON
    } else {
        CameraOperationalStatus.OFF
    }

    private val audioOperationStatus = if (microphoneOnByDefault == true) {
        AudioOperationalStatus.ON
    } else {
        AudioOperationalStatus.OFF
    }

    override var localParticipantState: LocalUserState =
        LocalUserState(
            CameraState(
                operation = cameraOperationStatus,
                device = CameraDeviceSelectionStatus.FRONT,
                transmission = CameraTransmissionStatus.LOCAL
            ),
            AudioState(
                operation = audioOperationStatus,
                device = AudioDeviceSelectionStatus.SPEAKER_SELECTED,
                bluetoothState = BluetoothState(
                    available = false,
                    deviceName = ""
                )
            ),
            videoStreamID = null,
            displayName = displayName,
        )

    override var permissionState: PermissionState =
        PermissionState(PermissionStatus.UNKNOWN, PermissionStatus.UNKNOWN)

    override var lifecycleState: LifecycleState = LifecycleState(LifecycleStatus.FOREGROUND)

    override var errorState: ErrorState = ErrorState(fatalError = null, callStateError = null)

    override var navigationState: NavigationState = NavigationState(NavigationStatus.NONE)

    override var audioSessionState: AudioSessionState = AudioSessionState(audioFocusStatus = null)
}
