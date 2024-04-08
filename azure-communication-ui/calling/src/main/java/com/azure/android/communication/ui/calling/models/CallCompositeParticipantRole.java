// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

import com.azure.android.core.util.ExpandableStringEnum;
import java.util.Collection;

/**
 * Defines values for {@linkCallCompositeParticipantRole}.
 */
/* <ROOMS_SUPPORT:3> */
public  final class CallCompositeParticipantRole extends ExpandableStringEnum<CallCompositeParticipantRole> {
    /* </ROOMS_SUPPORT:0> */

    /**
     * Presenter Role in the Room call.
     */
    /* <ROOMS_SUPPORT:3> */
    public static final CallCompositeParticipantRole PRESENTER = fromString("Presenter");
    /* </ROOMS_SUPPORT:0> */

    /**
     * Attendee Role in the Room call.
     */
    /* <ROOMS_SUPPORT:3> */
    public static final CallCompositeParticipantRole ATTENDEE = fromString("Attendee");
    /* </ROOMS_SUPPORT:0> */

    /**
     * Creates instance of {@linkCallCompositeParticipantRole}.
     */
    /* <ROOMS_SUPPORT:3> */
    public CallCompositeParticipantRole() { }
    /* </ROOMS_SUPPORT:0> */

    /**
     * Creates or finds a {@linkCallCompositeParticipantRole} from it's string representation.
     *
     * @param name a name to look for.
     * @return the corresponding {@linkCallCompositeParticipantRole}.
     */
    /* <ROOMS_SUPPORT:6> */
    public static CallCompositeParticipantRole fromString(final String name) {
        return fromString(name, CallCompositeParticipantRole.class);
    }
    /* </ROOMS_SUPPORT:0> */

    /**
     * @return known {@linkCallCompositeParticipantRole} values.
     */
    /* <ROOMS_SUPPORT:3> */
    public static Collection<CallCompositeParticipantRole> values() {
        return values(CallCompositeParticipantRole.class);
    }
}
/* </ROOMS_SUPPORT:0> */
