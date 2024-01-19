// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

/**
 * Incoming call end event.
 */
final class CallCompositeIncomingCallEndEvent {
    private final int code;
    private final int subCode;

    /**
     * Creates {@link CallCompositeIncomingCallEndEvent}.
     * @param code call end code.
     * @param subCode call end sub code.
     */
    private CallCompositeIncomingCallEndEvent(final int code, final int subCode) {
        this.code = code;
        this.subCode = subCode;
    }

    /**
     * Get code.
     *
     * @return code
     */
    int getCode() {
        return code;
    }

    /**
     * Get sub code.
     *
     * @return subCode
     */
    int getSubCode() {
        return subCode;
    }
}
