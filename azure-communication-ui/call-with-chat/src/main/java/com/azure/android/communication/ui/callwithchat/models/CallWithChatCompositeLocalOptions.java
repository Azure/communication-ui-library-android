// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callwithchat.models;

public final class CallWithChatCompositeLocalOptions {
    private CallWithChatCompositeParticipantViewData participantViewData;
    private CallWithChatCompositeNavigationBarViewData navigationBarViewData;

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

    /**
     * Get {@link CallWithChatCompositeParticipantViewData}.
     *
     * @return The {@link CallWithChatCompositeParticipantViewData};
     */
    public CallWithChatCompositeParticipantViewData getParticipantViewData() {
        return participantViewData;
    }

    /**
     * Get the {@link CallWithChatCompositeNavigationBarViewData}
     * @return The {@link CallWithChatCompositeNavigationBarViewData} that is currently set
     */
    public CallWithChatCompositeNavigationBarViewData getNavigationBarViewData() {
        return navigationBarViewData;
    }

    /**
     * Set a {@link CallWithChatCompositeNavigationBarViewData} to be used
     * @param navigationBarViewData The navigation bar view data object to be used
     * @return The current {@link CallWithChatCompositeLocalOptions} object for Fluent use
     */
    public CallWithChatCompositeLocalOptions setNavigationBarViewData(
            final CallWithChatCompositeNavigationBarViewData navigationBarViewData) {
        this.navigationBarViewData = navigationBarViewData;
        return this;
    }
}
