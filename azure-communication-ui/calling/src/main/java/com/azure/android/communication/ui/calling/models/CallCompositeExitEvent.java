// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

/**
 * Event with caused throwable.
 */
public final class CallCompositeExitEvent {
    private final CallCompositeErrorEvent errorEvent;

    /**
     * Create {@link CallCompositeExitEvent} with error event.
     *
     * @param errorEvent error event.
     */
    public CallCompositeExitEvent(final CallCompositeErrorEvent errorEvent) {
        this.errorEvent = errorEvent;
    }

    /**
     * Returns the cause of exit or {@code null} if the
     * call end is expected
     *
     * @return {@link CallCompositeErrorEvent}
     */
    public CallCompositeErrorEvent getErrorEvent() {
        return errorEvent;
    }
}
