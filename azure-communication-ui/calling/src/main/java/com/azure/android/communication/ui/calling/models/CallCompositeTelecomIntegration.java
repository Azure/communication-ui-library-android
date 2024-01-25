// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

import com.azure.android.core.util.ExpandableStringEnum;

import java.util.Collection;

/**
 * CallCompositeTelecomIntegration {@link CallCompositeTelecomIntegration}.
 */
final class CallCompositeTelecomIntegration extends ExpandableStringEnum<CallCompositeTelecomIntegration> {

    /**
     * Use telecom manager provided by native calling sdk.
     */
    static final CallCompositeTelecomIntegration USE_SDK_PROVIDED_TELECOM_MANAGER =
            fromString("USE_SDK_PROVIDED_TELECOM_MANAGER");

    /**
     * Use telecom manager managed by application.
     */
    static final CallCompositeTelecomIntegration APPLICATION_IMPLEMENTED_TELECOM_MANAGER =
            fromString("APPLICATION_IMPLEMENTED_TELECOM_MANAGER");


    /**
     * Creates or finds a {@link CallCompositeTelecomIntegration} from its string representation.
     * @param name
     * @return
     */
    static CallCompositeTelecomIntegration fromString(final String name) {
        return fromString(name, CallCompositeTelecomIntegration.class);
    }

    /**
     * @return known {@link CallCompositeTelecomIntegration} values.
     */
    static Collection<CallCompositeTelecomIntegration> values() {
        return values(CallCompositeTelecomIntegration.class);
    }
}
