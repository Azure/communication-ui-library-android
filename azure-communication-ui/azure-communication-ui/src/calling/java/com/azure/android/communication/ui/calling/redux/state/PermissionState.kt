// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.redux.state

internal enum class PermissionStatus {
    UNKNOWN,
    GRANTED,
    DENIED,
    NOT_ASKED,
    REQUESTING,
}

internal data class PermissionState(
    // Microphone used to record audio
    val micPermissionState: PermissionStatus,

    // Camera to share your camera feed
    val cameraPermissionState: PermissionStatus,

    // Phone state in order to detect incoming calls (and hang up)
    val phonePermissionState: PermissionStatus,
)
