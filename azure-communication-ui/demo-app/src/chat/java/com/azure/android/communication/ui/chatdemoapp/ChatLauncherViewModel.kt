// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chatdemoapp

import android.content.Context
import android.webkit.URLUtil
import androidx.lifecycle.ViewModel
import com.azure.android.communication.common.CommunicationTokenCredential
import com.azure.android.communication.common.CommunicationTokenRefreshOptions
import com.azure.android.communication.ui.chat.ChatAdapter
import com.azure.android.communication.ui.chat.ChatAdapterBuilder
import com.azure.android.communication.ui.demoapp.UrlTokenFetcher
import java.util.concurrent.Callable

class ChatLauncherViewModel : ViewModel() {
    private var token: String? = null

    var chatAdapter: ChatAdapter? = null

    private fun getTokenFetcher(tokenFunctionURL: String?, acsToken: String?): Callable<String> {
        val tokenRefresher = when {
            tokenFunctionURL != null && urlIsValid(tokenFunctionURL) -> {
                token = null
                UrlTokenFetcher(tokenFunctionURL)
            }
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
        tokenFunctionURL: String?,
        acsToken: String?,
    ) {
        val tokenRefresher = getTokenFetcher(tokenFunctionURL, acsToken)

        val communicationTokenRefreshOptions = CommunicationTokenRefreshOptions(tokenRefresher, true)
        val communicationTokenCredential = CommunicationTokenCredential(communicationTokenRefreshOptions)

        chatAdapter = ChatAdapterBuilder().build()
        chatAdapter?.connect(
            context,
            endpoint,
            threadId,
            communicationTokenCredential,
            acsIdentity,
            userName
        )?.get()
    }

    private fun urlIsValid(url: String) = url.isNotBlank() && URLUtil.isValidUrl(url.trim())

    fun closeChatComposite() {
        chatAdapter?.disconnect()
        chatAdapter = null
    }
}
