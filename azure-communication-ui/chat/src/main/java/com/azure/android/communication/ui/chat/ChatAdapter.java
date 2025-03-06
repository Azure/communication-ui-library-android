// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat;

import android.content.Context;
import android.content.Intent;

import com.azure.android.communication.common.CommunicationIdentifier;
import com.azure.android.communication.common.CommunicationTokenCredential;
import com.azure.android.communication.ui.chat.configuration.ChatCompositeConfiguration;
import com.azure.android.communication.ui.chat.models.ChatCompositeErrorEvent;
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
    private ChatContainer chatContainer;
    private boolean autoFocus = false;
    private final String endpoint;
    private final CommunicationIdentifier identity;
    private final CommunicationTokenCredential credential;
    private final String threadId;
    private final String displayName;
    private final ChatCompositeConfiguration configuration;

    ChatAdapter(final ChatCompositeConfiguration configuration,
                final String endpoint,
                final CommunicationIdentifier identity,
                final CommunicationTokenCredential credential,
                final String threadId,
                final String displayName,
                final boolean autoFocus) {

        this.endpoint = endpoint;
        this.identity = identity;
        this.credential = credential;
        this.threadId = threadId;
        this.displayName = displayName;
        this.configuration = configuration;
        this.autoFocus = autoFocus;
    }


    /**
     * Add {@link ChatCompositeEventHandler}.
     *
     * <p> A callback for Chat Composite Error Events.
     * See {@link ChatCompositeErrorEvent} for values.</p>
     * <pre>
     *
     * &#47;&#47; add error handler
     * chatAdapter.addOnErrorEventHandler&#40;event -> {
     *     &#47;&#47; Process error event
     *     System.out.println&#40;event.getCause&#40;&#41;&#41;;
     *     System.out.println&#40;event.getErrorCode&#40;&#41;&#41;;
     * }&#41;;
     *
     * </pre>
     *
     * @param errorHandler The {@link ChatCompositeEventHandler}.
     */
    public void addOnErrorEventHandler(final ChatCompositeEventHandler<ChatCompositeErrorEvent> errorHandler) {
        configuration.getEventHandlerRepository().addOnErrorEventHandler(errorHandler);
    }

    /**
     * Remove {@link ChatCompositeEventHandler}.
     *
     * <p> A callback for Chat Composite Error Events.
     * See {@link ChatCompositeErrorEvent} for values.</p>
     *
     * @param errorHandler The {@link ChatCompositeEventHandler}.
     */
    public void removeOnErrorEventHandler(final ChatCompositeEventHandler<ChatCompositeErrorEvent> errorHandler) {
        configuration.getEventHandlerRepository().removeOnErrorEventHandler(errorHandler);
    }

    /**
     * Connects to ACS service, starts realtime notifications.
     */
    public CompletableFuture<Void> connect(final Context context) {
        chatContainer = new ChatContainer(this, configuration, instanceId);

        launchComposite(context.getApplicationContext(), threadId);
        final CompletableFuture<Void> result = new CompletableFuture<>();
        result.complete(null);
        return result;
    }

    /**
     * Disconnects from backend services.
     */
    public void disconnect() {
        chatContainer.stop();
        chatContainer = null;
    }

    private void launchComposite(final Context context, final String threadId) {
        final ChatCompositeRemoteOptions remoteOptions =
                new ChatCompositeRemoteOptions(
                        endpoint, threadId, credential, identity, displayName != null ? displayName : "");
        chatContainer.start(context, remoteOptions);
    }

    void showTestCompositeUI(final Context context) {
        final Intent intent = new Intent(context, ChatCompositeActivityImpl.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ChatCompositeActivityImpl.Companion.setChatAdapter(this);
        context.startActivity(intent);
    }

    public boolean getAutoFocus() {
        return autoFocus;
    }
}
