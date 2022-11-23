// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat;

import android.content.Context;
import android.content.Intent;

import com.azure.android.communication.common.CommunicationTokenCredential;
import com.azure.android.communication.ui.chat.configuration.ChatCompositeConfiguration;
import com.azure.android.communication.ui.chat.models.ChatCompositeEvent;
import com.azure.android.communication.ui.chat.models.ChatCompositeRemoteOptions;
import com.azure.android.communication.ui.chat.presentation.ChatCompositeActivityImpl;

import java9.util.concurrent.CompletableFuture;

/**
 * Azure android communication chat composite component.
 *
 * <p><strong>Instantiating Chat Composite</strong></p>
 */
public final class ChatAdapter {

    private static int instanceIdCounter = 0;
    final Integer instanceId = instanceIdCounter++;
    private final ChatContainer chatContainer;
    private final String endpointUrl;
    private final String identity;
    private final CommunicationTokenCredential credential;
    private final String displayName;
    private final ChatCompositeConfiguration configuration;

    ChatAdapter(final ChatCompositeConfiguration configuration,
                final String endpointUrl,
                final String identity,
                final CommunicationTokenCredential credential,
                final String displayName) {
        chatContainer = new ChatContainer(this, configuration, instanceId);
        this.endpointUrl = endpointUrl;
        this.identity = identity;
        this.credential = credential;
        this.displayName = displayName;
        this.configuration = configuration;
    }

    /**
     * Connects to ACS service, starts realtime notifications.
     */
    public CompletableFuture<Void> connect(final Context context, final String threadId) {
        launchComposite(context, threadId);
        final CompletableFuture<Void> result = new CompletableFuture<>();
        result.complete(null);
        return result;
    }

    /**
     * Disconnects from backend services.
     */
    public void disconnect() {
    }

    /**
     * Add {@link ChatCompositeEventHandler} with {@link ChatCompositeEvent}.
     *
     * <p> A callback for Chat Composite  Events.
     * See {@link com.azure.android.communication.ui.chat.models.ChatCompositeEvent} for values.</p>
     *
     * @param eventHandler The {@link ChatCompositeEventHandler}.
     */
    public void addOnLocalParticipantRemovedEventHandler(
            final ChatCompositeEventHandler<ChatCompositeEvent> eventHandler) {
        configuration.getEventHandlerRepository().addLocalParticipantRemovedEventHandler(eventHandler);
    }

    /**
     * Remove {@link ChatCompositeEventHandler} with {@link ChatCompositeEvent}.
     *
     * <p> A callback for Call Composite Error Events.
     * See {@link com.azure.android.communication.ui.chat.models.ChatCompositeEvent} for
     * values.</p>
     *
     * @param eventHandler The {@link ChatCompositeEventHandler}.
     */
    public void removeOnLocalParticipantRemovedEventHandler(
            final ChatCompositeEventHandler<ChatCompositeEvent> eventHandler) {
        configuration.getEventHandlerRepository().removeLocalParticipantRemovedEventHandler(eventHandler);
    }

    private void launchComposite(final Context context, final String threadId) {
        final ChatCompositeRemoteOptions remoteOptions =
                new ChatCompositeRemoteOptions(
                        endpointUrl, threadId, credential, identity, displayName != null ? displayName : "");
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
        ChatCompositeActivityImpl.Companion.setChatAdapter(this);
        context.startActivity(intent);
    }
}
