// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.presentation;

import android.content.Context;
import android.widget.FrameLayout;

import com.azure.android.communication.ui.chat.ChatAdapter;
import com.azure.android.communication.ui.chat.presentation.ui.container.ChatCompositeViewImpl;

/**
 * Chat composite view.
 */
public final class ChatCompositeView extends FrameLayout {

    /**
     * Creates chat composite view
     * @param context
     * @param chatAdapter
     */
    public ChatCompositeView(final Context context, final ChatAdapter chatAdapter) {
        super(context);
        addView(new ChatCompositeViewImpl(context, chatAdapter));
    }
}
