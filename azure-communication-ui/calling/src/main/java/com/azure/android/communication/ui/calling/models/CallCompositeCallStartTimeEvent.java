// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

import java.util.Date;

/**
 * Event with call start time.
 */
public class CallCompositeCallStartTimeEvent {
    private final Date startTime;

    /**
     * Create {@link CallCompositeCallStartTimeEvent} with call start time.
     *
     * @param startTime call start time.
     */
    CallCompositeCallStartTimeEvent(final Date startTime) {
        this.startTime = startTime;
    }

    /**
     * Returns the call start time.
     *
     * @return the call start time.
     */
    public Date getStartTime() {
        return startTime;
    }
}
