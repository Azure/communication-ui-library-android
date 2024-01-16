// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

import java.io.File;

/**
 * Represents an event where a user reports an issue within the call composite experience.
 * This event captures the user message describing the problem, a list of log files
 * containing diagnostic information, and the call identifiers associated with the user's session.
 */
public class CallCompositeUserReportedIssueEvent {
    // The message provided by the user describing the issue they have encountered.
    private final String userMessage;
    // A list of files containing logs that may help diagnose the reported issue.

    // A screenshot of the current call composite view
    private final File screenshot;

    // A list of call identifiers that the user was a part of, to provide context for the reported issue.
    private final CallCompositeDebugInfo debugInfo;

    /**
     * Constructs a new {@link CallCompositeUserReportedIssueEvent}.
     *
     * @param userMessage A message provided by the user describing the issue.
     * @param screenshot    A list of files containing diagnostic logs related to the user's experience.
     */
    public CallCompositeUserReportedIssueEvent(final String userMessage,
                                               final File screenshot,
                                               final CallCompositeDebugInfo debugInfo
                                               ) {
        this.userMessage = userMessage;
        this.screenshot = screenshot;
        this.debugInfo = debugInfo;
    }

    /**
     * Retrieves the user's message describing the reported issue.
     *
     * @return The user message.
     */
    public String getUserMessage() {
        return userMessage;
    }

    /**
     * Retrieves a current screenshot of the current call composite view.
     *
     * @return The screenshot file.
     */
    public File getScreenshot() {
        return screenshot;
    }

    /**
     * Gets the DebugInfo for this call composite
     *
     * See {@link CallCompositeDebugInfo}
     */
    public CallCompositeDebugInfo getDebugInfo() {
        return debugInfo;
    }
}
