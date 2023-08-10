// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chatdemoapp.launcher

import android.content.Context
import java.util.concurrent.Callable

class ChatCompositeKotlinLauncher(private val tokenRefresher: Callable<String>) :
    ChatCompositeLauncher {

    override fun launch(
        context: Context,
        threadID: String?,
        endPointURL: String?,
        displayName: String?,
        identity: String?,
    ) {
//        val chatComposite = ChatCompositeBuilder().build()
//        val communicationTokenRefreshOptions =
//            CommunicationTokenRefreshOptions(tokenRefresher, true)
//        val communicationTokenCredential =
//            CommunicationTokenCredential(communicationTokenRefreshOptions)
//        val locator = ChatCompositeJoinLocator(threadID, endPointURL)
//        val remoteOptions =
//            ChatCompositeRemoteOptions(locator, communicationTokenCredential, identity, displayName)
//        chatComposite.launch(context, remoteOptions, ChatCompositeLocalOptions())
    }
}
