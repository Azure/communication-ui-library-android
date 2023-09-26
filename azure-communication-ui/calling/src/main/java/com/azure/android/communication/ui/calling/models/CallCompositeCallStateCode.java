// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

import com.azure.android.core.util.ExpandableStringEnum;

import java.util.Collection;

/**
 * Defines values for CallCompositeCallStateCode.
 */
public final class CallCompositeCallStateCode extends ExpandableStringEnum<CallCompositeCallStateCode> {

    /**
     * None - disposed or applicable very early in lifetime of a call.
     */
    public static final CallCompositeCallStateCode NONE = fromString("none");

    /**
     * Early Media.
     */
    public static final CallCompositeCallStateCode EARLY_MEDIA = fromString("earlyMedia");

    /**
     * Call is being connected.
     */
    public static final CallCompositeCallStateCode CONNECTING = fromString("connecting");

    /**
     * Call is ringing.
     */
    public static final CallCompositeCallStateCode RINGING = fromString("ringing");

    /**
     * Call is connected.
     */
    public static final CallCompositeCallStateCode CONNECTED = fromString("connected");

    /**
     * Call held by local participant.
     */
    public static final CallCompositeCallStateCode LOCAL_HOLD = fromString("localHold");

    /**
     * None - disposed or applicable very early in lifetime of a call.
     */
    public static final CallCompositeCallStateCode DISCONNECTING = fromString("disconnecting");

    /**
     * Call is being disconnected.
     */
    public static final CallCompositeCallStateCode DISCONNECTED = fromString("disconnected");

    /**
     * In Lobby.
     */
    public static final CallCompositeCallStateCode IN_LOBBY = fromString("inLobby");

    /**
     * Call held by a remote participant.
     */
    public static final CallCompositeCallStateCode REMOTE_HOLD = fromString("remoteHold");

    /**
     * Creates or finds a {@link CallCompositeCallStateCode} from its string representation.
     *
     * @param name a name to look for.
     * @return the corresponding CallCompositeCallStateCode.
     */
    public static CallCompositeCallStateCode fromString(final String name) {
        return fromString(name, CallCompositeCallStateCode.class);
    }

    /**
     * @return known {@link CallCompositeCallStateCode} values.
     */
    public static Collection<CallCompositeCallStateCode> values() {
        return values(CallCompositeCallStateCode.class);
    }
}


