// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat;

import android.content.Context;

import com.azure.android.communication.ui.chat.configuration.ChatCompositeConfiguration;
import com.azure.android.communication.ui.chat.models.ChatCompositeRemoteOptions;
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
            final ChatCompositeRemoteOptions remoteOptions) {
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

    // TODO: remove this method. Test should provide it's own UI host and call
    //  launch() then getCompositeUIView()
    void launchTest(final Context context,
                    final ChatCompositeRemoteOptions remoteOptions) {
        chatContainer.start(context, remoteOptions);
        //showTestCompositeUI(context);
    }
}
