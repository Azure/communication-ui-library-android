// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

import com.azure.android.core.util.ExpandableStringEnum;

public final class CallCompositeNavigationStatus extends ExpandableStringEnum<CallCompositeNavigationStatus> {

    public static final CallCompositeNavigationStatus SETUP = fromString("setup");
    public static final CallCompositeNavigationStatus CALL = fromString("call");

    // Consider none vs exit
    public static final CallCompositeNavigationStatus NONE = fromString("none");
    public static final CallCompositeNavigationStatus EXIT = fromString("exit");

    private CallCompositeNavigationStatus() {

    }

    private static CallCompositeNavigationStatus fromString(final String name) {
        return fromString(name, CallCompositeNavigationStatus.class);
    }
}
