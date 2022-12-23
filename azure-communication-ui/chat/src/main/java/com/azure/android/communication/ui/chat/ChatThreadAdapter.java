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
     * Creates a new {@link ChatThreadAdapter}.
     *
     * @param chatUIClient the {@link ChatUIClient} to be used by the adapter
     * @param threadId the ID of the chat thread
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
     * Returns the ID of the chat thread.
     *
     * @return the ID of the chat thread
     */
    public String getThreadId() {
        return threadId;
    }

    /**
     * Returns the topic of the chat thread.
     *
     * @return the topic of the chat thread, or {@code null} if no topic is set
     */
    public String getTopic() {
        return topic;
    }

    /**
     * Returns the {@link ChatUIClient} used by this adapter.
     *
     * @return the {@link ChatUIClient} used by this adapter
     */
    ChatUIClient getChatUIClient() {
        return chatUIClient;
    }

    /**
     * Launches a test chat composite activity.
     *
     * @param context the context to use to start the activity
     */
    void launchTest(final Context context) {
        final Intent intent = new Intent(context, ChatCompositeActivityImpl.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ChatCompositeActivityImpl.Companion.setChatUIClient(chatUIClient);
        context.startActivity(intent);
    }

}
