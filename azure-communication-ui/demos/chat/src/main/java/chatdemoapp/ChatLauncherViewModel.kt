// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package chatdemoapp

import android.content.Context
import androidx.lifecycle.ViewModel
import com.azure.android.communication.common.CommunicationTokenCredential
import com.azure.android.communication.common.CommunicationTokenRefreshOptions
import com.azure.android.communication.common.CommunicationUserIdentifier
import com.azure.android.communication.ui.chat.ChatAdapter
import com.azure.android.communication.ui.chat.ChatAdapterBuilder
import com.azure.android.communication.ui.chat.ChatCompositeEventHandler
import com.azure.android.communication.ui.chat.models.ChatCompositeErrorEvent
import java.util.concurrent.Callable

class ChatLauncherViewModel : ViewModel() {
    private var token: String? = null

    var chatAdapter: ChatAdapter? = null

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
        errorHandler: ChatCompositeEventHandler<ChatCompositeErrorEvent>,
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

        val chatAdapter = ChatAdapterBuilder()
            .endpoint(endpoint)
            .credential(communicationTokenCredential)
            .identity(CommunicationUserIdentifier(acsIdentity))
            .displayName(userName)
            .threadId(threadId)
            .build()

        chatAdapter.addOnErrorEventHandler(errorHandler)

        chatAdapter.connect(context)

        this.chatAdapter = chatAdapter
    }

    fun closeChatComposite() {
        chatAdapter = null
    }
}
