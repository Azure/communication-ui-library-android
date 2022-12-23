// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.presentation;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.azure.android.communication.ui.chat.ChatAdapter;
import com.azure.android.communication.ui.chat.presentation.ui.container.ChatCompositeViewImpl;

/**
 * ChatThreadView displays a chat thread.
 *
 * It can be constructed with or without a {@link ChatAdapter} to display the chat thread.
 * If a ChatThreadAdapter is provided, it will be set as the adapter for the ChatThreadView.
 *
 * @see ChatAdapter
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
     * @param chatAdapter the ChatAdapter to set for the ChatThreadView
     */
    public ChatThreadView(final Context context, final ChatAdapter chatAdapter) {
        super(context);
        setChatAdapter(chatAdapter);
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
     * @param chatAdapter the ChatAdapter to set for the ChatThreadView

     */
    public ChatThreadView(final Context context, final AttributeSet attrs, final ChatAdapter chatAdapter) {
        super(context, attrs);
        setChatAdapter(chatAdapter);
    }

    /**
     * Sets the ChatAdapter for the ChatThreadView.
     * @param chatAdapter the ChatAdapter to set for the ChatThreadView
     */
    void setChatAdapter(final ChatAdapter chatAdapter) {
        addView(new ChatCompositeViewImpl(this.getContext(), chatAdapter, false));
    }
}
