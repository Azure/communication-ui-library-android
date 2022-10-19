// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.mocking

import com.azure.android.communication.chat.ChatClient
import com.azure.android.communication.chat.ChatClientBuilder
import com.azure.android.communication.chat.ChatThreadClient
import com.azure.android.communication.chat.ChatThreadClientBuilder
import com.azure.android.communication.chat.models.ChatEvent
import com.azure.android.communication.chat.models.SendChatMessageOptions
import com.azure.android.communication.common.CommunicationTokenCredential
import com.azure.android.communication.ui.chat.configuration.ChatConfiguration
import com.azure.android.communication.ui.chat.models.ChatEventModel
import com.azure.android.communication.ui.chat.models.ChatThreadInfoModel
import com.azure.android.communication.ui.chat.models.MessageInfoModel
import com.azure.android.communication.ui.chat.models.MessagesPageModel
import com.azure.android.communication.ui.chat.redux.state.ChatStatus
import com.azure.android.communication.ui.chat.service.sdk.ChatSDK
import com.azure.android.communication.ui.chat.service.sdk.wrapper.ChatEventType
import com.azure.android.communication.ui.chat.service.sdk.wrapper.CommunicationIdentifier
import com.azure.android.communication.ui.chat.service.sdk.wrapper.SendChatMessageResult
import com.azure.android.communication.ui.chat.service.sdk.wrapper.into
import com.azure.android.communication.ui.chat.utilities.CoroutineContextProvider
import com.azure.android.core.http.policy.UserAgentPolicy
import com.azure.android.core.util.RequestContext
import java9.util.concurrent.CompletableFuture
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.threeten.bp.OffsetDateTime

internal class TestChatSDK(
    chatConfig: ChatConfiguration,
    coroutineContextProvider: CoroutineContextProvider = CoroutineContextProvider(),
) : ChatSDK {
    private val coroutineScope = CoroutineScope(coroutineContextProvider.Default)
    private val endPointURL = chatConfig.endPointURL
    private val credential: CommunicationTokenCredential = chatConfig.credential
    private val applicationID = chatConfig.applicationID
    private val sdkName = chatConfig.sdkName
    private val sdkVersion = chatConfig.sdkVersion
    private val threadId = chatConfig.threadId
    private val senderDisplayName = chatConfig.senderDisplayName
    
    private lateinit var chatClient: ChatClient
    private lateinit var threadClient: ChatThreadClient
    private var chatEventSharedFlow = MutableSharedFlow<ChatEventModel>()
    private var chatStatusStateFlow: MutableStateFlow<ChatStatus> =
        MutableStateFlow(ChatStatus.NONE)
    private val messagesSharedFlow: MutableSharedFlow<MessagesPageModel> = MutableSharedFlow()
    private val chatEventModelSharedFlow: MutableSharedFlow<ChatEventModel> = MutableSharedFlow()
    
    override fun initialization() {
        chatStatusStateFlow.value = ChatStatus.INITIALIZATION
        createChatClient()
        createChatThreadClient()
        onChatEventReceived(
            infoModel = ChatEventModel(
                eventType = ChatEventType.CHAT_THREAD_PROPERTIES_UPDATED,
                ChatThreadInfoModel(
                    topic = threadClient.properties.topic,
                    receivedOn = threadClient.properties.createdOn
                ),
                eventReceivedOffsetDateTime = null
            )
        )
    
        chatStatusStateFlow.value = ChatStatus.INITIALIZED
    }
    
    override fun destroy() {}
    
    override fun requestPreviousPage() {
        TODO("Not yet implemented")
    }
    
    override fun requestChatParticipants() {
        TODO("Not yet implemented")
    }
    
    override fun startEventNotifications() {
        TODO("Not yet implemented")
    }
    
    override fun stopEventNotifications() {
        TODO("Not yet implemented")
    }
    
    override fun getChatStatusStateFlow(): StateFlow<ChatStatus> = chatStatusStateFlow
    
    override fun getMessagesPageSharedFlow(): SharedFlow<MessagesPageModel> = messagesSharedFlow
    override fun getChatEventSharedFlow(): SharedFlow<ChatEventModel> = chatEventSharedFlow
    override fun sendMessage(messageInfoModel: MessageInfoModel): CompletableFuture<SendChatMessageResult> {
        val future = CompletableFuture<SendChatMessageResult>()
        // coroutine to make sure requests are not blocking
        coroutineScope.launch {
            val chatMessageOptions = SendChatMessageOptions()
                .setType(messageInfoModel.messageType!!.into())
                .setContent(messageInfoModel.content)
                .setSenderDisplayName(senderDisplayName)
        
            try {
                val response = threadClient.sendMessageWithResponse(
                    chatMessageOptions,
                    RequestContext.NONE
                )
                future.complete(response.value.into())
            } catch (ex: Exception) {
                future.completeExceptionally(ex)
            }
        }
        return future
    }
    
    override fun deleteMessage(id: String): CompletableFuture<Void> {
        TODO("Not yet implemented")
    }
    
    override fun editMessage(id: String, content: String): CompletableFuture<Void> {
        TODO("Not yet implemented")
    }
    
    override fun sendTypingIndicator(): CompletableFuture<Void> {
        TODO("Not yet implemented")
    }
    
    override fun sendReadReceipt(id: String): CompletableFuture<Void> {
        TODO("Not yet implemented")
    }
    
    override fun removeParticipant(communicationIdentifier: CommunicationIdentifier): CompletableFuture<Void> {
        TODO("Not yet implemented")
    }
    
    override fun fetchMessages(from: OffsetDateTime?) {}
    
    private fun createChatClient() {
        chatClient = ChatClientBuilder()
            .endpoint(endPointURL)
            .credential(credential)
            .addPolicy(
                UserAgentPolicy(
                    applicationID,
                    sdkName,
                    sdkVersion
                )
            )
            .buildClient()
    }
    
    private fun createChatThreadClient() {
        threadClient = ChatThreadClientBuilder()
            .endpoint(endPointURL)
            .credential(credential)
            .addPolicy(
                UserAgentPolicy(
                    applicationID,
                    sdkName,
                    sdkVersion
                )
            )
            .chatThreadId(threadId)
            .buildClient()
    }
    
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