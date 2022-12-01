// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat;

import android.content.Context;
import android.content.Intent;

import com.azure.android.communication.common.CommunicationIdentifier;
import com.azure.android.communication.common.CommunicationTokenCredential;
import com.azure.android.communication.ui.chat.configuration.ChatCompositeConfiguration;
import com.azure.android.communication.ui.chat.models.ChatCompositeRemoteOptions;
import com.azure.android.communication.ui.chat.presentation.ChatCompositeActivityImpl;

import java9.util.concurrent.CompletableFuture;

/**
 * Azure android communication chat composite component.
 *
 * <p><strong>Instantiating Chat Composite</strong></p>
 */
public final class ChatUIClient {

    private static int instanceIdCounter = 0;
    final Integer instanceId = instanceIdCounter++;
    private final ChatContainer chatContainer;
    private final String endpoint;
    private final CommunicationIdentifier identity;
    private final CommunicationTokenCredential credential;
    private final String displayName;
    private final ChatCompositeConfiguration configuration;

    ChatUIClient(final ChatCompositeConfiguration configuration,
                final String endpoint,
                final CommunicationIdentifier identity,
                final CommunicationTokenCredential credential,
                final String displayName) {
        chatContainer = new ChatContainer(this, configuration, instanceId);
        this.endpoint = endpoint;
        this.identity = identity;
        this.credential = credential;
        this.displayName = displayName;
        this.configuration = configuration;
    }

    /**
     * Connects to ACS service, starts realtime notifications.
     */
    public CompletableFuture<ChatAdaptor> connect(final Context context, final String threadId) {
        launchComposite(context, threadId);
        final CompletableFuture<ChatAdaptor> result = new CompletableFuture<>();
        result.complete(new ChatAdaptor(this, threadId, ""));
        return result;
    }

    /**
     * Disconnects from backend services.
     */
    public void disconnect(final ChatAdaptor chatAdaptor) {
        chatContainer.stop();
    }

    private void launchComposite(final Context context, final String threadId) {
        final ChatCompositeRemoteOptions remoteOptions =
                new ChatCompositeRemoteOptions(
                        endpoint, threadId, credential, identity, displayName != null ? displayName : "");
        chatContainer.start(context, remoteOptions);
    }

    void launchTest(final Context context,
                    final String threadId) {
        launchComposite(context, threadId);
        showTestCompositeUI(context);
    }

    private void showTestCompositeUI(final Context context) {
        final Intent intent = new Intent(context, ChatCompositeActivityImpl.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ChatCompositeActivityImpl.Companion.setChatUIClient(this);
        context.startActivity(intent);
    }
}
