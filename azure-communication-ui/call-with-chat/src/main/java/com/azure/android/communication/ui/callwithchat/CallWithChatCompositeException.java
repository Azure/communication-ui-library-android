// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callwithchat;

import com.azure.android.core.exception.AzureException;

/**
 * Defines the base type of custom Exception that can be thrown by this Library.
 */
public final class CallWithChatCompositeException extends AzureException {

    /**
     * Constructs a new CallWithChat Composite exception with the specified error message and cause. Note
     * that the error message associated with "cause" is not automatically incorporated into this
     * exception's error message.
     *
     * @param message - the error message. The error message can be retrieved by the
     *                     getMessage() method.
     * @param cause - the cause (which is saved for later retrieval by the getCause() method). A
     *              null value is permitted, and indicates that the cause is non-existent or unknown.
     */
    public CallWithChatCompositeException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new CallWithChat Composite exception with the specified cause and  message of
     * (cause==null ? null : cause.toString()) (which typically contains the class and detail message
     * of cause). This constructor is useful for exceptions that are little more than wrappers for
     * other throwables.
     *
     * @param cause - the cause (which is saved for later retrieval by the Throwable.getCause() method).
     *              A null value is permitted, and indicates that the cause is nonexistent or unknown.
     */
    public CallWithChatCompositeException(final Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new CallWithChat Composite exception with the specified error message and cause.
     *
     * @param message - the error message. The error message can be retrieved by the getMessage() method.
     */
    public CallWithChatCompositeException(final String message) {
        super(message);
    }
}
