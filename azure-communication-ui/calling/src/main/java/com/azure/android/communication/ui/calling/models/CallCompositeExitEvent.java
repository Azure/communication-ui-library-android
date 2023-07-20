// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

/**
 * Event with caused throwable.
 */
public final class CallCompositeExitEvent {
    private final CallCompositeErrorCode errorCode;

    /**
     * Create {@link CallCompositeExitEvent} with error code.
     *
     * @param errorCode error code.
     */
    public CallCompositeExitEvent(final CallCompositeErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    /**
     * Returns the cause of last call end or {@code null} if the
     * call end is success
     *
     * @return {@link CallCompositeErrorCode}
     */
    public CallCompositeErrorCode getErrorCode() {
        return errorCode;
    }
}
