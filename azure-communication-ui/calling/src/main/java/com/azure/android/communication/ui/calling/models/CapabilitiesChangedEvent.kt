package com.azure.android.communication.ui.calling.models

internal data class CapabilitiesChangedEvent(
    val changedCapabilities: List<ParticipantCapability>,
    val capabilitiesChangedReason: CapabilitiesChangedReason
)
