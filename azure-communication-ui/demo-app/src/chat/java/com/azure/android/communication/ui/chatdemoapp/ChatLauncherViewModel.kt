// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chatdemoapp

import android.webkit.URLUtil
import androidx.lifecycle.ViewModel
import com.azure.android.communication.ui.chat.ChatComposite
import com.azure.android.communication.ui.chat.ChatCompositeBuilder
import com.azure.android.communication.ui.demoapp.UrlTokenFetcher
import java.util.concurrent.Callable

class ChatLauncherViewModel : ViewModel() {
    private var token: String? = null

    var isTokenFunctionOptionSelected = false; private set

    var chatComposite: ChatComposite? = null
    private val lock = Object()

//    fun getChatComposite(): ChatComposite {
//        synchronized(lock) {
//            if (chatComposite == null)
//                chatComposite = ChatCompositeBuilder().build()
//            return chatComposite!!
//        }
//    }

    fun getTokenFetcher(tokenFunctionURL: String, acsToken: String): Callable<String> {
        val tokenRefresher = when {
            isTokenFunctionOptionSelected && urlIsValid(tokenFunctionURL) -> {
                token = null
                UrlTokenFetcher(tokenFunctionURL)
            }
            acsToken.isNotBlank() -> {
                token = acsToken
                CachedTokenFetcher(acsToken)
            }
            else -> {
                throw IllegalStateException("Invalid Token function URL or acs Token")
            }
        }
        return tokenRefresher
    }

    private fun urlIsValid(url: String) = url.isNotBlank() && URLUtil.isValidUrl(url.trim())

    fun useTokenFunction() {
        isTokenFunctionOptionSelected = true
    }

    fun useAcsToken() {
        isTokenFunctionOptionSelected = false
    }

    fun closeChatComposite() {
        chatComposite = null
    }

    var isChatRunning: Boolean = false
}
