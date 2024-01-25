// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

/**
 * CallCompositeIncomingCallEvent.
 */
final class CallCompositeIncomingCallEvent {
    private final CallCompositeIncomingCallInfo incomingCallInfo;

    /**
     * Create {@link CallCompositeIncomingCallEvent} with incoming call event.
     *
     * @param incomingCallInfo incoming call info.
     */
    CallCompositeIncomingCallEvent(final CallCompositeIncomingCallInfo incomingCallInfo) {
        this.incomingCallInfo = incomingCallInfo;
    }

    /**
     * @return {@link CallCompositeIncomingCallInfo}
     */
    CallCompositeIncomingCallInfo getIncomingCallInfo() {
        return incomingCallInfo;
    }
}
