// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.presentation;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.azure.android.communication.ui.chat.ChatAdapter;
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
     * @param chatAdapter
     */
    public ChatThreadView(final Context context, final ChatAdapter chatAdapter) {
        super(context);
        setChatAdapter(chatAdapter);
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
     * @param chatAdapter
     */
    public ChatThreadView(final Context context, final AttributeSet attrs, final ChatAdapter chatAdapter) {
        super(context, attrs);
        setChatAdapter(chatAdapter);
    }

    void setChatAdapter(final ChatAdapter chatAdapter) {
        addView(new ChatCompositeViewImpl(this.getContext(), chatAdapter, false));
    }
}
