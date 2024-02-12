// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

import android.graphics.Bitmap;

/**
 * Represents an event where a user reports an issue within the call composite experience.
 * This event captures the user's description of the problem, a screenshot for visual context,
 * and diagnostic information through call identifiers to assist in troubleshooting the reported issue.
 */
public final class CallCompositeUserReportedIssueEvent {
    // The message provided by the user describing the specific issue they encountered.
    private final String userMessage;

    // A screenshot of the current call composite view to visually depict the issue.
    private final Bitmap screenshot;

    // Diagnostic information encapsulating call identifiers to contextualize the reported issue.
    private final CallCompositeDebugInfo debugInfo;

    /**
     * Constructs a new instance of CallCompositeUserReportedIssueEvent.
     *
     * @param userMessage A descriptive message provided by the user about the issue.
     * @param screenshot  A screenshot of the call composite view at the time of the issue, providing a visual context.
     * @param debugInfo   Diagnostic information including call identifiers, aiding in issue resolution.
     */
    public CallCompositeUserReportedIssueEvent(final String userMessage,
                                               final Bitmap screenshot,
                                               final CallCompositeDebugInfo debugInfo) {
        this.userMessage = userMessage;
        this.screenshot = screenshot;
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
     * Retrieves the screenshot of the current call composite view, offering a visual snapshot of the issue.
     *
     * @return The screenshot bitmap.
     */
    public Bitmap getScreenshot() {
        return screenshot;
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
