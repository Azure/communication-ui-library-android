// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callwithchat.models;

public final class CallWithChatCompositeLocalOptions {
    private CallWithChatCompositeParticipantViewData participantViewData;

    /**
     * Get {@link CallWithChatCompositeParticipantViewData}.
     *
     */
    public CallWithChatCompositeLocalOptions() {
        participantViewData = null;
    }

    /**
     * Get {@link CallWithChatCompositeParticipantViewData}.
     *
     * @return The {@link CallWithChatCompositeParticipantViewData};
     */
    public CallWithChatCompositeParticipantViewData getParticipantViewData() {
        return participantViewData;
    }

    /**
     * Get {@link CallWithChatCompositeParticipantViewData}.
     *
     * @return The {@link CallWithChatCompositeParticipantViewData};
     */
    public CallWithChatCompositeLocalOptions setParticipantViewData(
            final CallWithChatCompositeParticipantViewData participantViewData) {
        this.participantViewData = participantViewData;
        return this;
    }

}
