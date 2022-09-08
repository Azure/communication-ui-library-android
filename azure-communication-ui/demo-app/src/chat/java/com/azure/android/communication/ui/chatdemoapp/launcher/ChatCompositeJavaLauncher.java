// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chatdemoapp.launcher;

import com.azure.android.communication.ui.chat.ChatComposite;
import com.azure.android.communication.ui.chat.ChatCompositeBuilder;
import com.azure.android.communication.ui.chatdemoapp.ChatLauncherActivity;

public class ChatCompositeJavaLauncher implements ChatCompositeLauncher {
    @Override
    public void launch(final ChatLauncherActivity chatLauncherActivity) {
        final ChatComposite chatComposite = new ChatCompositeBuilder().build();
        chatComposite.launch(chatLauncherActivity, null, null);
    }
}
