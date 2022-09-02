// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callwithchat.models;

public final class CallWithChatCompositeErrorEvent {
    private final Throwable cause;
    private final CallWithChatCompositeErrorCode code;

    /**
     * Create {@link CallWithChatCompositeErrorEvent} with error code and caused throwable.
     *
     * @param code  Error code {@link CallWithChatCompositeErrorCode}.
     * @param cause Throwable that caused an exception.
     */
    public CallWithChatCompositeErrorEvent(final CallWithChatCompositeErrorCode code, final Throwable cause) {
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
     * @return the call error code {@link CallWithChatCompositeErrorCode} instance.
     */
    public CallWithChatCompositeErrorCode getErrorCode() {
        return code;
    }
}
