// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callwithchat.models;

public final class CallWithChatCompositeLocalOptions {
    private final CallWithChatCompositeParticipantViewData participantViewData;

    /**
     * Create Local Options.
     *
     * @param participantViewData The {@link CallWithChatCompositeParticipantViewData};
     */
    public CallWithChatCompositeLocalOptions(final CallWithChatCompositeParticipantViewData participantViewData) {
        this.participantViewData = participantViewData;
    }

    /**
     * Get {@link CallWithChatCompositeParticipantViewData}.
     *
     * @return The {@link CallWithChatCompositeParticipantViewData};
     */
    public CallWithChatCompositeParticipantViewData getParticipantViewData() {
        return participantViewData;
    }
}
