// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

import com.azure.android.core.util.ExpandableStringEnum;

import java.util.Collection;

/**
 * Defines values for CommunicationUIErrorCode.
 */
public final class CommunicationUIErrorCode extends ExpandableStringEnum<CommunicationUIErrorCode> {

    public static final CommunicationUIErrorCode CALL_JOIN_FAILED = fromString("callJoinFailed");

    public static final CommunicationUIErrorCode HOLD_FAILED = fromString("holdFailed");
    
    public static final CommunicationUIErrorCode RESUME_FAILED = fromString("resumeFailed");

    public static final CommunicationUIErrorCode CALL_END_FAILED = fromString("callEndFailed");

    public static final CommunicationUIErrorCode TOKEN_EXPIRED = fromString("tokenExpired");

    public static final CommunicationUIErrorCode SWITCH_CAMERA_FAILED = fromString("switchCameraFailed");

    public static final CommunicationUIErrorCode TURN_CAMERA_ON_FAILED = fromString("turnCameraOnFailed");

    public static final CommunicationUIErrorCode TURN_CAMERA_OFF_FAILED = fromString("turnCameraOffFailed");

    public static final CommunicationUIErrorCode TURN_MIC_ON_FAILED = fromString("turnMicOnFailed");

    public static final CommunicationUIErrorCode TURN_MIC_OFF_FAILED = fromString("turnMicOffFailed");

    /**
     * Creates or finds a CallCompositeErrorCode from its string representation.
     *
     * @param name a name to look for.
     * @return the corresponding CallCompositeErrorCode.
     */
    private static CommunicationUIErrorCode fromString(final String name) {
        return fromString(name, CommunicationUIErrorCode.class);
    }

    /**
     * @return known CallCompositeErrorCode values.
     */
    public static Collection<CommunicationUIErrorCode> values() {
        return values(CommunicationUIErrorCode.class);
    }
}

