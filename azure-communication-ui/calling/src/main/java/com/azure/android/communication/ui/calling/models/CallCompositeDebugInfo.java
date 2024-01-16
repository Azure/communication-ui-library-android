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
 * A Call Composite Debug information.
 */
public final class CallCompositeDebugInfo {

    private final List<CallCompositeCallHistoryRecord> callHistoryRecord;
    private final Callable<List<File>> getLogFilesCallable;
    private final Callable<File> takeScreenshot;

    CallCompositeDebugInfo(final List<CallCompositeCallHistoryRecord> callHistoryRecord,
                           final Callable<List<File>> getLogFiles,
                           final Callable<File> takeScreenshot) {
        this.callHistoryRecord = callHistoryRecord;
        this.getLogFilesCallable = getLogFiles;
        this.takeScreenshot = takeScreenshot;
    }

    /**
     * The history of calls up to 30 days. Ordered ascending by call started date.
     * @return
     */
    public List<CallCompositeCallHistoryRecord> getCallHistoryRecords() {
        return callHistoryRecord;
    }

    public List<File> getLogFiles() {
        try {
            return getLogFilesCallable.call();
        } catch (Exception e) {
            // Warn and return empty list
            Log.w("CallCompositeDebugInfo", "Failure to get log files: ", e);
            return Collections.EMPTY_LIST;
        }
    }

    /**
     * Takes a screenshot if possible.
     *
     * The file will be automatically named and timestamped
     * The file will be saved in the app's cache directory.
     * @return screenshot file
     */
    public File takeScreenshot() {
        return this.takeScreenshot();
    }

    public String getCallingUIVersion() {
        return BuildConfig.UI_SDK_VERSION;
    }

    public String getCallingSDKVersion() {
        return BuildConfig.CALL_SDK_VERSION;
    }
}
