// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

import com.azure.android.core.util.ExpandableStringEnum;

public final class CallCompositePiPViewDefaultPosition
        extends ExpandableStringEnum<CallCompositePiPViewDefaultPosition> {

    public static final CallCompositePiPViewDefaultPosition TOP_LEFT = fromString("TOP_LEFT");
    public static final CallCompositePiPViewDefaultPosition TOP_RIGHT = fromString("TOP_RIGHT");
    public static final CallCompositePiPViewDefaultPosition BOTTOM_LEFT = fromString("BOTTOM_LEFT");
    public static final CallCompositePiPViewDefaultPosition BOTTOM_RIGHT = fromString("BOTTOM_RIGHT");

    private static CallCompositePiPViewDefaultPosition fromString(final String name) {
        return fromString(name, CallCompositePiPViewDefaultPosition.class);
    }
}
