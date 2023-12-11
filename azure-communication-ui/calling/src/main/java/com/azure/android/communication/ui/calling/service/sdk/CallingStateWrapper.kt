// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.service.sdk

import com.azure.android.communication.calling.CallState
import com.azure.android.communication.ui.calling.error.CallStateError
import com.azure.android.communication.ui.calling.error.ErrorCode
import com.azure.android.communication.ui.calling.models.CallCompositeEventCode
import com.azure.android.communication.ui.calling.redux.state.CallingStatus

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
        internal const val CALL_CAN_NOT_MAKE = 408
        internal const val CALL_END_REASON_TEAMS_EVICTED = 5300
        internal const val CALL_END_REASON_EVICTED = 5000
        internal const val CALL_END_REASON_SUB_CODE_DECLINED = 5854
    }

    internal fun toCallingStatus(): CallingStatus {
        return when (callState) {
            CallState.CONNECTED -> CallingStatus.CONNECTED
            CallState.CONNECTING -> CallingStatus.CONNECTING
            CallState.DISCONNECTED -> CallingStatus.DISCONNECTED
            CallState.DISCONNECTING -> CallingStatus.DISCONNECTING
            CallState.EARLY_MEDIA -> CallingStatus.EARLY_MEDIA
            CallState.RINGING -> CallingStatus.RINGING
            CallState.LOCAL_HOLD -> CallingStatus.LOCAL_HOLD
            CallState.IN_LOBBY -> CallingStatus.IN_LOBBY
            CallState.REMOTE_HOLD -> CallingStatus.REMOTE_HOLD
            else -> CallingStatus.NONE
        }
    }

    internal fun asCallStateError(currentStatus: CallingStatus): CallStateError? {
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
            callEndReason == CALL_CAN_NOT_MAKE ->
                CallStateError(ErrorCode.CALL_CAN_NOT_MAKE, null)
            callEndReason == CALL_END_REASON_DECLINED ->
                CallStateError(ErrorCode.CALL_DECLINED, null)
            callEndReason == CALL_END_REASON_TOKEN_EXPIRED ->
                CallStateError(ErrorCode.TOKEN_EXPIRED, null)
            else -> {
                if (currentStatus == CallingStatus.CONNECTED) {
                    CallStateError(ErrorCode.CALL_END_FAILED, null)
                } else {
                    CallStateError(ErrorCode.CALL_JOIN_FAILED, null)
                }
            }
        }
    }

    private fun callEndedNormally() = when (callEndReason) {
        CALL_END_REASON_SUCCESS, CALL_END_REASON_CANCELED -> true
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
