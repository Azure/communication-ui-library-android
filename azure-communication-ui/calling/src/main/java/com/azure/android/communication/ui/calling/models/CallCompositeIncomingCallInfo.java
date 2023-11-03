// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

/**
 * CallCompositeIncomingCallInfo for forwarding calling push notifications to UI.
 */
public class CallCompositeIncomingCallInfo {
    private final String callId;
    private final String displayName;
    private final String rawId;

    /**
     * Create {@link CallCompositeIncomingCallInfo}.
     *
     * @param callId      call id.
     * @param displayName
     * @param rawId
     */
    public CallCompositeIncomingCallInfo(final String callId,
                                         final String displayName,
                                         final String rawId) {
        this.callId = callId;
        this.displayName = displayName;
        this.rawId = rawId;
    }

    /**
     * Get call id.
     * @return call id.
     */
    public String getCallId() {
        return callId;
    }

    /**
     * Get display name.
     * @return display name.
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Get raw id.
     * @return raw id.
     */
    public String getRawId() {
        return rawId;
    }
}
