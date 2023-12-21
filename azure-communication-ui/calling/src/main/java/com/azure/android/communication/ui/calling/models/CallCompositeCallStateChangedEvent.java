// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

/**
 * Event with call state.
 */
public final class CallCompositeCallStateChangedEvent {
    private final CallCompositeCallStateCode code;
    private final Integer callEndReasonCode;
    private final Integer callEndReasonSubCode;

    /**
     * Create {@link CallCompositeCallStateChangedEvent} with call state.
     *
     * @param code call state {@link CallCompositeCallStateCode}.
     * @param callEndReasonCode call end reason code on DISCONNECTED.
     * @param callEndReasonSubCode call end reason sub code on DISCONNECTED.
     */
    public CallCompositeCallStateChangedEvent(final CallCompositeCallStateCode code,
                                              final Integer callEndReasonCode,
                                              final Integer callEndReasonSubCode) {
        this.code = code;
        this.callEndReasonCode = callEndReasonCode;
        this.callEndReasonSubCode = callEndReasonSubCode;
    }

    /**
     * Returns the call state.
     *
     * @return the call state {@link CallCompositeCallStateCode} instance.
     */
    public CallCompositeCallStateCode getCode() {
        return code;
    }

    /**
     * Returns the call end reason code.
     *
     * @return the call end reason code.
     */
    public Integer getCallEndReasonCode() {
        return callEndReasonCode;
    }

    /**
     * Returns the call end reason sub code.
     *
     * @return the call end reason sub code.
     */
    public Integer getCallEndReasonSubCode() {
        return callEndReasonSubCode;
    }
}
