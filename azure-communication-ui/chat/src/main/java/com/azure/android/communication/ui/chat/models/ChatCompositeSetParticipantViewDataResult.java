// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.models;

import com.azure.android.communication.common.CommunicationIdentifier;
import com.azure.android.communication.ui.chat.ChatManager;
import com.azure.android.core.util.ExpandableStringEnum;

import java.util.Collection;

/**
 * Result values for
 * {@link ChatManager#setRemoteParticipantViewData(CommunicationIdentifier, ChatCompositeParticipantViewData)}.
 */
public final class ChatCompositeSetParticipantViewDataResult
        extends ExpandableStringEnum<ChatCompositeSetParticipantViewDataResult> {

    /**
     * The Remote Participant View Data was Successfully set.
     */
    public static final ChatCompositeSetParticipantViewDataResult SUCCESS = fromString("success");

    /**
     * The Remote Participant was not in the chat.
     */
    public static final ChatCompositeSetParticipantViewDataResult PARTICIPANT_NOT_IN_CHAT
            = fromString("participantNotInChat");

    /**
     * Creates or finds a {@link ChatCompositeSetParticipantViewDataResult} from it's string representation.
     *
     * @param name a name to look for.
     * @return the corresponding {@link ChatCompositeSetParticipantViewDataResult}.
     */
    private static ChatCompositeSetParticipantViewDataResult fromString(final String name) {
        return fromString(name, ChatCompositeSetParticipantViewDataResult.class);
    }

    /**
     * @return collection of {@link ChatCompositeSetParticipantViewDataResult} values.
     */
    public static Collection<ChatCompositeSetParticipantViewDataResult> values() {
        return values(ChatCompositeSetParticipantViewDataResult.class);
    }
}

