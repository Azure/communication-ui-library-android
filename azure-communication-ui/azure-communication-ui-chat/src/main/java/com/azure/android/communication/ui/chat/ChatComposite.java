// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.azure.android.communication.ui.chat.configuration.ChatCompositeConfiguration;

import com.azure.android.communication.ui.chat.models.ChatCompositeException;
import com.azure.android.communication.ui.chat.models.ChatCompositeLocalOptions;
import com.azure.android.communication.ui.chat.models.ChatCompositeRemoteOptions;

import com.azure.android.communication.ui.chat.implementation.ui.AcsComposeChatActivity;
import com.azure.android.communication.ui.chat.implementation.ui.view.LiveStateComposeChatView;
import com.azure.android.communication.ui.chat.models.ChatCompositeLocalizationOptions;
import com.azure.android.communication.ui.chat.models.ChatCompositeUnreadMessageChangedEvent;

public final class ChatComposite {
    private static int instanceIdCounter = 0;

    private final ChatContainer chatContainer;
    private final int instanceId;
    private final ChatCompositeLocalizationOptions chatCompositeLocalizationOptions;

    ChatComposite(final ChatCompositeConfiguration configuration) {
        instanceId = instanceIdCounter++;
        chatCompositeLocalizationOptions = configuration.getLocalizationConfig();
        chatContainer = new ChatContainer(configuration, instanceId);
    }

    public void launch(final Context context,
                       final ChatCompositeRemoteOptions remoteOptions) {
        launch(context, remoteOptions, null);
    }

    public void launch(final Context context,
                       final ChatCompositeRemoteOptions remoteOptions,
                       final ChatCompositeLocalOptions localOptions) {
        chatContainer.start(context, remoteOptions, localOptions);

        if (localOptions == null || !localOptions.getIsBackgroundMode()) {
            final Intent launchIntent = new Intent(context, AcsComposeChatActivity.class);
            launchIntent.putExtra(AcsComposeChatActivity.KEY_LOCALE,
                    String.valueOf(chatCompositeLocalizationOptions.getLocale()));
            launchIntent.putExtra(AcsComposeChatActivity.KEY_INSTANCE_ID, instanceId);
            context.startActivity(launchIntent);
            chatContainer.initUINotifier();
        }
    }

    // TODO: POC code to stop in background mode
    // name will change in future
    // The name will be discussed as part of finalizing API
    // Do not get notifications from Contoso
    public void stop() {
        // if forground mode
            // destroy UI
        // destroy service layer
        chatContainer.stop();
    }

    public View getCompositeUIView(final Context context) {
        // TODO: track LiveStateComposeChatView instance - if getCompositeUIView was already
        // called and not getCompositeUIView() nor stop() is called return same instance
        return new LiveStateComposeChatView(context, instanceId);
    }

    public void showCompositeUI(final Context context) throws ChatCompositeException {
        // if (ui is already shown or instance tracked by getCompositeUIView() is not disposed yet)
        // throw an exception
//        throw new ChatCompositeException("");
    }

    public void hideCompositeUI(final Context context) {

    }

    public void addOnViewClosedEventHandler(final ChatCompositeEventHandler<Object> handler) {
        chatContainer.addOnViewClosedEventHandler(handler);
    }

    public void removeOnViewClosedEventHandler(final ChatCompositeEventHandler<Object> handler) {
        chatContainer.removeOnViewClosedEventHandler(handler);
    }

    public void addOnUnreadMessagesChangedEventHandler(
            final ChatCompositeEventHandler<ChatCompositeUnreadMessageChangedEvent> handler) {
        chatContainer.addOnUnreadMessagesChangedEventHandler(handler);
    }

    public void removeOnUnreadMessagesChangedEventHandler(
            final ChatCompositeEventHandler<ChatCompositeUnreadMessageChangedEvent> handler) {
        chatContainer.removeOnUnreadMessagesChangedEventHandler(handler);
    }
}
