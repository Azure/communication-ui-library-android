// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

import com.azure.android.core.util.ExpandableStringEnum;

/**
 * Defines where custom button will be displayed in the composite.
 */
public final class CallCompositeCustomButtonType extends ExpandableStringEnum<CallCompositeCustomButtonType> {

    /**
     * Button is placed in the Call Screen Header Bar.
     */
    public static final CallCompositeCustomButtonType CALL_SCREEN_INFO_HEADER = fromString("callScreenInfoHeader");

    private CallCompositeCustomButtonType() { }

    private static CallCompositeCustomButtonType fromString(final String name) {
        return fromString(name, CallCompositeCustomButtonType.class);
    }
}
