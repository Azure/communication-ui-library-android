// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

/**
 * CallCompositeIncomingCallInfo for forwarding calling push notifications to UI.
 */
public class CallCompositeIncomingCallInfo {
    private final String callId;

    /**
     * Create {@link CallCompositeIncomingCallInfo}.
     * @param callId call id.
     */
    public CallCompositeIncomingCallInfo(final String callId) {
        this.callId = callId;
    }

    /**
     * Get call id.
     * @return call id.
     */
    public String getCallId() {
        return callId;
    }
}
