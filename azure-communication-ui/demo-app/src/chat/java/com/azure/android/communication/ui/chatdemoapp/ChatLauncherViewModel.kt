// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chatdemoapp

import android.content.Context
import android.webkit.URLUtil
import androidx.lifecycle.ViewModel
import com.azure.android.communication.common.CommunicationTokenCredential
import com.azure.android.communication.common.CommunicationTokenRefreshOptions
import com.azure.android.communication.common.CommunicationUserIdentifier
import com.azure.android.communication.ui.chat.ChatThreadAdapter
import com.azure.android.communication.ui.chat.ChatUIClient
import com.azure.android.communication.ui.chat.ChatUIClientBuilder
import java.util.concurrent.Callable

class ChatLauncherViewModel : ViewModel() {
    private var token: String? = null

    var chatUIClient: ChatUIClient? = null
    var chatThreadAdapter: ChatThreadAdapter? = null

    private fun getTokenFetcher(acsToken: String?): Callable<String> {
        val tokenRefresher = when {
            acsToken != null && acsToken.isNotBlank() -> {
                token = acsToken
                CachedTokenFetcher(acsToken)
            }
            else -> {
                throw IllegalStateException("Invalid Token function URL or acs Token")
            }
        }
        return tokenRefresher
    }

    fun launch(
        context: Context,
        endpoint: String,
        acsIdentity: String,
        threadId: String,
        userName: String,
        acsToken: String?,
    ) {
        // Create ChatAdapter
        val tokenRefresher = getTokenFetcher(acsToken)
        val communicationTokenRefreshOptions =
            CommunicationTokenRefreshOptions(tokenRefresher, true)
        val communicationTokenCredential =
            CommunicationTokenCredential(communicationTokenRefreshOptions)

        val chatUIClient = ChatUIClientBuilder()
            .context(context)
            .endpoint(endpoint)
            .credential(communicationTokenCredential)
            .identity(CommunicationUserIdentifier(acsIdentity))
            .displayName(userName)
            .build()

        val chatThreadAdapter = ChatThreadAdapter(chatUIClient, threadId)

        this.chatUIClient = chatUIClient
        this.chatThreadAdapter = chatThreadAdapter
    }

    private fun urlIsValid(url: String) = url.isNotBlank() && URLUtil.isValidUrl(url.trim())

    fun closeChatComposite() {

        chatUIClient = null
    }
}
