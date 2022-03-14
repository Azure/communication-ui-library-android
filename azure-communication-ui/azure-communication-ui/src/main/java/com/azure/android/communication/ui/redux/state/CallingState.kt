// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.redux.state

internal enum class CallingStatus {
    NONE,
    EARLY_MEDIA,
    CONNECTING,
    RINGING,
    CONNECTED,
    LOCAL_HOLD,
    DISCONNECTING,
    DISCONNECTED,
    IN_LOBBY,
    REMOTE_HOLD,
}

internal data class CallingState(
    val callingStatus: CallingStatus,
    // due to the async nature of the CallingStatus update we need to indicate joining call
    // until we receive CallingStatus.CONNECTING from the SDK.
    val joinCallIsRequested: Boolean = false,
    val isRecording: Boolean = false,
    val isTranscribing: Boolean = false,
)
