// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.models;

import com.azure.android.communication.ui.chat.ChatManager;

/**
 * Chat locator to join chat experience using {@link ChatManager}.
 */
public final class ChatCompositeJoinLocator {

    private final String chatThreadId;
    private final String endpointURL;

    /**
     * Creates {@link ChatCompositeJoinLocator}.
     *
     * @param chatThreadId Chat thread id.
     * @param endpointURL  Chat end point URL.
     */
    public ChatCompositeJoinLocator(final String chatThreadId, final String endpointURL) {
        this.chatThreadId = chatThreadId;
        this.endpointURL = endpointURL;
    }

    /**
     * Get chat thread id.
     *
     * @return {@link String}
     */
    public String getChatThreadId() {
        return chatThreadId;
    }

    /**
     * Get chat end point URL.
     *
     * @return {@link String}
     */
    public String getEndpointURL() {
        return endpointURL;
    }
}
