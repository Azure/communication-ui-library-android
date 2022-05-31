// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

import com.azure.android.core.util.ExpandableStringEnum;

import java.util.Collection;

/**
 * Defines values for CallCompositeErrorCode.
 */
public final class CallCompositeErrorCode extends ExpandableStringEnum<CallCompositeErrorCode> {

    public static final CallCompositeErrorCode CALL_JOIN_FAILED = fromString("callJoinFailed");

    public static final CallCompositeErrorCode CALL_END_FAILED = fromString("callEndFailed");

    public static final CallCompositeErrorCode TOKEN_EXPIRED = fromString("tokenExpired");

    public static final CallCompositeErrorCode SWITCH_CAMERA_FAILED = fromString("switchCameraFailed");

    public static final CallCompositeErrorCode TURN_CAMERA_ON_FAILED = fromString("turnCameraOnFailed");

    public static final CallCompositeErrorCode TURN_CAMERA_OFF_FAILED = fromString("turnCameraOffFailed");

    public static final CallCompositeErrorCode TURN_MIC_ON_FAILED = fromString("turnMicOnFailed");

    public static final CallCompositeErrorCode TURN_MIC_OFF_FAILED = fromString("turnMicOffFailed");

    /**
     * Creates or finds a {@link CallCompositeErrorCode} from its string representation.
     *
     * @param name a name to look for.
     * @return the corresponding CallCompositeErrorCode.
     */
    private static CallCompositeErrorCode fromString(final String name) {
        return fromString(name, CallCompositeErrorCode.class);
    }

    /**
     * @return known {@link CallCompositeErrorCode} values.
     */
    public static Collection<CallCompositeErrorCode> values() {
        return values(CallCompositeErrorCode.class);
    }
}

