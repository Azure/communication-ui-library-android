// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.presentation;

import android.content.Context;
import android.widget.FrameLayout;

import com.azure.android.communication.ui.chat.ChatComposite;
import com.azure.android.communication.ui.chat.presentation.ui.container.ChatCompositeViewImpl;

/**
 * Chat composite view.
 */
public final class ChatCompositeView extends FrameLayout {

    /**
     * Creates chat composite view
     * @param context
     * @param chatComposite
     */
    public ChatCompositeView(final Context context, final ChatComposite chatComposite) {
        super(context);
        addView(new ChatCompositeViewImpl(context, chatComposite));
    }
}
