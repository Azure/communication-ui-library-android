// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.redux.state

import com.azure.android.communication.ui.calling.error.CallCompositeError
import com.azure.android.communication.ui.calling.models.CallCompositeInternalParticipantRole

internal enum class CameraOperationalStatus {
    PENDING,
    ON,
    OFF,
    PAUSED,
    DISABLED,
}

internal enum class CameraDeviceSelectionStatus {
    FRONT,
    BACK,
    UNKNOWN,
    RIGHT_FRONT,
    LEFT_FRONT,
    PANORAMIC,
    EXTERNAL,
    SWITCHING,
}

internal enum class CameraTransmissionStatus {
    LOCAL,
    REMOTE,
}

internal enum class AudioOperationalStatus {
    ON,
    OFF,
    PENDING,
}

internal enum class AudioDeviceSelectionStatus {
    SPEAKER_SELECTED,
    SPEAKER_REQUESTED,
    RECEIVER_SELECTED,
    RECEIVER_REQUESTED,
    BLUETOOTH_SCO_SELECTED,
    BLUETOOTH_SCO_REQUESTED,
}

internal data class CameraState(
    val operation: CameraOperationalStatus,
    val device: CameraDeviceSelectionStatus,
    val transmission: CameraTransmissionStatus,
    val camerasCount: Int = 0,
    val error: CallCompositeError? = null,
)

internal data class AudioState(
    val operation: AudioOperationalStatus,
    val device: AudioDeviceSelectionStatus,
    val bluetoothState: BluetoothState,
    val error: CallCompositeError? = null,
    val isHeadphonePlugged: Boolean = false,
)

internal data class BluetoothState(
    val available: Boolean,
    val deviceName: String,
)

internal data class InitialCallControllerState(
    val startWithCameraOn: Boolean,
    val startWithMicrophoneOn: Boolean,
)

internal data class LocalUserState(
    val cameraState: CameraState,
    val audioState: AudioState,
    val videoStreamID: String?,
    val displayName: String?,
    val initialCallJoinState: InitialCallControllerState =
        InitialCallControllerState(
            false,
            false,
        ),
    val localParticipantRole: CallCompositeInternalParticipantRole?,
)
