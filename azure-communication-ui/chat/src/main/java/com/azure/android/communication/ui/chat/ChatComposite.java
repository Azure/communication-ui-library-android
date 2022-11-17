// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat;

import android.content.Context;
import android.content.Intent;

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
public final class ChatComposite {

    private static int instanceIdCounter = 0;
    private final ChatContainer chatContainer;
    private final ChatCompositeConfiguration configuration;
    final Integer instanceId = instanceIdCounter++;

    ChatComposite(final ChatCompositeConfiguration configuration) {
        this.configuration = configuration;
        chatContainer = new ChatContainer(this, configuration, instanceId);
    }

    /**
     * Connects to backend services.
     */
    public CompletableFuture<Void> connect(
            final Context context,
            final String endpointUrl,
            final String threadId,
            final CommunicationTokenCredential credential,
            final String identity,
            final String displayName) {
        final ChatCompositeRemoteOptions remoteOptions =
                new ChatCompositeRemoteOptions(endpointUrl, threadId, credential, identity, displayName);
        launchComposite(context, remoteOptions, false);
        final CompletableFuture<Void> result = new CompletableFuture<>();
        result.complete(null);
        return result;
    }

    /**
     * Disconnects from backend services.
     */
    public void disconnect() {
    }


    private void launchComposite(final Context context,
                                 final ChatCompositeRemoteOptions remoteOptions,
                                 final boolean isTest) {
        chatContainer.start(context, remoteOptions);
    }

    void launchTest(final Context context,
                    final ChatCompositeRemoteOptions remoteOptions) {
        chatContainer.start(context, remoteOptions);
        showTestCompositeUI(context);
    }

    private void showTestCompositeUI(final Context context) {
        final Intent intent = new Intent(context, ChatCompositeActivityImpl.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ChatCompositeActivityImpl.Companion.setChatComposite(this);
        context.startActivity(intent);
    }
}
