// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

import com.azure.android.core.util.ExpandableStringEnum;

import java.util.Collection;

/**
 * CallCompositeTelecomIntegration {@link CallCompositeTelecomIntegration}.
 */
public final class CallCompositeTelecomIntegration extends ExpandableStringEnum<CallCompositeTelecomIntegration> {

    /**
     * Use telecom manager managed by application.
     */
    public static final CallCompositeTelecomIntegration APPLICATION_IMPLEMENTED_TELECOM_MANAGER =
            fromString("APPLICATION_IMPLEMENTED_TELECOM_MANAGER");

    /**
     * Creates or finds a {@link CallCompositeTelecomIntegration} from its string representation.
     */
    public static CallCompositeTelecomIntegration fromString(final String name) {
        return fromString(name, CallCompositeTelecomIntegration.class);
    }

    /**
     * @return known {@link CallCompositeTelecomIntegration} values.
     */
    public static Collection<CallCompositeTelecomIntegration> values() {
        return values(CallCompositeTelecomIntegration.class);
    }
}
