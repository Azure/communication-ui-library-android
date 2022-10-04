// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.models;

/**
 * Chat locator
 */
public class ChatCompositeJoinLocator {

    private final String chatThreadId;
    private final String endpoint;

    public ChatCompositeJoinLocator(final String endpoint,
                                    final String chatThreadId) {
        this.endpoint = endpoint;
        this.chatThreadId = chatThreadId;
    }

    public String getChatThreadId() {
        return chatThreadId;
    }

    public String getEndpoint() {
        return endpoint;
    }

}
