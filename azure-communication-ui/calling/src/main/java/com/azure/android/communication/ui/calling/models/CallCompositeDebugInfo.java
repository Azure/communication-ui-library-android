// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

import android.util.Log;

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

    CallCompositeDebugInfo(final List<CallCompositeCallHistoryRecord> callHistoryRecord,
                           final Callable<List<File>> getLogFiles) {
        this.callHistoryRecord = callHistoryRecord;
        this.getLogFilesCallable = getLogFiles;
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
}
