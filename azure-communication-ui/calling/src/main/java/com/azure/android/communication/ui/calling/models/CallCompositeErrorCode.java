// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

import com.azure.android.core.util.ExpandableStringEnum;

import java.util.Collection;

/**
 * Defines values for CallCompositeErrorCode.
 */
public final class CallCompositeErrorCode extends ExpandableStringEnum<CallCompositeErrorCode> {

    /**
     * Dispatched when there is a failure to join a call.
     */
    public static final CallCompositeErrorCode CALL_JOIN_FAILED = fromString("callJoinFailed");

    /**
     * Dispatched when there is a failure to end a call.
     */
    public static final CallCompositeErrorCode CALL_END_FAILED = fromString("callEndFailed");

    /**
     * Dispatched when the ACS Token supplied is no longer valid (expired).
     */
    public static final CallCompositeErrorCode TOKEN_EXPIRED = fromString("tokenExpired");

    /**
     * Dispatched when camera failed to start, stop, switch or fails to instantiate camera.
     */
    public static final CallCompositeErrorCode CAMERA_FAILURE = fromString("cameraFailure");

    /**
     * Dispatched when default call control API is accessed without their respective granted permission.
     */
    public static final CallCompositeErrorCode PERMISSION_REQUIRED = fromString("requestFailedDueToRequiredPermission");

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

