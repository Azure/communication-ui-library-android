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
 * Chat thread view.
 */
public final class ChatThreadView extends FrameLayout {

    /**
     * Creates {@link ChatThreadView}
     * @param context
     */
    public ChatThreadView(final Context context) {
        super(context);
    }

    /**
     * Creates {@link ChatThreadView}
     * @param context
     * @param chatThreadAdapter
     */
    public ChatThreadView(final Context context, final ChatThreadAdapter chatThreadAdapter) {
        super(context);
        setChatAdapter(chatThreadAdapter);
    }

    /**
     * Creates {@link ChatThreadView}
     * @param context
     * @param attrs
     */
    public ChatThreadView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Creates {@link ChatThreadView}
     * @param context
     * @param attrs
     * @param chatThreadAdapter
     */
    public ChatThreadView(final Context context, final AttributeSet attrs, final ChatThreadAdapter chatThreadAdapter) {
        super(context, attrs);
        setChatAdapter(chatThreadAdapter);
    }

    void setChatAdapter(final ChatThreadAdapter chatThreadAdapter) {
        final ChatUIClient chatUiClient =  ChatThreadAdapterExtensionsKt.getChatUIClient(chatThreadAdapter);
        addView(new ChatCompositeViewImpl(this.getContext(), chatUiClient, false));
    }
}
