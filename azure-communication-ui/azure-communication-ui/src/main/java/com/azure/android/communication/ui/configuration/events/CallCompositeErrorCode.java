// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.configuration.events;

import com.azure.android.core.util.ExpandableStringEnum;

import java.util.Collection;

/** Defines values for CallCompositeErrorCode. */
public final class CallCompositeErrorCode extends ExpandableStringEnum<CallCompositeErrorCode> {

    public static final CallCompositeErrorCode CALL_JOIN = fromString("callJoin");

    public static final CallCompositeErrorCode CALL_END = fromString("callEnd");

    public static final CallCompositeErrorCode TOKEN_EXPIRED = fromString("tokenExpired");

    public static final CallCompositeErrorCode SWITCH_CAMERA = fromString("switchCamera");

    public static final CallCompositeErrorCode TURN_CAMERA_ON = fromString("turnCameraOn");

    public static final CallCompositeErrorCode TURN_CAMERA_OFF = fromString("turnCameraOff");

    public static final CallCompositeErrorCode TURN_MIC_ON = fromString("turnMicOn");

    public static final CallCompositeErrorCode TURN_MIC_OFF = fromString("turnMicOff");

    /**
     * Creates or finds a CallCompositeErrorCode from its string representation.
     *
     * @param name a name to look for.
     * @return the corresponding CallCompositeErrorCode.
     */
    public static CallCompositeErrorCode fromString(final String name) {
        return fromString(name, CallCompositeErrorCode.class);
    }

    @Override
    public String toString() {
        return super.toString();
    }

    /** @return known CallCompositeErrorCode values. */
    public static Collection<CallCompositeErrorCode> values() {
        return values(CallCompositeErrorCode.class);
    }
}

