// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

import com.azure.android.core.util.ExpandableStringEnum;

import java.util.Collection;

/**
 * Defines values for SetParticipantViewDataResult.
 */
public final class SetParticipantViewDataResult extends ExpandableStringEnum<SetParticipantViewDataResult> {
    public static final SetParticipantViewDataResult SUCCESS = fromString("success");
    public static final SetParticipantViewDataResult PARTICIPANT_NOT_IN_CALL = fromString("participantNotInCall");

    /**
     * Creates or finds a SetParticipantViewDataResult from it's string representation.
     *
     * @param name a name to look for.
     * @return the corresponding SetParticipantViewDataResult.
     */
    private static SetParticipantViewDataResult fromString(final String name) {
        return fromString(name, SetParticipantViewDataResult.class);
    }

    /**
     * @return known SetParticipantViewDataResult values.
     */
    public static Collection<SetParticipantViewDataResult> values() {
        return values(SetParticipantViewDataResult.class);
    }
}
