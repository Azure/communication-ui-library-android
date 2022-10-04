// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.models;

public final class ChatCompositeLocalOptions {

    private boolean isBackgroundMode;
    private final ChatCompositeParticipantViewData participantViewData;

    public ChatCompositeLocalOptions(final ChatCompositeParticipantViewData participantViewData) {
        this.participantViewData = participantViewData;
    }

    public boolean getIsBackgroundMode() {
        return isBackgroundMode;
    }

    // what should be default value?
    public void setIsBackgroundMode(final boolean isBackgroundMode) {
        this.isBackgroundMode = isBackgroundMode;
    }

    public ChatCompositeParticipantViewData getParticipantViewData() {
        return participantViewData;
    }
}
