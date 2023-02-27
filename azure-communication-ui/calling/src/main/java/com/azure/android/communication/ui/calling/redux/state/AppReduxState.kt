// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.redux.state

import com.azure.android.communication.ui.calling.models.CallCompositeRoomRole

internal class AppReduxState(
    displayName: String? = null,
    roomRole: CallCompositeRoomRole? = null,
) : ReduxState {
    override var callState: CallingState = CallingState(callingStatus = CallingStatus.NONE, roomRole = roomRole)

    override var remoteParticipantState: RemoteParticipantsState = RemoteParticipantsState(
        HashMap(), 0
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
        )

    override var permissionState: PermissionState =
        PermissionState(PermissionStatus.UNKNOWN, PermissionStatus.UNKNOWN)

    override var privilegeState: PrivilegeState = PrivilegeState(canUseCamera = true, canUseMicrophone = true)

    override var lifecycleState: LifecycleState = LifecycleState(LifecycleStatus.FOREGROUND)

    override var errorState: ErrorState = ErrorState(fatalError = null, callStateError = null)

    override var navigationState: NavigationState = NavigationState(NavigationStatus.SETUP)

    override var audioSessionState: AudioSessionState = AudioSessionState(audioFocusStatus = null)
}
