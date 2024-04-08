// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

/**
 * Event with call state.
 */
public final class CallCompositeCallStateChangedEvent {
    private final CallCompositeCallStateCode code;

    /**
     * Create {@link CallCompositeCallStateChangedEvent} with call state.
     *
     * @param code call state {@link CallCompositeCallStateCode}.
     */
    public CallCompositeCallStateChangedEvent(final CallCompositeCallStateCode code) {
        this.code = code;
    }

    /**
     * Returns the call state.
     *
     * @return the call state {@link CallCompositeCallStateCode} instance.
     */
    public CallCompositeCallStateCode getCode() {
        return code;
    }
}
