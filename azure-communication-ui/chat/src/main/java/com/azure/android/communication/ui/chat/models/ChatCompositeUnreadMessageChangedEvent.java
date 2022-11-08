// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.models;

/**
 * Unread message count changed event.
 */
public final class ChatCompositeUnreadMessageChangedEvent {
    private final int count;

    ChatCompositeUnreadMessageChangedEvent(final int count) {
        this.count = count;
    }

    /**
     * Get unread message changed count.
     *
     * @return int count.
     */
    public int getCount() {
        return count;
    }

    String getThreadID() {
        return "";
    }
}
