// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.models;

public class ChatCompositeUnreadMessageChangedEvent {
    private final int count;

    public ChatCompositeUnreadMessageChangedEvent(final int count) {
        this.count = count;
    }

    public int getCount() {
        return count;
    }
}
