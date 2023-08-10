// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package chatdemoapp.launcher;

import android.content.Context;


import java.util.concurrent.Callable;

public class ChatCompositeJavaLauncher implements ChatCompositeLauncher {

    private final Callable<String> tokenRefresher;

    public ChatCompositeJavaLauncher(final Callable<String> tokenRefresher) {
        this.tokenRefresher = tokenRefresher;
    }

    @Override
    public void launch(final Context context,
                       final String threadID,
                       final String endPointURL,
                       final String displayName,
                       final String identity) {
//        final ChatComposite chatComposite = new ChatCompositeBuilder().build();
//
//        final CommunicationTokenRefreshOptions communicationTokenRefreshOptions =
//                new CommunicationTokenRefreshOptions(tokenRefresher, true);
//        final CommunicationTokenCredential communicationTokenCredential =
//                new CommunicationTokenCredential(communicationTokenRefreshOptions);
//
//        final ChatCompositeJoinLocator locator =
//                new ChatCompositeJoinLocator(threadID, endPointURL);
//        final ChatCompositeRemoteOptions remoteOptions =
//                new ChatCompositeRemoteOptions(locator, communicationTokenCredential, identity, displayName);
//        chatComposite.launch(context, remoteOptions, null);

    }
}
