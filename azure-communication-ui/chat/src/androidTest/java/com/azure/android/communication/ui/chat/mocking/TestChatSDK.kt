// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.mocking

import com.azure.android.communication.ui.chat.models.ChatEventModel
import com.azure.android.communication.ui.chat.models.MessageInfoModel
import com.azure.android.communication.ui.chat.models.MessagesPageModel
import com.azure.android.communication.ui.chat.redux.state.ChatStatus
import com.azure.android.communication.ui.chat.service.sdk.ChatSDK
import com.azure.android.communication.ui.chat.service.sdk.wrapper.CommunicationIdentifier
import com.azure.android.communication.ui.chat.service.sdk.wrapper.SendChatMessageResult
import com.azure.android.communication.ui.chat.utilities.CoroutineContextProvider
import java9.util.concurrent.CompletableFuture
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.threeten.bp.OffsetDateTime

internal class TestChatSDK(
    coroutineContextProvider: CoroutineContextProvider = CoroutineContextProvider(),
) : ChatSDK {
    private val coroutineScope = CoroutineScope(coroutineContextProvider.Default)

    private var chatEventSharedFlow = MutableSharedFlow<ChatEventModel>()
    private var chatStatusStateFlow: MutableStateFlow<ChatStatus> =
        MutableStateFlow(ChatStatus.NONE)
    private val messagesSharedFlow: MutableSharedFlow<MessagesPageModel> = MutableSharedFlow()
    private val chatEventModelSharedFlow: MutableSharedFlow<ChatEventModel> = MutableSharedFlow()

    fun setChatStatus(status: ChatStatus) {
        chatStatusStateFlow.value = status
    }

    override fun initialization(): CompletableFuture<Void> {
        val future = CompletableFuture<Void>()
        // coroutine to make sure requests are not blocking
        coroutineScope.launch {
            future.complete(null)
        }
        return future
    }

    override fun destroy() {}

    override fun getAdminUserId(): String {
        return ""
    }

    override fun requestPreviousPage() {
    }

    override fun requestChatParticipants() {
    }

    override fun startEventNotifications() {
    }

    override fun stopEventNotifications() {
    }

    override fun getChatStatusStateFlow(): StateFlow<ChatStatus> = chatStatusStateFlow

    override fun getMessagesPageSharedFlow(): SharedFlow<MessagesPageModel> = messagesSharedFlow

    override fun getChatEventSharedFlow(): SharedFlow<ChatEventModel> = chatEventSharedFlow

    override fun sendMessage(messageInfoModel: MessageInfoModel): CompletableFuture<SendChatMessageResult> {
        val future = CompletableFuture<SendChatMessageResult>()
        // coroutine to make sure requests are not blocking
        coroutineScope.launch {
            future.complete(null)
        }
        return future
    }

    override fun deleteMessage(id: String): CompletableFuture<Void> {
        val future = CompletableFuture<Void>()
        // coroutine to make sure requests are not blocking
        coroutineScope.launch {
            future.complete(null)
        }
        return future
    }

    override fun editMessage(
        id: String,
        content: String,
    ): CompletableFuture<Void> {
        val future = CompletableFuture<Void>()
        // coroutine to make sure requests are not blocking
        coroutineScope.launch {
            future.complete(null)
        }
        return future
    }

    override fun sendTypingIndicator(): CompletableFuture<Void> {
        val future = CompletableFuture<Void>()
        // coroutine to make sure requests are not blocking
        coroutineScope.launch {
            future.complete(null)
        }
        return future
    }

    override fun sendReadReceipt(id: String): CompletableFuture<Void> {
        val future = CompletableFuture<Void>()
        // coroutine to make sure requests are not blocking
        coroutineScope.launch {
            future.complete(null)
        }
        return future
    }

    override fun removeParticipant(communicationIdentifier: CommunicationIdentifier): CompletableFuture<Void> {
        val future = CompletableFuture<Void>()
        // coroutine to make sure requests are not blocking
        coroutineScope.launch {
            future.complete(null)
        }
        return future
    }

    override fun fetchMessages(from: OffsetDateTime?) {}

    private fun onChatEventReceived(infoModel: ChatEventModel) {
        coroutineScope.launch {
            chatEventModelSharedFlow.emit(infoModel)
        }
    }
}

internal fun <T> completedFuture(f: () -> T): CompletableFuture<T> {
    return CompletableFuture<T>().also { it.complete(f.invoke()) }
}

internal fun <T> completedFuture(res: T): CompletableFuture<T> {
    return CompletableFuture<T>().also { it.complete(res) }
}

internal fun completedNullFuture(): CompletableFuture<Void> {
    return CompletableFuture<Void>().also { it.complete(null) }
}

internal fun completedNullFuture(
    coroutineScope: CoroutineScope,
    f: suspend () -> Any,
): CompletableFuture<Void> {
    val future = CompletableFuture<Void>()
    coroutineScope.launch {
        f.invoke()
        future.complete(null)
    }
    return future
}
