// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

/**
 * Event with call state.
 */
public final class CallCompositeCallStateEvent {
    private final CallCompositeCallState callState;

    /**
     * Create {@link CallCompositeCallStateEvent} with call state.
     *
     * @param callState call state {@link CallCompositeCallState}.
     */
    public CallCompositeCallStateEvent(final CallCompositeCallState callState) {
        this.callState = callState;
    }

    /**
     * Returns the call state.
     *
     * @return the call state {@link CallCompositeCallState} instance.
     */
    public CallCompositeCallState getCallState() {
        return callState;
    }
}
