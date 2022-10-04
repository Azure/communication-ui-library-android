// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

import com.azure.android.core.util.ExpandableStringEnum;

public final class CallCompositeCallStatus extends ExpandableStringEnum<CallCompositeCallStatus> {
    public static final CallCompositeCallStatus NONE = fromString("none");
    public static final CallCompositeCallStatus CONNECTED = fromString("connected");

    private  CallCompositeCallStatus() {

    }

    private static CallCompositeCallStatus fromString(final String name) {
        return fromString(name, CallCompositeCallStatus.class);
    }
}
