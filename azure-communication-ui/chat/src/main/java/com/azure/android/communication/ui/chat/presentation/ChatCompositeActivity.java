// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.presentation;

import android.content.Context;
import android.content.Intent;

import com.azure.android.communication.ui.chat.ChatComposite;
import com.azure.android.communication.ui.chat.presentation.ui.container.ChatCompositeActivityImpl;

public class ChatCompositeActivity {
    private final Context context;

    public ChatCompositeActivity(final Context context) {
        this.context = context;
    }

    public void launch(final ChatComposite chatComposite) {
        final Intent intent = new Intent(context, ChatCompositeActivityImpl.class);
        ChatCompositeActivityImpl.Companion.setChatComposite(chatComposite);
        context.startActivity(intent);
    }
}
