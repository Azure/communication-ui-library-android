// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.presentation;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.azure.android.communication.ui.chat.ChatThreadAdapter;
import com.azure.android.communication.ui.chat.ChatThreadAdapterExtensionsKt;
import com.azure.android.communication.ui.chat.ChatUIClient;
import com.azure.android.communication.ui.chat.presentation.ui.container.ChatCompositeViewImpl;

/**
 * ChatThreadView displays a chat thread.
 *
 * It can be constructed with or without a {@link ChatThreadAdapter} to display the chat thread.
 * If a ChatThreadAdapter is provided, it will be set as the adapter for the ChatThreadView.
 *
 * @see ChatThreadAdapter
 * @see ChatUIClient
 */
public final class ChatThreadView extends FrameLayout {

    /**
     * Creates a ChatThreadView without a ChatThreadAdapter.
     * Note: Will not display anything unless an adapter is bound
     * @param context the context to use for creating the ChatThreadView
     */
    public ChatThreadView(final Context context) {
        super(context);
    }

    /**
     * Creates a ChatThreadView with a ChatThreadAdapter.
     * @param context the context to use for creating the ChatThreadView
     * @param chatThreadAdapter the ChatThreadAdapter to set for the ChatThreadView
     */
    public ChatThreadView(final Context context, final ChatThreadAdapter chatThreadAdapter) {
        super(context);
        setChatAdapter(chatThreadAdapter);
    }

    /**
     * Creates a ChatThreadView with attributes from an XML file.
     * The ChatThreadView is created without a ChatThreadAdapter.
     * Note: Will not display anything unless an adapter is bound
     * @param context the context to use for creating the ChatThreadView
     * @param attrs the attributes from the XML file
     */
    public ChatThreadView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Creates a ChatThreadView with attributes from an XML file and a ChatThreadAdapter.
     * @param context the context to use for creating the ChatThreadView
     * @param attrs the attributes from the XML file
     * @param chatThreadAdapter the ChatThreadAdapter to set for the ChatThreadView
     */
    public ChatThreadView(final Context context, final AttributeSet attrs, final ChatThreadAdapter chatThreadAdapter) {
        super(context, attrs);
        setChatAdapter(chatThreadAdapter);
    }

    /**
     * Sets the ChatThreadAdapter for the ChatThreadView.
     * @param chatThreadAdapter the ChatThreadAdapter to set for the ChatThreadView
     */
    public void setChatAdapter(final ChatThreadAdapter chatThreadAdapter) {
        final ChatUIClient chatUiClient =  ChatThreadAdapterExtensionsKt.getChatUIClient(chatThreadAdapter);
        addView(new ChatCompositeViewImpl(this.getContext(), chatUiClient, false));
    }
}
