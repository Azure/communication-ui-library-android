// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

/**
 * Event with caused throwable.
 */
public final class CallCompositeExitEvent {
    private final Throwable cause;

    /**
     * Create {@link CallCompositeExitEvent} with throwable.
     *
     * @param cause Throwable that caused an exception.
     */
    public CallCompositeExitEvent(final Throwable cause) {
        this.cause = cause;
    }

    /**
     * Returns the cause of this throwable or {@code null} if the
     * cause is nonexistent or unknown. (The cause is the throwable that
     * caused this throwable to get thrown).
     *
     * @return the cause of this throwable or {@code null} if the
     * cause is nonexistent or unknown.
     */
    public Throwable getCause() {
        return cause;
    }
}
