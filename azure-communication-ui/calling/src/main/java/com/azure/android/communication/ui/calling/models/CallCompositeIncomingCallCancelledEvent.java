// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

/**
 * Incoming call cancelled event.
 */
public final class CallCompositeIncomingCallCancelledEvent {
    private final int code;
    private final int subCode;
    private final String callId;

    /**
     * Creates {@link CallCompositeIncomingCallCancelledEvent}.
     * @param code call end code.
     * @param subCode call end sub code.
     * @param callId call id.
     */
    CallCompositeIncomingCallCancelledEvent(final int code,
                                                   final int subCode,
                                                   final String callId) {
        this.code = code;
        this.subCode = subCode;
        this.callId = callId;
    }

    /**
     * Get code.
     *
     * @return code
     */
    public int getCode() {
        return code;
    }

    /**
     * Get sub code.
     *
     * @return subCode
     */
    public int getSubCode() {
        return subCode;
    }

    /**
     * Get call id.
     *
     * @return callId
     */
    public String getCallId() {
        return callId;
    }
}
