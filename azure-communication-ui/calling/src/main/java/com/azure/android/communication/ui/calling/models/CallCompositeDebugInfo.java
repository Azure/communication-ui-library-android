// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

import java.util.List;

/**
 * A Call Composite Debug information.
 */
public final class CallCompositeDebugInfo {

    private final List<CallCompositeCallHistoryRecord> callHistoryRecordList;

    CallCompositeDebugInfo(final List<CallCompositeCallHistoryRecord> callHistoryRecordList) {
        this.callHistoryRecordList = callHistoryRecordList;
    }

    public List<CallCompositeCallHistoryRecord> getCallHistoryRecordList() {
        return callHistoryRecordList;
    }
}
