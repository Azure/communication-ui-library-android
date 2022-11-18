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
    boolean titleBarEnabled = false;

    public ChatCompositeView(final Context context) {
        super(context);
    }

    public ChatCompositeView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public ChatCompositeView(final Context context, final AttributeSet attrs, final ChatAdapter chatAdapter) {
        super(context, attrs);
    }

    public ChatCompositeView setChatAdapter(final ChatAdapter chatAdapter) {
        if (getChildCount() != 0) {
            removeAllViews();
        }

        if (chatAdapter == null) {
            return this;
        }

        addView(new ChatCompositeViewImpl(this.getContext(), chatAdapter, titleBarEnabled));
        return this;
    }

    // Package Private ability to enable title bar
    void enableTitleBar() {
        titleBarEnabled = true;
    }
}
