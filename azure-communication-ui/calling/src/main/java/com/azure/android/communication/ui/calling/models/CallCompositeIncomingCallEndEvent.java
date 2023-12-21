// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

/**
 * Incoming call end event.
 */
public class CallCompositeIncomingCallEndEvent {
    private final int code;
    private final int subCode;

    /**
     * Creates {@link CallCompositeIncomingCallEndEvent}.
     * @param code call end code.
     * @param subCode call end sub code.
     */
    public CallCompositeIncomingCallEndEvent(final int code, final int subCode) {
        this.code = code;
        this.subCode = subCode;
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
}
