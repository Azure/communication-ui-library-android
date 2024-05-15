package com.azure.android.communication.ui.calling.models


internal enum class CapabilitiesChangedReason {
    /**
     * Role changed
     */
    ROLE_CHANGED,

    /**
     * User policy changed
     */
    USER_POLICY_CHANGED,

    /**
     * Meeting details changed
     */
    MEETING_DETAILS_CHANGED
}
