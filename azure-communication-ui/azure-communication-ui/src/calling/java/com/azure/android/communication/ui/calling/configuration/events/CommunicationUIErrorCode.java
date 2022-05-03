// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.configuration.events;

import com.azure.android.core.util.ExpandableStringEnum;

import java.util.Collection;

/**
 * Defines values for CommunicationUIErrorCode.
 */
public final class CommunicationUIErrorCode extends ExpandableStringEnum<CommunicationUIErrorCode> {

    public static final CommunicationUIErrorCode CALL_JOIN = fromString("callJoin");

    public static final CommunicationUIErrorCode CALL_END = fromString("callEnd");

    public static final CommunicationUIErrorCode TOKEN_EXPIRED = fromString("tokenExpired");

    public static final CommunicationUIErrorCode SWITCH_CAMERA = fromString("switchCamera");

    public static final CommunicationUIErrorCode TURN_CAMERA_ON = fromString("turnCameraOn");

    public static final CommunicationUIErrorCode TURN_CAMERA_OFF = fromString("turnCameraOff");

    public static final CommunicationUIErrorCode TURN_MIC_ON = fromString("turnMicOn");

    public static final CommunicationUIErrorCode TURN_MIC_OFF = fromString("turnMicOff");

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

