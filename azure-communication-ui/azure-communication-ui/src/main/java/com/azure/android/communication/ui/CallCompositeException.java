// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui;

public class CallCompositeException extends RuntimeException {
    /**
     * Constructs a new Calling Composite exception with null as its error message.
     *
     */
    public CallCompositeException() {
        super();
    }
    /**
     * Constructs a new Calling Composite exception with the specified error message.
     *
     * @param errorMessage - the error message.  The error message can be retrieved by the
     *                     getMessage() method
     */
    public CallCompositeException(final String errorMessage) {
        super(errorMessage);
    }
    /**
     * Constructs a new Calling Composite exception with the specified error message and cause. Note
     * that the error message associated with "cause" is not automatically incorporated into this
     * exception's error message.
     *
     * @param errorMessage - the error message.  The error message can be retrieved by the
     *                     getMessage() method
     * @param cause - the cause (which is saved for later retrieval by the getCause() method).  A
     *              null value is permitted, and indicates that the cause is non-existent or unknown.
     */
    public CallCompositeException(final String errorMessage, final Throwable cause) {
        super(errorMessage, cause);
    }
    /**
     * Constructs a new Calling Composite exception with the specified cause and  message of
     * (cause==null ? null : cause.toString()) (which typically contains the class and detail message
     * of cause). This constructor is useful for exceptions that are little more than wrappers for
     * other throwables.
     *
     * @param cause - the cause (which is saved for later retrieval by the Throwable.getCause() method).
     *              A null value is permitted, and indicates that the cause is nonexistent or unknown.
     */
    public CallCompositeException(final Throwable cause) {
        super(cause);
    }
}
