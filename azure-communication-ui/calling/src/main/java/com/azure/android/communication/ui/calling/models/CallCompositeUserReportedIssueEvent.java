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
    private final List<CallCompositeCallHistoryRecord> history;

    /**
     * Constructs a new {@link CallCompositeUserReportedIssueEvent}.
     *
     * @param userMessage A message provided by the user describing the issue.
     * @param logFiles    A list of files containing diagnostic logs related to the user's experience.
     * @param history     A list of identifiers for the calls that the user was involved in.
     */
    public CallCompositeUserReportedIssueEvent(final String userMessage,
                                               final List<File> logFiles,
                                               final List<CallCompositeCallHistoryRecord> history) {
        this.userMessage = userMessage;
        this.logFiles = logFiles;
        this.history = history;
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
     * Retrieves Call-ID's that may be relevant to the support request
     * @return The list of the CallCompositeHistoryRecords
     */
    public List<CallCompositeCallHistoryRecord> getHistory() {
        return history;
    }
}
