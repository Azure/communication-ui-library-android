// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.manager

import com.azure.android.communication.ui.calling.configuration.CallType
import com.azure.android.communication.ui.calling.models.ParticipantCapabilityType
import com.azure.android.communication.ui.calling.models.ParticipantRole

internal class CapabilitiesManager(
    private val callType: CallType,
) {
    fun hasCapability(capabilities: Set<ParticipantCapabilityType>, capability: ParticipantCapabilityType): Boolean {
        return when (callType) {
            CallType.GROUP_CALL -> true
            CallType.TEAMS_MEETING -> true
            CallType.ROOMS_CALL -> capabilities.contains(capability)
        }
    }

    fun hasCapability(participantRole: ParticipantRole, capability: ParticipantCapabilityType): Boolean {
        if (capability == ParticipantCapabilityType.UNMUTE_MICROPHONE || capability == ParticipantCapabilityType.TURN_VIDEO_ON) {
            return participantRole != ParticipantRole.CONSUMER
        }

        return false
    }
}
