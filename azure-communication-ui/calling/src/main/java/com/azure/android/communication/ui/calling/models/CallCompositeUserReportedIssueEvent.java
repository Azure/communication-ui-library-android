// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

import java.io.File;
import java.util.List;

/**
 * Represents an event where a user reports an issue within the call composite experience.
 * This event captures the user message describing the problem, a list of log files
 * containing diagnostic information, and the call identifiers associated with the user's session.
 */
public class CallCompositeUserReportedIssueEvent {
    // The message provided by the user describing the issue they have encountered.
    private final String userMessage;
    // A list of files containing logs that may help diagnose the reported issue.
    private final List<File> logFiles;
    // A list of call identifiers that the user was a part of, to provide context for the reported issue.
    private final List<String> callIds;

    /**
     * Constructs a new {@link CallCompositeUserReportedIssueEvent}.
     *
     * @param userMessage A message provided by the user describing the issue.
     * @param logFiles    A list of files containing diagnostic logs related to the user's experience.
     * @param callIds     A list of identifiers for the calls that the user was involved in.
     */
    public CallCompositeUserReportedIssueEvent(String userMessage, List<File> logFiles, List<String> callIds) {
        this.userMessage = userMessage;
        this.logFiles = logFiles;
        this.callIds = callIds;
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
     * Retrieves the list of log files that may contain information useful for diagnosing the issue.
     *
     * @return The list of log files.
     */
    public List<File> getLogFiles() {
        return logFiles;
    }

    /**
     * Retrieves the list of call identifiers associated with the user's session.
     *
     * @return The list of call IDs.
     */
    public List<String> getCallIds() {
        return callIds;
    }
}
