// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.manager

import com.azure.android.communication.ui.calling.configuration.CallType
import com.azure.android.communication.ui.calling.models.ParticipantCapabilityType

internal class CapabilitiesManager(
    private val callType: CallType,
) {
    fun hasCapability(
        capabilities: Set<ParticipantCapabilityType>,
        capability: ParticipantCapabilityType,
    ): Boolean {
        return when (callType) {
            CallType.GROUP_CALL,
            CallType.TEAMS_MEETING -> true
            CallType.ROOMS_CALL -> capabilities.contains(capability)
        }
    }
}
