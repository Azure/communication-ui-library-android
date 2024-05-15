// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.manager

import com.azure.android.communication.ui.calling.configuration.CallType
import com.azure.android.communication.ui.calling.models.ParticipantCapabilityType
import com.azure.android.communication.ui.calling.models.ParticipantRole

internal class CapabilitiesManager(
    private val callType: CallType,
) {
    fun hasCapability(
        capabilities: Set<ParticipantCapabilityType>,
        participantRole: ParticipantRole?,
        capability: ParticipantCapabilityType,
        ): Boolean {
        return when (callType) {
            CallType.GROUP_CALL -> hasCapability(participantRole, capability)
            CallType.TEAMS_MEETING -> hasCapability(participantRole, capability)
            CallType.ROOMS_CALL -> capabilities.contains(capability)
        }
    }

    fun hasCapability(
        participantRole: ParticipantRole?,
        capability: ParticipantCapabilityType,
        ): Boolean {
        if (participantRole == null)
            return true

        if (capability == ParticipantCapabilityType.UNMUTE_MICROPHONE || capability == ParticipantCapabilityType.TURN_VIDEO_ON) {
            return participantRole != ParticipantRole.CONSUMER
        }

        if (capability == ParticipantCapabilityType.MANAGE_LOBBY) {
            return participantRole == ParticipantRole.ORGANIZER
                    || participantRole == ParticipantRole.PRESENTER
        }

        return false
    }
}
