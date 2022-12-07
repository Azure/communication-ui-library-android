// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.presentation;

import android.content.Context;
import android.content.Intent;

import com.azure.android.communication.ui.chat.ChatUIClient;

class ChatCompositeActivity {
    private final Context context;

    ChatCompositeActivity(final Context context) {
        this.context = context;
    }

    public void launch(final ChatUIClient chatUIClient) {
        final Intent intent = new Intent(context, ChatCompositeActivityImpl.class);
        ChatCompositeActivityImpl.Companion.setChatUIClient(chatUIClient);
        context.startActivity(intent);
    }
}
