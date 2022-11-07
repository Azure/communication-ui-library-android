// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.models;

/**
 * Event with error type and caused throwable.
 */
public final class ChatCompositeErrorEvent {
    private final Throwable cause;
    private final ChatCompositeEventCode code;

    /**
     * Create {@link ChatCompositeErrorEvent} with error code and caused throwable.
     *
     * @param code  Error code {@link ChatCompositeEventCode}.
     * @param cause Throwable that caused an exception.
     */
    public ChatCompositeErrorEvent(final ChatCompositeEventCode code, final Throwable cause) {
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
     * @return the call error code {@link ChatCompositeEventCode} instance.
     */
    public ChatCompositeEventCode getErrorCode() {
        return code;
    }
}
