// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

/**
 * CallCompositeIncomingCallInfo
 */
public class CallCompositeIncomingCallInfo {
    private final String callId;
    private final String callerDisplayName;
    private final String callerIdentifierRawId;

    /**
     * Create {@link CallCompositeIncomingCallInfo}.
     *
     * @param callId
     * @param callerDisplayName
     * @param callerIdentifierRawId
     */
    public CallCompositeIncomingCallInfo(final String callId,
                                         final String callerDisplayName,
                                         final String callerIdentifierRawId) {
        this.callId = callId;
        this.callerDisplayName = callerDisplayName;
        this.callerIdentifierRawId = callerIdentifierRawId;
    }

    /**
     * Get call id.
     * @return call id.
     */
    public String getCallId() {
        return callId;
    }

    /**
     * Get caller display name.
     * @return caller display name.
     */
    public String getCallerDisplayName() {
        return callerDisplayName;
    }

    /**
     * Get caller raw id.
     * @return caller raw id.
     */
    public String getCallerIdentifierRawId() {
        return callerIdentifierRawId;
    }
}
