// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

import com.azure.android.core.util.ExpandableStringEnum;

import java.util.Collection;

/**
 * Capability Resolution Reason.
 */
public class CallCompositeCapabilityResolutionReason
        extends ExpandableStringEnum<CallCompositeCapabilityResolutionReason> {

    /**
     * Capable
     */
    public static final CallCompositeCapabilityResolutionReason CAPABLE =
            fromString("CAPABLE");

    /**
     * Call type restricted
     */
    public static final CallCompositeCapabilityResolutionReason CALL_TYPE_RESTRICTED =
            fromString("CALL_TYPE_RESTRICTED");

    /**
     * User policy restricted
     */
    public static final CallCompositeCapabilityResolutionReason USER_POLICY_RESTRICTED =
            fromString("USER_POLICY_RESTRICTED");

    /**
     * Role restricted
     */
    public static final CallCompositeCapabilityResolutionReason ROLE_RESTRICTED =
            fromString("ROLE_RESTRICTED");

    /**
     * Meeting restricted
     */
    public static final CallCompositeCapabilityResolutionReason MEETING_RESTRICTED =
            fromString("MEETING_RESTRICTED");

    /**
     * Feature not supported
     */
    public static final CallCompositeCapabilityResolutionReason FEATURE_NOT_SUPPORTED =
            fromString("FEATURE_NOT_SUPPORTED");

    /**
     * Not initialized
     */
    public static final CallCompositeCapabilityResolutionReason NOT_INITIALIZED =
            fromString("NOT_INITIALIZED");

    /**
     * Not capable
     */
    public static final CallCompositeCapabilityResolutionReason NOT_CAPABLE =
            fromString("NOT_CAPABLE");

    /**
     * Creates or finds a {@link CallCompositeCapabilityResolutionReason} from its string representation.
     *
     * @param name a name to look for.
     * @return the corresponding {@link CallCompositeCapabilityResolutionReason}.
     */
    public static CallCompositeCapabilityResolutionReason fromString(final String name) {
        return fromString(name, CallCompositeCapabilityResolutionReason.class);
    }

    /**
     * @return known {@link CallCompositeCapabilityResolutionReason} values.
     */
    public static Collection<CallCompositeCapabilityResolutionReason> values() {
        return values(CallCompositeCapabilityResolutionReason.class);
    }
}
