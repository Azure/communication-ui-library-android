package com.azure.android.communication.ui.calling.models

/**
 * Capability Resolution Reason.
 */
internal enum class CapabilityResolutionReason {
    /**
     * Capable
     */
    CAPABLE,
    /**
     * Call type restricted
     */
    CALL_TYPE_RESTRICTED,
    /**
     * User policy restricted
     */
    USER_POLICY_RESTRICTED,
    /**
     * Role restricted
     */
    ROLE_RESTRICTED,
    /**
     * Meeting restricted
     */
    MEETING_RESTRICTED,
    /**
     * Feature not supported
     */
    FEATURE_NOT_SUPPORTED,
    /**
     * Not initialized
     */
    NOT_INITIALIZED,
    /**
     * Not capable
     */
    NOT_CAPABLE,
}

