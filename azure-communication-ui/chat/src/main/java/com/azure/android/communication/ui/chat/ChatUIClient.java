// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat;

import android.content.Context;

import com.azure.android.communication.common.CommunicationIdentifier;
import com.azure.android.communication.common.CommunicationTokenCredential;
import com.azure.android.communication.ui.chat.configuration.ChatCompositeConfiguration;
import com.azure.android.communication.ui.chat.models.ChatCompositeErrorEvent;

/**
 * Azure android communication chat composite component.
 *
 * <p><strong>Instantiating Chat Composite</strong></p>
 */
public final class ChatUIClient {

    private static int instanceIdCounter = 0;
    final Integer instanceId = instanceIdCounter++;
    private final Context applicationContext;
    private final String endpoint;
    private final CommunicationIdentifier identity;
    private final CommunicationTokenCredential credential;
    private final String displayName;
    private final ChatCompositeConfiguration configuration;

    ChatUIClient(final Context applicationContext,
            final ChatCompositeConfiguration configuration,
                 final String endpoint,
                 final CommunicationIdentifier identity,
                 final CommunicationTokenCredential credential,
                 final String displayName) {
        this.applicationContext = applicationContext;
        this.endpoint = endpoint;
        this.identity = identity;
        this.credential = credential;
        this.displayName = displayName;
        this.configuration = configuration;
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

    String getEndpoint() {
        return endpoint;
    }

    CommunicationIdentifier getIdentity() {
        return identity;
    }

    CommunicationTokenCredential getCredential() {
        return credential;
    }

    String getDisplayName() {
        return displayName;
    }

    ChatCompositeConfiguration getConfiguration() {
        return configuration;
    }

    Context getApplicationContextContext() {
        return applicationContext;
    }
}
