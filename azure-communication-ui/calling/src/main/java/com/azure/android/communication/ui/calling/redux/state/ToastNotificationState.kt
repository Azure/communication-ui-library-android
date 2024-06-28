// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.redux.state

internal enum class ToastNotificationKind {
    NETWORK_RECEIVE_QUALITY,
    NETWORK_SEND_QUALITY,
    NETWORK_RECONNECTION_QUALITY,
    NETWORK_UNAVAILABLE,
    NETWORK_RELAYS_UNREACHABLE,
    SPEAKING_WHILE_MICROPHONE_IS_MUTED,
    CAMERA_START_FAILED,
    CAMERA_START_TIMED_OUT,
    SOME_FEATURES_LOST,
    SOME_FEATURES_GAINED,
}

internal data class ToastNotificationState(
    val kind: ToastNotificationKind?,
)
