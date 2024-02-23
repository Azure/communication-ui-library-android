// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.service.sdk

import com.azure.android.communication.calling.CallState
import com.azure.android.communication.ui.calling.error.CallStateError
import com.azure.android.communication.ui.calling.error.ErrorCode
import com.azure.android.communication.ui.calling.models.CallCompositeEventCode
import com.azure.android.communication.ui.calling.redux.state.CallStatus

internal data class CallingStateWrapper(
    val callState: CallState,
    val callEndReason: Int,
    val callEndReasonSubCode: Int = 0
) {
    companion object {
        internal const val CALL_END_REASON_TOKEN_EXPIRED = 401
        internal const val CALL_END_REASON_SUCCESS = 0

        /*
        * Call canceled, locally declined, ended due to an endpoint mismatch issue, or failed to generate media offer.
        * Expected behavior.
        * */
        internal const val CALL_END_REASON_CANCELED = 487

        /*
        * Call globally declined by remote Communication Services participant.
        * Expected behavior.
        * */
        internal const val CALL_END_REASON_DECLINED = 603
        internal const val CALL_END_REASON_TEAMS_EVICTED = 5300
        internal const val CALL_END_REASON_EVICTED = 5000
        internal const val CALL_END_REASON_SUB_CODE_DECLINED = 5854
    }

    internal fun toCallStatus(): CallStatus {
        return when (callState) {
            CallState.CONNECTED -> CallStatus.CONNECTED
            CallState.CONNECTING -> CallStatus.CONNECTING
            CallState.DISCONNECTED -> CallStatus.DISCONNECTED
            CallState.DISCONNECTING -> CallStatus.DISCONNECTING
            CallState.EARLY_MEDIA -> CallStatus.EARLY_MEDIA
            CallState.RINGING -> CallStatus.RINGING
            CallState.LOCAL_HOLD -> CallStatus.LOCAL_HOLD
            CallState.IN_LOBBY -> CallStatus.IN_LOBBY
            CallState.REMOTE_HOLD -> CallStatus.REMOTE_HOLD
            else -> CallStatus.NONE
        }
    }

    internal fun asCallStateError(currentStatus: CallStatus): CallStateError? {
        // NB: order of these checks matter, which likely isn't ideal. Consider refactoring this a bit.
        // E.g. call is considered to end normally after an eviction.
        return when {
            isEvicted() -> CallStateError(
                ErrorCode.CALL_END_FAILED,
                CallCompositeEventCode.CALL_EVICTED
            )
            isDeclined() -> CallStateError(
                ErrorCode.CALL_END_FAILED,
                CallCompositeEventCode.CALL_DECLINED
            )
            callEndedNormally() -> null
            callEndReason == CALL_END_REASON_TOKEN_EXPIRED ->
                CallStateError(ErrorCode.TOKEN_EXPIRED, null)
            else -> {
                if (currentStatus == CallStatus.CONNECTED) {
                    CallStateError(ErrorCode.CALL_END_FAILED, null)
                } else {
                    CallStateError(ErrorCode.CALL_JOIN_FAILED, null)
                }
            }
        }
    }

    private fun callEndedNormally() = when (callEndReason) {
        CALL_END_REASON_SUCCESS, CALL_END_REASON_CANCELED, CALL_END_REASON_DECLINED -> true
        else -> false
    }

    private fun isDeclined() =
        callState == CallState.DISCONNECTED &&
            callEndReason == CALL_END_REASON_SUCCESS &&
            callEndReasonSubCode == CALL_END_REASON_SUB_CODE_DECLINED

    private fun isEvicted() =
        callState == CallState.DISCONNECTED &&
            callEndReason == CALL_END_REASON_SUCCESS && (
            callEndReasonSubCode == CALL_END_REASON_EVICTED ||
                callEndReasonSubCode == CALL_END_REASON_TEAMS_EVICTED
            )
}
