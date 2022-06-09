// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.redux.state

import com.azure.android.communication.ui.calling.error.CallCompositeError

internal enum class CameraOperationalStatus {
    PENDING,
    ON,
    OFF,
    PAUSED,
}

internal enum class CameraDeviceSelectionStatus {
    FRONT,
    BACK,
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
    RECEIVER_SELECTED,
    BLUETOOTH_SCO_SELECTED,
}

internal data class CameraState(
    val operation: CameraOperationalStatus,
    val device: CameraDeviceSelectionStatus,
    val transmission: CameraTransmissionStatus,
    val error: CallCompositeError? = null,
)

internal data class AudioState(
    val operation: AudioOperationalStatus,
    val device: AudioDeviceSelectionStatus,
    val bluetoothState: BluetoothState,
    val previousDevice: AudioDeviceSelectionStatus ?= null,
    val error: CallCompositeError? = null,
    val isHeadphonePlugged: Boolean = false,
)

internal data class BluetoothState(
    val available: Boolean,
    val deviceName: String,
)

internal data class LocalUserState(
    val cameraState: CameraState,
    val audioState: AudioState,
    val videoStreamID: String?,
    val displayName: String?,
)
