// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.redux.state

import org.threeten.bp.OffsetDateTime

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

internal enum class OperationStatus {
    NONE,
    SKIP_SETUP_SCREEN,
}

internal data class CallingState(
    val callingStatus: CallingStatus,
    val operationStatus: OperationStatus,
    var callId: String? = null,
    // due to the async nature of the CallingStatus update we need to indicate joining call
    // until we receive CallingStatus.CONNECTING from the SDK.
    val joinCallIsRequested: Boolean = false,
    val isRecording: Boolean = false,
    val isTranscribing: Boolean = false,
    // set once for the duration of the call in the CallStateReducer when call start requested.
    val callStartDateTime: OffsetDateTime? = null,
)

internal fun CallingState.isDisconnected() = !joinCallIsRequested && CallingStatus.DISCONNECTED == callingStatus
