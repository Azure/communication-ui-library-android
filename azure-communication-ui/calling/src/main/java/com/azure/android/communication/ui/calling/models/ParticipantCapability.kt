// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models

/**
 * Single participant capability.
 */
internal class ParticipantCapability constructor(
    participantCapabilityType: ParticipantCapabilityType,
    isAllowed: Boolean,
    capabilityResolutionReason: CapabilityResolutionReason
) {
    private val participantCapabilityType: ParticipantCapabilityType

    /**
     * Tells whether capability is capable or not.
     */
    val isAllowed: Boolean
    private val capabilityResolutionReason: CapabilityResolutionReason

    init {
        this.participantCapabilityType = participantCapabilityType
        this.isAllowed = isAllowed
        this.capabilityResolutionReason = capabilityResolutionReason
    }

    val type: ParticipantCapabilityType
        /**
         * Capability name.
         */
        get() = participantCapabilityType
    val reason: CapabilityResolutionReason
        /**
         * Capability resolution reason.
         */
        get() = capabilityResolutionReason
}
