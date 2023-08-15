// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

/**
 * Event with caused throwable.
 */
public final class CallCompositeDismissedEvent {
    private final Throwable cause;

    private final CallCompositeErrorCode errorCode;

    /**
     * Create {@link CallCompositeDismissedEvent} with error code.
     *
     */
    public CallCompositeDismissedEvent() {
        errorCode = null;
        cause = null;
    }

    /**
     * Create {@link CallCompositeDismissedEvent} with error code.
     *
     * @param errorCode error code.
     * @param cause Throwable that caused an exception and CallComposite dismissal.
     */
    public CallCompositeDismissedEvent(final CallCompositeErrorCode errorCode, final Throwable cause) {
        this.errorCode = errorCode;
        this.cause = cause;
    }

    /**
     * If CallComposite dismissed due to an error, returns the {@link CallCompositeErrorCode}
     * or {@code null} if there is no error.
     *
     * @return {@link CallCompositeErrorCode}
     */
    public CallCompositeErrorCode getErrorCode() {
        return errorCode;
    }

    /**
     * If CallComposite dismissed due to an error, returns the {@link Throwable} if available
     * or {@code null} if there is no error.
     *
     * @return the cause of this throwable or {@code null} if the
     * cause is nonexistent or unknown.
     */
    public Throwable getCause() {
        return cause;
    }
}
