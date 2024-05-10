package com.azure.android.communication.ui.calling.models

internal fun createCallCompositeCapabilitiesChangedEvent(
    changedCapabilities: List<CallCompositeParticipantCapability>,
    capabilitiesChangedReason: CallCompositeCapabilitiesChangedReason,
) =
    CallCompositeCapabilitiesChangedEvent(changedCapabilities, capabilitiesChangedReason)
