// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

import com.azure.android.core.util.ExpandableStringEnum;

import java.util.Collection;

/**
 * Reason for capabilities changed.
 */
public class CallCompositeCapabilitiesChangedReason
        extends ExpandableStringEnum<CallCompositeCapabilitiesChangedReason> {

    /**
     * Role changed.
     */
    public static final CallCompositeCapabilitiesChangedReason ROLE_CHANGED =
            fromString("ROLE_CHANGED");

    /**
     * User policy changed.
     */
    public static final CallCompositeCapabilitiesChangedReason USER_POLICY_CHANGED =
            fromString("USER_POLICY_CHANGED");

    /**
     * Meeting details changed.
     */
    public static final CallCompositeCapabilitiesChangedReason MEETING_DETAILS_CHANGED =
            fromString("MEETING_DETAILS_CHANGED");


    /**
     * Creates or finds a {@link CallCompositeCapabilitiesChangedReason} from its string representation.
     *
     * @param name a name to look for.
     * @return the corresponding {@link CallCompositeCapabilitiesChangedReason}.
     */
    public static CallCompositeCapabilitiesChangedReason fromString(final String name) {
        return fromString(name, CallCompositeCapabilitiesChangedReason.class);
    }

    /**
     * @return known {@link CallCompositeCapabilitiesChangedReason} values.
     */
    public static Collection<CallCompositeCapabilitiesChangedReason> values() {
        return values(CallCompositeCapabilitiesChangedReason.class);
    }
}
