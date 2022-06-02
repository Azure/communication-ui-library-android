// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

import com.azure.android.core.util.ExpandableStringEnum;

import java.util.Collection;

/**
 * Defines values for CallCompositeSetParticipantViewDataResult.
 */
public final class CallCompositeSetParticipantViewDataResult
        extends ExpandableStringEnum<CallCompositeSetParticipantViewDataResult> {
    public static final CallCompositeSetParticipantViewDataResult SUCCESS = fromString("success");
    public static final CallCompositeSetParticipantViewDataResult PARTICIPANT_NOT_IN_CALL
            = fromString("participantNotInCall");

    /**
     * Creates or finds a CallCompositeSetParticipantViewDataResult from it's string representation.
     *
     * @param name a name to look for.
     * @return the corresponding CallCompositeSetParticipantViewDataResult.
     */
    private static CallCompositeSetParticipantViewDataResult fromString(final String name) {
        return fromString(name, CallCompositeSetParticipantViewDataResult.class);
    }

    /**
     * @return known CallCompositeSetParticipantViewDataResult values.
     */
    public static Collection<CallCompositeSetParticipantViewDataResult> values() {
        return values(CallCompositeSetParticipantViewDataResult.class);
    }
}
