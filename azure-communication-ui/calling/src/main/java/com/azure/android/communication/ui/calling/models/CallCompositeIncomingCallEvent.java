// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

import com.azure.android.communication.common.CommunicationIdentifier;

/**
 * Incoming call event.
 */
public final class CallCompositeIncomingCallEvent {
    private final String callId;
    private final String callerDisplayName;
    private final CommunicationIdentifier callerIdentifier;

    /**
     * Create {@link CallCompositeIncomingCallEvent}.
     *
     * @param callId call id.
     * @param callerDisplayName caller display name.
     * @param callerIdentifier caller CommunicationIdentifier.
     */
    public CallCompositeIncomingCallEvent(final String callId,
                                          final String callerDisplayName,
                                          final CommunicationIdentifier callerIdentifier) {
        this.callId = callId;
        this.callerDisplayName = callerDisplayName;
        this.callerIdentifier = callerIdentifier;
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
     * Get caller communication identifier.
     * @return {@link CommunicationIdentifier}.
     */
    public CommunicationIdentifier getCallerIdentifier() {
        return callerIdentifier;
    }
}
