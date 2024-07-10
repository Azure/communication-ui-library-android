// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

import com.azure.android.core.util.ExpandableStringEnum;

import java.util.Collection;

public class CallCompositeCustomButtonPlacement extends ExpandableStringEnum<CallCompositeCustomButtonPlacement> {
    public static final CallCompositeCustomButtonPlacement PRIMARY = fromString("PRIMARY");

    public static final CallCompositeCustomButtonPlacement OVERFLOW = fromString("OVERFLOW");

    public static CallCompositeCustomButtonPlacement fromString(final String name) {
        return fromString(name, CallCompositeCustomButtonPlacement.class);
    }

    public static Collection<CallCompositeCustomButtonPlacement> values() {
        return values(CallCompositeCustomButtonPlacement.class);
    }
}
