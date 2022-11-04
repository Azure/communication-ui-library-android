// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chatdemoapp.launcher;

import com.azure.android.communication.common.CommunicationTokenCredential;
import com.azure.android.communication.common.CommunicationTokenRefreshOptions;
import com.azure.android.communication.ui.chat.ChatManager;
import com.azure.android.communication.ui.chat.ChatCompositeBuilder;
import com.azure.android.communication.ui.chat.models.ChatCompositeJoinLocator;
import com.azure.android.communication.ui.chat.models.ChatCompositeRemoteOptions;
import com.azure.android.communication.ui.chat.presentation.ChatCompositeActivity;
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
                       final String endPointURL,
                       final String displayName,
                       final String identity) {
        final ChatManager chatComposite = new ChatCompositeBuilder().build(chatLauncherActivity);

        final CommunicationTokenRefreshOptions communicationTokenRefreshOptions =
                new CommunicationTokenRefreshOptions(tokenRefresher, true);
        final CommunicationTokenCredential communicationTokenCredential =
                new CommunicationTokenCredential(communicationTokenRefreshOptions);

        final ChatCompositeJoinLocator locator =
                new ChatCompositeJoinLocator(threadID, endPointURL);
        final ChatCompositeRemoteOptions remoteOptions =
                new ChatCompositeRemoteOptions(locator, communicationTokenCredential, identity, displayName);

        ChatCompositeActivity.startForChatThread(
                chatLauncherActivity,
                chatComposite.connectToChatThread(remoteOptions, null)
        );
    }
}
