// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

import com.azure.android.core.util.ExpandableStringEnum;

import java.util.Collection;

/**
 * CallCompositeTelecomManagerIntegration {@link CallCompositeTelecomManagerIntegration}.
 */
public final class CallCompositeTelecomManagerIntegration
        extends ExpandableStringEnum<CallCompositeTelecomManagerIntegration> {

    /**
     * Use telecom manager provided by native calling sdk.
     */
    public static final CallCompositeTelecomManagerIntegration USE_SDK_PROVIDED_TELECOM_MANAGER =
            fromString("USE_SDK_PROVIDED_TELECOM_MANAGER");

    /**
     * Use telecom manager managed by application.
     */
    public static final CallCompositeTelecomManagerIntegration APPLICATION_IMPLEMENTED_TELECOM_MANAGER =
            fromString("APPLICATION_IMPLEMENTED_TELECOM_MANAGER");

    /**
     * Creates or finds a {@link CallCompositeTelecomManagerIntegration} from its string representation.
     */
    public static CallCompositeTelecomManagerIntegration fromString(final String name) {
        return fromString(name, CallCompositeTelecomManagerIntegration.class);
    }

    /**
     * @return known {@link CallCompositeTelecomManagerIntegration} values.
     */
    public static Collection<CallCompositeTelecomManagerIntegration> values() {
        return values(CallCompositeTelecomManagerIntegration.class);
    }
}
