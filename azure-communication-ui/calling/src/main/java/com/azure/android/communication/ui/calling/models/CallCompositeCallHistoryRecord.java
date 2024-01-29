// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

import java.util.List;

import org.threeten.bp.OffsetDateTime;

/**
 * Call history.
 */
public class CallCompositeCallHistoryRecord {
    private final OffsetDateTime callStartedOn;
    private final List<String> callIds;

    CallCompositeCallHistoryRecord(final OffsetDateTime callStartedOn, final List<String> callIds) {
        this.callStartedOn = callStartedOn;
        this.callIds = callIds;
    }

    /**
     * Get offset date call started on.
     *
     * @return
     */
    public OffsetDateTime getCallStartedOn() {
        return callStartedOn;
    }

    /**
     * Call Id list associated with particular call.
     *
     * @return
     */
    public List<String> getCallIds() {
        return callIds;
    }

    @Override
    public String toString() {
        return "CallCompositeCallHistoryRecord{"
               + "callStartedOn=" + callStartedOn
               + ", callIds=" + callIds
               + '}';
    }
}
