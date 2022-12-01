// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat;

public class ChatAdapter {
    private final ChatUIClient chatUIClient;
    private final String threadId;
    private final String topic;

    ChatAdapter(final ChatUIClient chatUIClient, final String threadId, final String topic) {
        this.chatUIClient = chatUIClient;
        this.threadId = threadId;
        this.topic = topic;
    }

    public String getThreadId() {
        return threadId;
    }

    public String getTopic() {
        return topic;
    }
}
