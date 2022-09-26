// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chatdemoapp.launcher

import com.azure.android.communication.common.CommunicationTokenCredential
import com.azure.android.communication.common.CommunicationTokenRefreshOptions
import com.azure.android.communication.ui.chat.ChatCompositeBuilder
import com.azure.android.communication.ui.chat.models.ChatCompositeJoinLocator
import com.azure.android.communication.ui.chat.models.ChatCompositeRemoteOptions
import com.azure.android.communication.ui.chatdemoapp.ChatLauncherActivity
import java.util.concurrent.Callable

class ChatCompositeKotlinLauncher(private val tokenRefresher: Callable<String>) :
    ChatCompositeLauncher {

    override fun launch(
        chatLauncherActivity: ChatLauncherActivity?,
        threadID: String?,
        endPointURL: String?,
        displayName: String?,
        identity: String?,
    ) {
        val chatComposite = ChatCompositeBuilder().build()
        val communicationTokenRefreshOptions =
            CommunicationTokenRefreshOptions(tokenRefresher, true)
        val communicationTokenCredential =
            CommunicationTokenCredential(communicationTokenRefreshOptions)
        val locator = ChatCompositeJoinLocator(threadID, endPointURL)
        val remoteOptions =
            ChatCompositeRemoteOptions(locator, communicationTokenCredential, identity, displayName)
        chatComposite.launch(chatLauncherActivity, remoteOptions, null)
    }
}
