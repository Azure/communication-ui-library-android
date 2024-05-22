package com.azure.android.communication.ui.calling.models

internal class CapabilitiesChangedEvent(
    changedCapabilities: List<ParticipantCapability>,
    capabilitiesChangedReason: CapabilitiesChangedReason
) {
    private val changedCapabilities: List<ParticipantCapability>
    private val capabilitiesChangedReason: CapabilitiesChangedReason

    init {
        this.changedCapabilities = changedCapabilities
        this.capabilitiesChangedReason = capabilitiesChangedReason
    }

    /**
     * List of capabilities changed.
     */
    fun getChangedCapabilities(): List<ParticipantCapability> {
        return changedCapabilities
    }

    val reason: CapabilitiesChangedReason
        /**
         * Capability changed reason.
         */
        get() = capabilitiesChangedReason
}
