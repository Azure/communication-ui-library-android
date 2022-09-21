// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chatdemoapp.launcher;

import com.azure.android.communication.common.CommunicationTokenCredential;
import com.azure.android.communication.common.CommunicationTokenRefreshOptions;
import com.azure.android.communication.ui.chat.ChatComposite;
import com.azure.android.communication.ui.chat.ChatCompositeBuilder;
import com.azure.android.communication.ui.chat.models.ChatCompositeJoinLocator;
import com.azure.android.communication.ui.chat.models.ChatCompositeRemoteOptions;
import com.azure.android.communication.ui.chatdemoapp.ChatLauncherActivity;

import java.util.concurrent.Callable;

public class ChatCompositeJavaLauncher implements ChatCompositeLauncher {

    private final Callable<String> tokenRefresher;

    public ChatCompositeJavaLauncher(final Callable<String> tokenRefresher) {
        this.tokenRefresher = tokenRefresher;
    }

    @Override
    public void launch(final ChatLauncherActivity chatLauncherActivity,
                       final String threadID,
                       final String endPointURL) {
        final ChatComposite chatComposite = new ChatCompositeBuilder().build();

        final CommunicationTokenRefreshOptions communicationTokenRefreshOptions =
                new CommunicationTokenRefreshOptions(tokenRefresher, true);
        final CommunicationTokenCredential communicationTokenCredential =
                new CommunicationTokenCredential(communicationTokenRefreshOptions);

        final ChatCompositeJoinLocator locator =
                new ChatCompositeJoinLocator(threadID, endPointURL);
        final ChatCompositeRemoteOptions remoteOptions =
                new ChatCompositeRemoteOptions(locator, communicationTokenCredential);
        chatComposite.launch(chatLauncherActivity, remoteOptions, null);
    }
}
