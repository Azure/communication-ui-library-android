// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.configuration.events;

/**
 * Event with error type and caused throwable.
 */
public final class ErrorEvent<T> {
    private final Throwable cause;
    private final T code;

    /**
     * Create {@link ErrorEvent} with error code and caused throwable.
     *
     * @param code  error code
     * @param cause   throwable that caused an exception
     */
    public ErrorEvent(final T code, final Throwable cause) {
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
     * @return the call error code {@code T} instance
     */
    public T getErrorCode() {
        return code;
    }
}
