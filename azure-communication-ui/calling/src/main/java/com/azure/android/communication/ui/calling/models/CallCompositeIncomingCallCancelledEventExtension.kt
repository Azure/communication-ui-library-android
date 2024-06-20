// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models

internal fun buildCallCompositeIncomingCallCancelledEvent(
    code: Int,
    subCode: Int,
    callId: String,
): CallCompositeIncomingCallCancelledEvent {
    return CallCompositeIncomingCallCancelledEvent(
        code,
        subCode,
        callId
    )
}
