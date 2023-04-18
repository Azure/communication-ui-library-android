// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

import com.azure.android.core.util.ExpandableStringEnum;

import java.util.Collection;

/**
 * Defines values for CallCompositeCallState.
 */
public final class CallCompositeCallState extends ExpandableStringEnum<CallCompositeCallState> {

    /**
     * None - disposed or applicable very early in lifetime of a call.
     */
    public static final CallCompositeCallState NONE = fromString("none");

    /**
     * Early Media.
     */
    public static final CallCompositeCallState EARLY_MEDIA = fromString("earlyMedia");

    /**
     * Call is being connected.
     */
    public static final CallCompositeCallState CONNECTING = fromString("connecting");

    /**
     * Call is ringing.
     */
    public static final CallCompositeCallState RINGING = fromString("ringing");

    /**
     * Call is connected.
     */
    public static final CallCompositeCallState CONNECTED = fromString("connected");

    /**
     * Call held by local participant.
     */
    public static final CallCompositeCallState LOCAL_HOLD = fromString("localHold");

    /**
     * None - disposed or applicable very early in lifetime of a call.
     */
    public static final CallCompositeCallState DISCONNECTING = fromString("disconnecting");

    /**
     * Call is being disconnected.
     */
    public static final CallCompositeCallState DISCONNECTED = fromString("disconnected");

    /**
     * In Lobby.
     */
    public static final CallCompositeCallState IN_LOBBY = fromString("inLobby");

    /**
     * Call held by a remote participant.
     */
    public static final CallCompositeCallState REMOTE_HOLD = fromString("remoteHold");

    /**
     * Creates or finds a {@link CallCompositeCallState} from its string representation.
     *
     * @param name a name to look for.
     * @return the corresponding CallCompositeCallState.
     */
    public static CallCompositeCallState fromString(final String name) {
        return fromString(name, CallCompositeCallState.class);
    }

    /**
     * @return known {@link CallCompositeCallState} values.
     */
    public static Collection<CallCompositeCallState> values() {
        return values(CallCompositeCallState.class);
    }
}


