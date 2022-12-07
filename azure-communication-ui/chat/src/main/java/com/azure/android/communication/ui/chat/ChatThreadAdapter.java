// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat;

import android.content.Context;
import android.content.Intent;

import com.azure.android.communication.ui.chat.models.ChatCompositeRemoteOptions;
import com.azure.android.communication.ui.chat.presentation.ChatCompositeActivityImpl;

/**
 * Azure android communication chat thread adapter.
 */
public class ChatThreadAdapter {
    private final ChatUIClient chatUIClient;
    private final String threadId;
    private final String topic = null;

    /**
     * Creates {@link ChatThreadAdapter}
     * @param chatUIClient
     * @param threadId
     */
    public ChatThreadAdapter(final ChatUIClient chatUIClient, final String threadId) {
        this.chatUIClient = chatUIClient;
        this.threadId = threadId;

        String displayName = chatUIClient.getDisplayName();

        if (displayName == null) {
            displayName = "";
        }

        final ChatCompositeRemoteOptions remoteOptions = new ChatCompositeRemoteOptions(
                chatUIClient.getEndpoint(),
                threadId,
                chatUIClient.getCredential(),
                chatUIClient.getIdentity(),
                displayName);

        final ChatContainer chatContainer =
                new ChatContainer(chatUIClient, chatUIClient.getConfiguration(), chatUIClient.instanceId);

        chatContainer.start(chatUIClient.getApplicationContextContext(), remoteOptions);
    }

    /**
     * Get chat thread Id.
     * @return
     */
    public String getThreadId() {
        return threadId;
    }

    /**
     * Get chat thread topic.
     * @return
     */
    public String getTopic() {
        return topic;
    }

    ChatUIClient getChatUIClient() {
        return chatUIClient;
    }

    void launchTest(final Context context) {
        final Intent intent = new Intent(context, ChatCompositeActivityImpl.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ChatCompositeActivityImpl.Companion.setChatUIClient(chatUIClient);
        context.startActivity(intent);
    }

}
