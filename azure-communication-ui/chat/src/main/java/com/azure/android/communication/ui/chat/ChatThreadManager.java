// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat;

import android.content.Context;

import com.azure.android.communication.ui.chat.models.ChatCompositeLocalOptions;
import com.azure.android.communication.ui.chat.models.ChatCompositeRemoteOptions;
import com.azure.android.communication.ui.chat.models.ChatCompositeUnreadMessageChangedEvent;

public class ChatThreadManager {
    final ChatThreadContainer container;



    ChatThreadManager(final ChatThreadContainer container) {
        this.container = container;
    }


    /**
     * Add {@link ChatCompositeEventHandler} with {@link ChatCompositeUnreadMessageChangedEvent}.
     *
     * @param handler The {@link ChatCompositeEventHandler}.
     */
    public void addOnUnreadMessagesChangedEventHandler(
            final ChatCompositeEventHandler<ChatCompositeUnreadMessageChangedEvent> handler) {
    }

    /**
     * Remove {@link ChatCompositeEventHandler} with {@link ChatCompositeUnreadMessageChangedEvent}.
     *
     * @param handler The {@link ChatCompositeEventHandler}.
     */
    public void removeOnUnreadMessagesChangedEventHandler(
            final ChatCompositeEventHandler<ChatCompositeUnreadMessageChangedEvent> handler) {

    }

    /**
     * Detaches from this Thread
     */
    public void stop() {
        container.stop();
    }

    /**
     * Starts listening to a thread
     *
     * Implicitly started by ChatManager
     *
     * @param context
     * @param remoteOptions
     * @param localOptions
     */
    void start(
            final Context context,
            final ChatCompositeRemoteOptions remoteOptions,
            final ChatCompositeLocalOptions localOptions) {
        if (remoteOptions == null) {
            throw new IllegalArgumentException("Remote Options can not be null");
        }
        if (context == null) {
            throw new IllegalArgumentException("Context can not be null");
        }
        container.start(context, remoteOptions, localOptions);
    }

}
