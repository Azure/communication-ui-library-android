// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

/**
 * Incoming call end event.
 */
public class CallCompositeIncomingCallEndEvent {
    private final int code;
    private final int subCode;

    public CallCompositeIncomingCallEndEvent(final int code, final int subCode) {
        this.code = code;
        this.subCode = subCode;
    }

    public int getCode() {
        return code;
    }

    public int getSubCode() {
        return subCode;
    }
}
