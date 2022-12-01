// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.presentation;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.azure.android.communication.ui.chat.ChatUIClient;
import com.azure.android.communication.ui.chat.presentation.ui.container.ChatCompositeViewImpl;

/**
 * Chat composite view.
 */
public final class ChatCompositeView extends FrameLayout {

    public ChatCompositeView(final Context context) {
        super(context);
    }

    public ChatCompositeView(final Context context, final ChatUIClient chatUIClient) {
        super(context);
        setChatAdapter(chatUIClient);
    }

    public ChatCompositeView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public ChatCompositeView(final Context context, final AttributeSet attrs, final ChatUIClient chatUIClient) {
        super(context, attrs);
        setChatAdapter(chatUIClient);
    }

    public ChatCompositeView setChatAdapter(final ChatUIClient chatUIClient) {
        addView(new ChatCompositeViewImpl(this.getContext(), chatUIClient, false));
        return this;
    }
}
