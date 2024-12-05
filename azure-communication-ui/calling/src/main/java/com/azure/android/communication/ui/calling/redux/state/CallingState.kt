// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.redux.state

import org.threeten.bp.OffsetDateTime
/*  <CALL_START_TIME>
import java.util.Date
</CALL_START_TIME> */

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
    val callingStatus: CallingStatus = CallingStatus.NONE,
    var callId: String? = null,
    // due to the async nature of the CallStatus update we need to indicate joining call
    // until we receive CallStatus.CONNECTING from the SDK.
    val joinCallIsRequested: Boolean = false,
    val isRecording: Boolean = false,
    val isTranscribing: Boolean = false,
    // set once for the duration of the call in the CallStateReducer when call start requested.
    val callStartDateTime: OffsetDateTime? = null,

    /**
     * Indicates if call has already been started with default camera and mic parameters once.
     * We only need to do it once.
     */
    val isDefaultParametersCallStarted: Boolean = false,
    val callEndReasonCode: Int? = null,
    val callEndReasonSubCode: Int? = null,
    /*  <CALL_START_TIME>
    val callStartTime: Date? = null,
    </CALL_START_TIME> */
)

internal fun CallingState.isDisconnected() =
    !joinCallIsRequested && CallingStatus.DISCONNECTED == callingStatus
