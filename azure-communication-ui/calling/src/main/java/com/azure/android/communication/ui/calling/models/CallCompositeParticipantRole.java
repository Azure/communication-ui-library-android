// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
/* <ROOMS_SUPPORT:0> */
package com.azure.android.communication.ui.calling.models;

import com.azure.android.core.util.ExpandableStringEnum;

import java.util.Collection;

/**
 * Defines values for {@linkCallCompositeParticipantRole}.
 */
public final class CallCompositeParticipantRole extends ExpandableStringEnum<CallCompositeParticipantRole> {

    /**
     * Presenter Role in the Room call.
     */
    public static final CallCompositeParticipantRole PRESENTER = fromString("Presenter");

    /**
     * Attendee Role in the Room call.
     */
    public static final CallCompositeParticipantRole ATTENDEE = fromString("Attendee");

    /**
     * Creates instance of {@linkCallCompositeParticipantRole}.
     */
    public CallCompositeParticipantRole() { }

    /**
     * Creates or finds a {@linkCallCompositeParticipantRole} from it's string representation.
     *
     * @param name a name to look for.
     * @return the corresponding {@linkCallCompositeParticipantRole}.
     */
    public static CallCompositeParticipantRole fromString(final String name) {
        return fromString(name, CallCompositeParticipantRole.class);
    }

    /**
     * @return known {@linkCallCompositeParticipantRole} values.
     */
    public static Collection<CallCompositeParticipantRole> values() {
        return values(CallCompositeParticipantRole.class);
    }
}
/* </ROOMS_SUPPORT:0> */
