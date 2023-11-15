// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

import com.azure.android.core.util.ExpandableStringEnum;

import java.util.Collection;

public final class CallCompositeTelecomIntegration extends ExpandableStringEnum<CallCompositeTelecomIntegration> {

    public static final CallCompositeTelecomIntegration USE_SDK_PROVIDED_TELECOM_MANAGER =
            fromString("USE_SDK_PROVIDED_TELECOM_MANAGER");

    public static final CallCompositeTelecomIntegration APPLICATION_IMPLEMENTED_TELECOM_MANAGER =
            fromString("APPLICATION_IMPLEMENTED_TELECOM_MANAGER");


    public static CallCompositeTelecomIntegration fromString(final String name) {
        return fromString(name, CallCompositeTelecomIntegration.class);
    }

    public static Collection<CallCompositeTelecomIntegration> values() {
        return values(CallCompositeTelecomIntegration.class);
    }
}
