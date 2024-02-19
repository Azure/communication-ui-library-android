// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.service.sdk

import android.content.Context
import com.azure.android.communication.chat.ChatClient
import com.azure.android.communication.chat.ChatClientBuilder
import com.azure.android.communication.chat.ChatThreadClient
import com.azure.android.communication.chat.ChatThreadClientBuilder
import com.azure.android.communication.chat.models.ListChatMessagesOptions
import com.azure.android.communication.chat.models.SendChatMessageOptions
import com.azure.android.communication.chat.models.UpdateChatMessageOptions
import com.azure.android.communication.common.CommunicationTokenCredential
import com.azure.android.communication.ui.chat.configuration.ChatConfiguration
import com.azure.android.communication.ui.chat.logger.Logger
import com.azure.android.communication.ui.chat.models.ChatEventModel
import com.azure.android.communication.ui.chat.models.ChatThreadInfoModel
import com.azure.android.communication.ui.chat.models.MessageInfoModel
import com.azure.android.communication.ui.chat.models.MessagesPageModel
import com.azure.android.communication.ui.chat.models.RemoteParticipantInfoModel
import com.azure.android.communication.ui.chat.models.RemoteParticipantsInfoModel
import com.azure.android.communication.ui.chat.models.into
import com.azure.android.communication.ui.chat.redux.state.ChatStatus
import com.azure.android.communication.ui.chat.service.sdk.wrapper.ChatEventType
import com.azure.android.communication.ui.chat.service.sdk.wrapper.CommunicationIdentifier
import com.azure.android.communication.ui.chat.service.sdk.wrapper.SendChatMessageResult
import com.azure.android.communication.ui.chat.service.sdk.wrapper.into
import com.azure.android.communication.ui.chat.utilities.CoroutineContextProvider
import com.azure.android.core.http.policy.UserAgentPolicy
import com.azure.android.core.util.RequestContext
import java9.util.concurrent.CompletableFuture
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.threeten.bp.OffsetDateTime
import java.util.concurrent.Executors

