// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

import java.util.List;

/**
 * A Call Composite Debug information.
 */
public final class CallCompositeDebugInfo {

    private final List<CallCompositeCallHistoryRecord> callHistoryRecord;

    CallCompositeDebugInfo(final List<CallCompositeCallHistoryRecord> callHistoryRecord) {
        this.callHistoryRecord = callHistoryRecord;
    }

    /**
     * Call history. Ordered ascending by call started date.
     * @return
     */
    public List<CallCompositeCallHistoryRecord> getCallHistoryRecords() {
        return callHistoryRecord;
    }
}
