// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

/**
 * Single participant capability.
 */
public class CallCompositeParticipantCapability {

    private final CallCompositeParticipantCapabilityType participantCapabilityType;
    private final Boolean isAllowed;
    private final CallCompositeCapabilityResolutionReason capabilityResolutionReason;

    CallCompositeParticipantCapability(
            final CallCompositeParticipantCapabilityType participantCapabilityType,
            final Boolean isAllowed,
            final CallCompositeCapabilityResolutionReason capabilityResolutionReason
    ) {

        this.participantCapabilityType = participantCapabilityType;
        this.isAllowed = isAllowed;
        this.capabilityResolutionReason = capabilityResolutionReason;
    }

    /**
     * Capability name.
     */
    public CallCompositeParticipantCapabilityType getType() {
        return participantCapabilityType;
    }

    /**
     * Tells whether capability is capable or not.
     */
    public Boolean isAllowed() {
        return isAllowed;
    }

    /**
     * Capability resolution reason.
     */
    public CallCompositeCapabilityResolutionReason getReason() {
        return capabilityResolutionReason;
    }
}
