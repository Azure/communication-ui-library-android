// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

import java.util.List;
import org.threeten.bp.LocalDateTime;

/**
 * Call history.
 */
public class CallCompositeCallHistoryRecord {
    private final  LocalDateTime callStartedOn;
    private final List<String> callIds;

    CallCompositeCallHistoryRecord(final LocalDateTime callStartedOn, final List<String> callIds) {
        this.callStartedOn = callStartedOn;
        this.callIds = callIds;
    }

    /**
     * Get local date call started on.
     * @return
     */
    public LocalDateTime getCallStartedOn() {
        return callStartedOn;
    }

    /**
     * Call Id list associated with particular call.
     * @return
     */
    public List<String> getCallIds() {
        return callIds;
    }
}
