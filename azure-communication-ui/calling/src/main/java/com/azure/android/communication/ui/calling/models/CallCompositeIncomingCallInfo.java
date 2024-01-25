// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

/**
 * CallCompositeIncomingCallInfo
 */
class CallCompositeIncomingCallInfo {
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
    CallCompositeIncomingCallInfo(final String callId,
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
    String getCallId() {
        return callId;
    }

    /**
     * Get caller display name.
     * @return caller display name.
     */
    String getCallerDisplayName() {
        return callerDisplayName;
    }

    /**
     * Get caller raw id.
     * @return caller raw id.
     */
    String getCallerIdentifierRawId() {
        return callerIdentifierRawId;
    }
}
