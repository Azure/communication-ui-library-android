// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

import android.util.Log;

import com.azure.android.communication.ui.calling.implementation.BuildConfig;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * A Call Composite Debug information.
 *
 * This class provides access to debugging and diagnostic information related to a communication call composite.
 */
public final class CallCompositeDebugInfo {

    private final List<CallCompositeCallHistoryRecord> callHistoryRecord;
    private final Callable<List<File>> getLogFilesCallable;

    /**
     * Constructs a new CallCompositeDebugInfo instance.
     *
     * @param callHistoryRecord    The history of calls up to 30 days, ordered in ascending order by call start date.
     * @param getLogFiles          A Callable function to retrieve log files
     */
    CallCompositeDebugInfo(final List<CallCompositeCallHistoryRecord> callHistoryRecord,
                           final Callable<List<File>> getLogFiles) {
        this.callHistoryRecord = callHistoryRecord;
        this.getLogFilesCallable = getLogFiles;
    }

    /**
     * Retrieves the history of calls up to 30 days, ordered in ascending order by call start date.
     *
     * @return a list of call history records, or an empty list if the history cannot be retrieved.
     */
    public List<CallCompositeCallHistoryRecord> getCallHistoryRecords() {
        return callHistoryRecord;
    }

    /**
     * Retrieves the log files associated with the communication call composite.
     *
     * @return a list of log files, or an empty list if log files cannot be retrieved.
     */
    public List<File> getLogFiles() {
        try {
            return getLogFilesCallable.call();
        } catch (Exception e) {
            // Warn and return empty list
            Log.w("CallCompositeDebugInfo", "Failure to get log files: ", e);
            return Collections.emptyList();
        }
    }

    /**
     * Gets the version of the calling UI SDK.
     *
     * @return the version of the calling UI SDK.
     */
    public String getCallingUiVersion() {
        return BuildConfig.UI_SDK_VERSION;
    }

    /**
     * Gets the version of the calling SDK.
     *
     * @return the version of the calling SDK.
     */
    public String getCallingSdkVersion() {
        return BuildConfig.CALL_SDK_VERSION;
    }
}
