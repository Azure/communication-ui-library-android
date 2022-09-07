// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat;

import android.content.Context;
import android.content.Intent;

import com.azure.android.communication.ui.chat.presentation.ChatCompositeActivity;

public class ChatComposite {

    ChatComposite() {
    }

    public void launch(final Context context) {
        final Intent launchIntent = new Intent(context, ChatCompositeActivity.class);
        context.startActivity(launchIntent);
    }
}
