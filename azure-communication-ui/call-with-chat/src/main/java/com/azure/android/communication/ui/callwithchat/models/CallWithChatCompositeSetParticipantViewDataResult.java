// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callwithchat.models;

import com.azure.android.communication.common.CommunicationIdentifier;
import com.azure.android.communication.ui.callwithchat.CallWithChatComposite;
import com.azure.android.core.util.ExpandableStringEnum;

import java.util.Collection;

/**
 * Result values for
 * {@link CallWithChatComposite#setRemoteParticipantViewData(
 *      CommunicationIdentifier, CallWithChatCompositeParticipantViewData)}.
 */
public final class CallWithChatCompositeSetParticipantViewDataResult
        extends ExpandableStringEnum<CallWithChatCompositeSetParticipantViewDataResult> {

    /**
     * The Remote Participant View Data was Successfully set.
     */
    public static final CallWithChatCompositeSetParticipantViewDataResult SUCCESS = fromString("success");

    /**
     * The Remote Participant was not in the call.
     */
    public static final CallWithChatCompositeSetParticipantViewDataResult PARTICIPANT_NOT_IN_CALL
            = fromString("participantNotInCall");

    /**
     * Creates or finds a {@link CallWithChatCompositeSetParticipantViewDataResult} from it's string representation.
     *
     * @param name a name to look for.
     * @return the corresponding {@link CallWithChatCompositeSetParticipantViewDataResult}.
     */
    private static CallWithChatCompositeSetParticipantViewDataResult fromString(final String name) {
        return fromString(name, CallWithChatCompositeSetParticipantViewDataResult.class);
    }

    /**
     * @return known {@link CallWithChatCompositeSetParticipantViewDataResult} values.
     */
    public static Collection<CallWithChatCompositeSetParticipantViewDataResult> values() {
        return values(CallWithChatCompositeSetParticipantViewDataResult.class);
    }
}
