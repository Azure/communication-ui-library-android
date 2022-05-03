// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.configuration.events;

/**
 * Event with error type and caused throwable.
 */
public final class CommunicationUIErrorEvent {
    private final Throwable cause;
    private final CommunicationUIErrorCode code;

    /**
     * Create {@link CommunicationUIErrorEvent} with error code and caused throwable.
     *
     * @param code  error code
     * @param cause throwable that caused an exception
     */
    public CommunicationUIErrorEvent(final CommunicationUIErrorCode code, final Throwable cause) {
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
    public CommunicationUIErrorCode getErrorCode() {
        return code;
    }
}
