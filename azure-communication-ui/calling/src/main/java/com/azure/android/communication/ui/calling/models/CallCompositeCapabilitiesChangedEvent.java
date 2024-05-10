// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

import java.util.List;

public class CallCompositeCapabilitiesChangedEvent {
    private final List<CallCompositeParticipantCapability> changedCapabilities;
    private final CallCompositeCapabilitiesChangedReason capabilitiesChangedReason;

    CallCompositeCapabilitiesChangedEvent(final List<CallCompositeParticipantCapability> changedCapabilities,
                                          final CallCompositeCapabilitiesChangedReason capabilitiesChangedReason) {

        this.changedCapabilities = changedCapabilities;
        this.capabilitiesChangedReason = capabilitiesChangedReason;
    }

    /**
     * List of capabilities changed.
     */
    public List<CallCompositeParticipantCapability> getChangedCapabilities() {
        return changedCapabilities;
    }

    /**
     * Capability changed reason.
     */
    public CallCompositeCapabilitiesChangedReason getReason() {
        return capabilitiesChangedReason;
    }
}
