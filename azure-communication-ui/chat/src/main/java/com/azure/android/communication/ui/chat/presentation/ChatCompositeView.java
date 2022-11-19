// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.presentation;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.azure.android.communication.ui.chat.ChatAdapter;
import com.azure.android.communication.ui.chat.presentation.ui.container.ChatCompositeViewImpl;

/**
 * Chat composite view.
 */
public final class ChatCompositeView extends FrameLayout {

    public ChatCompositeView(final Context context) {
        super(context);
    }

    public ChatCompositeView(final Context context, final ChatAdapter chatAdapter) {
        super(context);
        setChatAdapter(chatAdapter);
    }

    public ChatCompositeView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public ChatCompositeView(final Context context, final AttributeSet attrs, final ChatAdapter chatAdapter) {
        super(context, attrs);
        setChatAdapter(chatAdapter);
    }

    public ChatCompositeView setChatAdapter(final ChatAdapter chatAdapter) {
        addView(new ChatCompositeViewImpl(this.getContext(), chatAdapter, false));
        return this;
    }
}
