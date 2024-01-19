// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

import com.azure.android.core.util.ExpandableStringEnum;

import java.util.Collection;

/**
 * Defines values for {@linkCallCompositeParticipantRole}.
 */
final class CallCompositeParticipantRole extends ExpandableStringEnum<CallCompositeParticipantRole> {
    /**
     * Presenter Role in the Room call.
     */
    static final CallCompositeParticipantRole PRESENTER = fromString("Presenter");

    /**
     * Attendee Role in the Room call.
     */
    static final CallCompositeParticipantRole ATTENDEE = fromString("Attendee");

    /**
     * Creates instance of {@linkCallCompositeParticipantRole}.
     */
    CallCompositeParticipantRole() { }

    /**
     * Creates or finds a {@linkCallCompositeParticipantRole} from it's string representation.
     *
     * @param name a name to look for.
     * @return the corresponding {@linkCallCompositeParticipantRole}.
     */
    static CallCompositeParticipantRole fromString(final String name) {
        return fromString(name, CallCompositeParticipantRole.class);
    }

    /**
     * @return known {@linkCallCompositeParticipantRole} values.
     */
    static Collection<CallCompositeParticipantRole> values() {
        return values(CallCompositeParticipantRole.class);
    }
}
