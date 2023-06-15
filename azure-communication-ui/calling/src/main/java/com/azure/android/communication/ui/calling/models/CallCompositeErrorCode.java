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
     * There is a failure to join a call.
     */
    public static final CallCompositeErrorCode CALL_JOIN_FAILED = fromString("callJoinFailed");

    /**
     * There is a failure to end a call.
     */
    public static final CallCompositeErrorCode CALL_END_FAILED = fromString("callEndFailed");

    /**
     * ACS Token supplied is no longer valid (expired).
     */
    public static final CallCompositeErrorCode TOKEN_EXPIRED = fromString("tokenExpired");

    /**
     * Camera failed to start, stop, switch or fails to instantiate camera.
     */
    public static final CallCompositeErrorCode CAMERA_FAILURE = fromString("cameraFailure");

    /**
     * Default microphone control API is accessed without microphone permission being granted.
     */
    public static final CallCompositeErrorCode MICROPHONE_PERMISSION_NOT_GRANTED =
            fromString("microphonePermissionNotGranted");

    /**
     * Internet not available while trying to join a call bypassing the setup screen.
     */
    public static final CallCompositeErrorCode NETWORK_CONNECTION_NOT_AVAILABLE =
            fromString("networkConnectionNotAvailable");

    /***
     * Microphone is being used by other application or can not be accessed
     */
    public static final CallCompositeErrorCode MICROPHONE_NOT_AVAILABLE = fromString("microphoneNotAvailable");

    /**
     * Creates or finds a {@link CallCompositeErrorCode} from its string representation.
     *
     * @param name a name to look for.
     * @return the corresponding CallCompositeErrorCode.
     */
    public static CallCompositeErrorCode fromString(final String name) {
        return fromString(name, CallCompositeErrorCode.class);
    }

    /**
     * @return known {@link CallCompositeErrorCode} values.
     */
    public static Collection<CallCompositeErrorCode> values() {
        return values(CallCompositeErrorCode.class);
    }

}

