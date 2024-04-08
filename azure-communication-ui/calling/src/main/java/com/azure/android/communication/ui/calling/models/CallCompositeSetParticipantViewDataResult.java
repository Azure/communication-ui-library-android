// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

import com.azure.android.communication.common.CommunicationIdentifier;
import com.azure.android.communication.ui.calling.CallComposite;
import com.azure.android.core.util.ExpandableStringEnum;

import java.util.Collection;

/**
 * Result values for
 * {@link CallComposite#setRemoteParticipantViewData(CommunicationIdentifier, CallCompositeParticipantViewData)}.
 */
public final class CallCompositeSetParticipantViewDataResult
        extends ExpandableStringEnum<CallCompositeSetParticipantViewDataResult> {

    /**
     * The Remote Participant View Data was Successfully set.
     */
    public static final CallCompositeSetParticipantViewDataResult SUCCESS = fromString("success");

    /**
     * The Remote Participant was not in the call.
     */
    public static final CallCompositeSetParticipantViewDataResult PARTICIPANT_NOT_IN_CALL
            = fromString("participantNotInCall");

    /**
     * Creates or finds a {@link CallCompositeSetParticipantViewDataResult} from it's string representation.
     *
     * @param name a name to look for.
     * @return the corresponding {@link CallCompositeSetParticipantViewDataResult}.
     */
    public static CallCompositeSetParticipantViewDataResult fromString(final String name) {
        return fromString(name, CallCompositeSetParticipantViewDataResult.class);
    }

    /**
     * @return known {@link CallCompositeSetParticipantViewDataResult} values.
     */
    public static Collection<CallCompositeSetParticipantViewDataResult> values() {
        return values(CallCompositeSetParticipantViewDataResult.class);
    }
}
