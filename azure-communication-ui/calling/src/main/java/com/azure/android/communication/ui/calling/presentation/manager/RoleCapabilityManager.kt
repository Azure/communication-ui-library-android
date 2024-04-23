package com.azure.android.communication.ui.calling.presentation.manager

import com.azure.android.communication.ui.calling.models.ParticipantCapabilityType
import com.azure.android.communication.ui.calling.models.ParticipantRole

// private val capabilityMatrix

// Provides a capability according to the role for Rooms call context.
internal fun ParticipantRole.hasCapability(capabilityType: ParticipantCapabilityType): Boolean {
    if (capabilityType == ParticipantCapabilityType.UNMUTE_MIC || capabilityType == ParticipantCapabilityType.TURN_VIDEO_ON) {
        return this != ParticipantRole.CONSUMER
    }

    return false
}
