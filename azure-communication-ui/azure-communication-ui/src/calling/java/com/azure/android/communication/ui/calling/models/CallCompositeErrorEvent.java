// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

/**
 * Event with error type and caused throwable.
 */
public final class CallCompositeErrorEvent {
    private final Throwable cause;
    private final CallCompositeErrorCode code;

    /**
     * Create {@link CallCompositeErrorEvent} with error code and caused throwable.
     *
     * @param code  Error code {@link CallCompositeErrorCode}.
     * @param cause Throwable that caused an exception.
     */
    public CallCompositeErrorEvent(final CallCompositeErrorCode code, final Throwable cause) {
        this.cause = cause;
        this.code = code;
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

    /**
     * Returns the event source.
     *
     * @return the call error code {@link CallCompositeErrorCode} instance.
     */
    public CallCompositeErrorCode getErrorCode() {
        return code;
    }
}
