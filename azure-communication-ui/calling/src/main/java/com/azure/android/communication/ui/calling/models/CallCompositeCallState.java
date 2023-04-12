// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

import com.azure.android.core.util.ExpandableStringEnum;

import java.util.Collection;

/**
 * Defines values for CallCompositeCallState.
 */
public final class CallCompositeCallState extends ExpandableStringEnum<CallCompositeCallState> {

    public static final CallCompositeCallState NONE = fromString("none");

    public static final CallCompositeCallState EARLY_MEDIA = fromString("earlyMedia");

    public static final CallCompositeCallState CONNECTING = fromString("connecting");

    public static final CallCompositeCallState RINGING = fromString("ringing");

    public static final CallCompositeCallState CONNECTED = fromString("connected");

    public static final CallCompositeCallState LOCAL_HOLD = fromString("localHold");

    public static final CallCompositeCallState DISCONNECTING = fromString("disconnecting");

    public static final CallCompositeCallState DISCONNECTED = fromString("disconnected");

    public static final CallCompositeCallState IN_LOBBY = fromString("inLobby");

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


