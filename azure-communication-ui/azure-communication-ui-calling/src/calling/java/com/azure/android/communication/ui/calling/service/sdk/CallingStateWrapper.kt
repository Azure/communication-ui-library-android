// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.service.sdk

import com.azure.android.communication.calling.CallState
import com.azure.android.communication.ui.calling.service.CallingService

internal data class CallingStateWrapper(
    val callState: CallState,
    val callEndReason: Int,
    val callEndReasonSubCode: Int = 0
) {
    fun isDeclined() =
        callState == CallState.DISCONNECTED &&
            callEndReason == CallingService.CALL_END_REASON_SUCCESS &&
            callEndReasonSubCode == CallingService.CALL_END_REASON_SUB_CODE_DECLINED

    fun isEvicted() =
        callState == CallState.DISCONNECTED &&
            callEndReason == CallingService.CALL_END_REASON_SUCCESS && (
            callEndReasonSubCode == CallingService.CALL_END_REASON_EVICTED ||
                callEndReasonSubCode == CallingService.CALL_END_REASON_TEAMS_EVICTED
            )
}
