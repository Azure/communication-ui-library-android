// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

/**
 * Represents a user-reported issue event in a call.
 *
 * - Dispatched when the user submits the support form.
 * - Subscribing to this event enables the support form.
 */
public final class CallCompositeUserReportedIssueEvent {
    // The message provided by the user describing the specific issue they encountered.
    private final String userMessage;

    // Diagnostic information encapsulating call identifiers to contextualize the reported issue.
    private final CallCompositeDebugInfo debugInfo;

    /**
     * Constructs a new instance of CallCompositeUserReportedIssueEvent.
     *
     * @param userMessage A descriptive message provided by the user about the issue.
     * @param debugInfo   Diagnostic information including call identifiers, aiding in issue resolution.
     */
    public CallCompositeUserReportedIssueEvent(final String userMessage,
                                               final CallCompositeDebugInfo debugInfo) {
        this.userMessage = userMessage;
        this.debugInfo = debugInfo;
    }

    /**
     * Retrieves the message provided by the user describing the reported issue.
     *
     * @return The descriptive user message.
     */
    public String getUserMessage() {
        return userMessage;
    }

    /**
     * Retrieves the diagnostic information related to the call, aiding in the analysis of the reported issue.
     *
     * See {@link CallCompositeDebugInfo} for more details.
     *
     * @return The debug information object containing call identifiers and other relevant diagnostic data.
     */
    public CallCompositeDebugInfo getDebugInfo() {
        return debugInfo;
    }
}
