// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.manager

import com.azure.android.communication.ui.calling.configuration.CallType
import com.azure.android.communication.ui.calling.models.CallCompositeParticipantCapabilityType
import com.azure.android.communication.ui.calling.models.ParticipantRole

internal class CapabilitiesManager(
    private val callType: CallType,
) {
    fun hasCapability(
        capabilities: Set<CallCompositeParticipantCapabilityType>,
        capability: CallCompositeParticipantCapabilityType
    ): Boolean {
        return when (callType) {
            CallType.GROUP_CALL -> true
            CallType.TEAMS_MEETING -> true
            CallType.ROOMS_CALL -> capabilities.contains(capability)
        }
    }

    fun hasCapability(
        participantRole: ParticipantRole,
        capability: CallCompositeParticipantCapabilityType
    ): Boolean {
        if (capability == CallCompositeParticipantCapabilityType.UNMUTE_MICROPHONE
            || capability == CallCompositeParticipantCapabilityType.TURN_VIDEO_ON) {
            return participantRole != ParticipantRole.CONSUMER
        }

        return false
    }
}
