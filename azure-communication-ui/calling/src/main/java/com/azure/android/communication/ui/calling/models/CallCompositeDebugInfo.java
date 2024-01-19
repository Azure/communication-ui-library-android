// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

import android.util.Log;

import com.azure.android.communication.ui.BuildConfig;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * A class representing debug information for the Call Composite.
 * It provides functionalities to access call history records, retrieve log files,
 * take screenshots for debugging purposes, and get version information of the UI and calling SDK.
 */
public final class CallCompositeDebugInfo {

    private final List<CallCompositeCallHistoryRecord> callHistoryRecord;
    private final Callable<List<File>> getLogFilesCallable;
    private final Callable<File> takeScreenshot;

    /**
     * Constructor for CallCompositeDebugInfo.
     *
     * @param callHistoryRecord A list of CallCompositeCallHistoryRecord, representing the history of calls.
     * @param getLogFiles A Callable that returns a list of log files.
     * @param takeScreenshot A Callable that captures and returns a screenshot file.
     */
    CallCompositeDebugInfo(final List<CallCompositeCallHistoryRecord> callHistoryRecord,
                           final Callable<List<File>> getLogFiles,
                           final Callable<File> takeScreenshot) {
        this.callHistoryRecord = callHistoryRecord;
        this.getLogFilesCallable = getLogFiles;
        this.takeScreenshot = takeScreenshot;
    }

    /**
     * Returns the history of calls up to 30 days, ordered ascending by the call start date.
     *
     * @return A list of CallCompositeCallHistoryRecord representing the call history.
     */
    public List<CallCompositeCallHistoryRecord> getCallHistoryRecords() {
        return callHistoryRecord;
    }

    /**
     * Retrieves a list of log files. If an error occurs, an empty list is returned and a warning is logged.
     *
     * @return A list of File objects representing the log files.
     */
    public List<File> getLogFiles() {
        try {
            return getLogFilesCallable.call();
        } catch (Exception e) {
            Log.w("CallCompositeDebugInfo", "Failure to get log files: ", e);
            return Collections.emptyList();
        }
    }

    /**
     * Takes a screenshot of the current screen if possible.
     * The file is automatically named and timestamped, and saved in the app's cache directory.
     * Returns null if the screenshot capture fails.
     *
     * @return A File object representing the screenshot, or null if the operation fails.
     */
    public File takeScreenshot() {
        try {
            return takeScreenshot.call();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Returns the version of the calling UI SDK.
     *
     * @return A string representing the UI SDK version.
     */
    public String getCallingUIVersion() {
        return BuildConfig.UI_SDK_VERSION;
    }

    /**
     * Returns the version of the calling SDK.
     *
     * @return A string representing the Call SDK version.
     */
    public String getCallingSDKVersion() {
        return BuildConfig.CALL_SDK_VERSION;
    }
}