internal class ChatSDKWrapper(
    private val context: Context,
    private val chatConfig: ChatConfiguration,
    coroutineContextProvider: CoroutineContextProvider,
    private val chatEventHandler: ChatEventHandler,
    private val chatFetchNotificationHandler: ChatFetchNotificationHandler,
    private val logger: Logger,
) : ChatSDK {
    companion object {
        const val PAGE_MESSAGES_SIZE = 50
        private const val RESPONSE_SUCCESS_CODE = 200
    }

    private val coroutineScope = CoroutineScope((coroutineContextProvider.Default))
    private val singleThreadedContext = Executors.newSingleThreadExecutor()

    private lateinit var threadClient: ChatThreadClient
    private lateinit var chatClient: ChatClient

    private val endPointURL = chatConfig.endpoint
    private val credential: CommunicationTokenCredential = chatConfig.credential
    private val applicationID = chatConfig.applicationID
    private val sdkName = chatConfig.sdkName
    private val sdkVersion = chatConfig.sdkVersion
    private val threadId = chatConfig.threadId
    private val senderDisplayName = chatConfig.senderDisplayName
    private val localParticipantIdentifier = chatConfig.identity
    private var startedEventNotifications = false

    private val options = ListChatMessagesOptions().apply { maxPageSize = PAGE_MESSAGES_SIZE }
    private var pagingContinuationToken: String? = null
    private var adminUserId: String = ""

    @Volatile
    private var allPagesFetched: Boolean = false

    private val chatStatusStateFlow: MutableStateFlow<ChatStatus> =
        MutableStateFlow(ChatStatus.NONE)
    private val messagesSharedFlow: MutableSharedFlow<MessagesPageModel> =
        MutableSharedFlow()
    private val chatEventModelSharedFlow: MutableSharedFlow<ChatEventModel> =
        MutableSharedFlow()

    override fun getChatStatusStateFlow(): StateFlow<ChatStatus> = chatStatusStateFlow

    override fun getMessagesPageSharedFlow(): SharedFlow<MessagesPageModel> = messagesSharedFlow

    override fun getChatEventSharedFlow(): SharedFlow<ChatEventModel> = chatEventModelSharedFlow

    override fun initialization(): CompletableFuture<Void> {
        val future = CompletableFuture<Void>()
        try {
            chatStatusStateFlow.value = ChatStatus.INITIALIZATION
            createChatClient()
            createChatThreadClient()
            // TODO: initialize polling or try to get first message here to make sure SDK can establish connection with thread
            // TODO: above will make sure, network is connected as well
            onChatEventReceived(
                infoModel =
                    ChatEventModel(
                        eventType = ChatEventType.CHAT_THREAD_PROPERTIES_UPDATED,
                        ChatThreadInfoModel(
                            topic = threadClient.properties.topic,
                            receivedOn = threadClient.properties.createdOn,
                        ),
                        eventReceivedOffsetDateTime = null,
                    ),
            )

            adminUserId = threadClient.properties.createdByCommunicationIdentifier.into().id
            chatStatusStateFlow.value = ChatStatus.INITIALIZED
            future.complete(null)
        } catch (ex: Exception) {
            future.completeExceptionally(ex)
            logger.debug("sendMessage failed.", ex)
        }
        return future
    }

    override fun destroy() {
        chatEventHandler.stop(chatClient)
        stopEventNotifications()
        singleThreadedContext.shutdown()
        coroutineScope.cancel()
        chatFetchNotificationHandler.stop()
    }

    override fun getAdminUserId(): String {
        return adminUserId
    }

    override fun requestPreviousPage() {
        // coroutine to make sure requests are not blocking
        coroutineScope.launch {
            withContext(singleThreadedContext.asCoroutineDispatcher()) {
                var messages: List<MessageInfoModel>? = null
                var throwable: Throwable? = null
                if (!allPagesFetched) {
                    try {
                        val pagedIterable = threadClient.listMessages(options, RequestContext.NONE)
                        val pagedResponse = pagedIterable.byPage(pagingContinuationToken)

                        val response = pagedResponse.iterator().next()
                        response?.apply {
                            if (continuationToken == null) {
                                allPagesFetched = true
                            }
                            pagingContinuationToken = continuationToken
                            messages = elements.map { it.into(chatConfig.identity) }
                        }
                    } catch (ex: Exception) {
                        throwable = ex
                    }
                }
                coroutineScope.launch {
                    messagesSharedFlow.emit(
                        MessagesPageModel(
                            messages = messages,
                            throwable = throwable,
                            allPagesFetched = allPagesFetched,
                        ),
                    )
                }
            }
        }
    }

    override fun sendMessage(messageInfoModel: MessageInfoModel): CompletableFuture<SendChatMessageResult> {
        val future = CompletableFuture<SendChatMessageResult>()
        // coroutine to make sure requests are not blocking
        coroutineScope.launch {
            val chatMessageOptions =
                SendChatMessageOptions()
                    .setType(messageInfoModel.messageType!!.into())
                    .setContent(messageInfoModel.content)
                    .setSenderDisplayName(senderDisplayName)

            try {
                val response =
                    threadClient.sendMessageWithResponse(
                        chatMessageOptions,
                        RequestContext.NONE,
                    )
                future.complete(response.value.into())
            } catch (ex: Exception) {
                future.completeExceptionally(ex)
                logger.debug("sendMessage failed.", ex)
            }
        }
        return future
    }

    override fun requestChatParticipants() {
        coroutineScope.launch {
            try {
                val participants: MutableList<RemoteParticipantInfoModel> = mutableListOf()
                threadClient.listParticipants().byPage().forEach { page ->
                    page.elements.map {
                        participants.add(
                            RemoteParticipantInfoModel(
                                userIdentifier = it.communicationIdentifier.into(),
                                displayName = it.displayName,
                                isLocalUser = it.communicationIdentifier.into().id == localParticipantIdentifier,
                            ),
                        )
                    }
                }
                onChatEventReceived(
                    infoModel =
                        ChatEventModel(
                            eventType = ChatEventType.PARTICIPANTS_ADDED,
                            infoModel =
                                RemoteParticipantsInfoModel(
                                    participants = participants,
                                ),
                            eventReceivedOffsetDateTime = null,
                        ),
                )
            } catch (ex: Exception) {
                throw ex
            }
        }
    }

    override fun sendTypingIndicator(): CompletableFuture<Void> {
        val future = CompletableFuture<Void>()
        // coroutine to make sure requests are not blocking
        coroutineScope.launch {
            try {
                val response = threadClient.sendTypingNotificationWithResponse(RequestContext.NONE)
                if (response.statusCode == RESPONSE_SUCCESS_CODE) {
                    future.complete(null)
                } else {
                    // TODO: in future create exception if required
                    future.completeExceptionally(null)
                }
            } catch (ex: Exception) {
                future.completeExceptionally(ex)
            }
        }
        return future
    }

    override fun sendReadReceipt(id: String): CompletableFuture<Void> {
        val future = CompletableFuture<Void>()
        // coroutine to make sure requests are not blocking
        coroutineScope.launch {
            try {
                val response = threadClient.sendReadReceiptWithResponse(id, RequestContext.NONE)
                if (response.statusCode == RESPONSE_SUCCESS_CODE) {
                    future.complete(null)
                } else {
                    // TODO: in future create exception if required
                    future.completeExceptionally(null)
                }
            } catch (ex: Exception) {
                future.completeExceptionally(ex)
            }
        }
        return future
    }

    override fun editMessage(
        id: String,
        content: String,
    ): CompletableFuture<Void> {
        val future = CompletableFuture<Void>()
        coroutineScope.launch {
            try {
                val options = UpdateChatMessageOptions()
                options.content = content
                val response = threadClient.updateMessageWithResponse(id, options, RequestContext.NONE)
                if (response.statusCode == RESPONSE_SUCCESS_CODE) {
                    future.complete(null)
                } else {
                    // TODO: in future create exception if required
                    future.completeExceptionally(null)
                }
            } catch (ex: Exception) {
                future.completeExceptionally(ex)
            }
        }
        return future
    }

    override fun deleteMessage(id: String): CompletableFuture<Void> {
        val future = CompletableFuture<Void>()
        // coroutine to make sure requests are not blocking
        coroutineScope.launch {
            val response = threadClient.deleteMessageWithResponse(id, RequestContext.NONE)
            if (response.statusCode == RESPONSE_SUCCESS_CODE) {
                future.complete(null)
            } else {
                // TODO: in future create exception if required
                future.completeExceptionally(null)
            }
        }
        return future
    }

    override fun removeParticipant(communicationIdentifier: CommunicationIdentifier): CompletableFuture<Void> {
        val future = CompletableFuture<Void>()
        // coroutine to make sure requests are not blocking
        coroutineScope.launch {
            val response =
                threadClient.removeParticipantWithResponse(
                    communicationIdentifier.into(),
                    RequestContext.NONE,
                )
            if (response.statusCode == RESPONSE_SUCCESS_CODE) {
                future.complete(null)
            } else {
                // TODO: in future create exception if required
                future.completeExceptionally(null)
            }
        }
        return future
    }

    override fun fetchMessages(from: OffsetDateTime?) {
        chatFetchNotificationHandler.fetchMessages(from)
    }

    override fun startEventNotifications() {
        if (startedEventNotifications) return
        startedEventNotifications = true
        chatClient.startRealtimeNotifications(context) {
            throw it
        }
        chatEventHandler.start(
            chatClient = chatClient,
            threadID = threadId,
            localParticipantIdentifier = localParticipantIdentifier,
            eventSubscriber = this::onChatEventReceived,
        )
        chatFetchNotificationHandler.start(
            chatThreadClient = threadClient,
            eventSubscriber = this::onChatEventReceived,
        )
    }

    override fun stopEventNotifications() {
        if (startedEventNotifications) {
            chatClient.stopRealtimeNotifications()
            startedEventNotifications = false
            chatEventHandler.stop(chatClient)
        }
    }

    private fun createChatClient() {
        chatClient =
            ChatClientBuilder()
                .endpoint(endPointURL)
                .credential(credential)
                .addPolicy(
                    UserAgentPolicy(
                        applicationID,
                        sdkName,
                        sdkVersion,
                    ),
                )
                .buildClient()
    }

    private fun createChatThreadClient() {
        threadClient =
            ChatThreadClientBuilder()
                .endpoint(endPointURL)
                .credential(credential)
                .addPolicy(
                    UserAgentPolicy(
                        applicationID,
                        sdkName,
                        sdkVersion,
                    ),
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
