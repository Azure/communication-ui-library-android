// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.configuration.events;

import com.azure.android.core.util.ExpandableStringEnum;

import java.util.Collection;

/**
 * Defines values for CommunicationUIErrorEvent.
 */
public final class CommunicationUIErrorEvent extends ExpandableStringEnum<CommunicationUIErrorEvent> {

    public static final CommunicationUIErrorEvent CALL_JOIN = fromString("callJoin");

    public static final CommunicationUIErrorEvent CALL_END = fromString("callEnd");

    public static final CommunicationUIErrorEvent TOKEN_EXPIRED = fromString("tokenExpired");

    public static final CommunicationUIErrorEvent SWITCH_CAMERA = fromString("switchCamera");

    public static final CommunicationUIErrorEvent TURN_CAMERA_ON = fromString("turnCameraOn");

    public static final CommunicationUIErrorEvent TURN_CAMERA_OFF = fromString("turnCameraOff");

    public static final CommunicationUIErrorEvent TURN_MIC_ON = fromString("turnMicOn");

    public static final CommunicationUIErrorEvent TURN_MIC_OFF = fromString("turnMicOff");

    /**
     * Creates or finds a CallCompositeErrorCode from its string representation.
     *
     * @param name a name to look for.
     * @return the corresponding CallCompositeErrorCode.
     */
    public static CommunicationUIErrorEvent fromString(final String name) {
        return fromString(name, CommunicationUIErrorEvent.class);
    }

    /**
     * @return known CallCompositeErrorCode values.
     */
    public static Collection<CommunicationUIErrorEvent> values() {
        return values(CommunicationUIErrorEvent.class);
    }

    @Override
    public String toString() {
        return super.toString();
    }
}

