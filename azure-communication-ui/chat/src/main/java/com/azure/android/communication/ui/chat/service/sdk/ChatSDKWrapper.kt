// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.service.sdk

import android.content.Context
import com.azure.android.communication.chat.ChatAsyncClient
import com.azure.android.communication.chat.ChatClientBuilder
import com.azure.android.communication.chat.ChatThreadAsyncClient
import com.azure.android.communication.chat.ChatThreadClientBuilder
import com.azure.android.communication.chat.models.ListChatMessagesOptions
import com.azure.android.communication.chat.models.SendChatMessageOptions
import com.azure.android.communication.common.CommunicationTokenCredential
import com.azure.android.communication.ui.chat.configuration.ChatConfiguration
import com.azure.android.communication.ui.chat.models.MessageInfoModel
import com.azure.android.communication.ui.chat.models.MessagesPageModel
import com.azure.android.communication.ui.chat.models.into
import com.azure.android.communication.ui.chat.redux.state.ChatStatus
import com.azure.android.communication.ui.chat.service.sdk.wrapper.SendChatMessageResult
import com.azure.android.communication.ui.chat.service.sdk.wrapper.into
import com.azure.android.core.http.policy.UserAgentPolicy
import com.azure.android.core.util.RequestContext
import java9.util.concurrent.CompletableFuture
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

internal class ChatSDKWrapper(
    context: Context,
    chatConfig: ChatConfiguration,
) : ChatSDK {

    companion object {
        private const val PAGE_SIZE = 50
    }

    private lateinit var chatAsyncClient: ChatAsyncClient
    private lateinit var chatThreadAsyncClient: ChatThreadAsyncClient

    private val endPointURL = chatConfig.endPointURL
    private val credential: CommunicationTokenCredential = chatConfig.credential
    private val applicationID = chatConfig.applicationID
    private val sdkName = chatConfig.sdkName
    private val sdkVersion = chatConfig.sdkVersion
    private val threadId = chatConfig.threadId
    private val senderDisplayName = chatConfig.senderDisplayName

    private var continuationToken: String? = null

    private val chatStatusStateFlow: MutableStateFlow<ChatStatus> =
        MutableStateFlow(ChatStatus.NONE)

    override fun getChatStatusStateFlow(): StateFlow<ChatStatus> = chatStatusStateFlow

    override fun initialization() {
        chatStatusStateFlow.value = ChatStatus.INITIALIZATION
        createChatAsyncClient()
        createChatThreadAsyncClient()
        // TODO: initialize polling or try to get first message here to make sure SDK can establish connection with thread
        // TODO: above will make sure, network is connected as well
        chatStatusStateFlow.value = ChatStatus.INITIALIZED
    }

    override fun getPreviousPage(): MessagesPageModel {
        var messages: List<MessageInfoModel>? = null
        var throwable: Throwable? = null

        val options = ListChatMessagesOptions()
        options.maxPageSize = PAGE_SIZE

        try {
            val chatMessages = chatThreadAsyncClient.listMessages(options, RequestContext.NONE)
            val chatMessagesStream = chatMessages.byPage(continuationToken)

            chatMessagesStream.forEach { handler ->
                continuationToken = handler.continuationToken
                messages = handler.elements.map { it.into() }
            }.cancel()
        } catch (ex: Exception) {
            throwable = ex
        }

        return MessagesPageModel(
            messages = messages ?: listOf(),
            error = throwable
        )
    }

    override fun sendMessage(
        messageInfoModel: MessageInfoModel,
    ): CompletableFuture<SendChatMessageResult> {
        val future = CompletableFuture<SendChatMessageResult>()
        val chatMessageOptions = SendChatMessageOptions()
            .setType(messageInfoModel.messageType.into())
            .setContent(messageInfoModel.content)
            .setSenderDisplayName(senderDisplayName)

        chatThreadAsyncClient.sendMessageWithResponse(
            chatMessageOptions,
            RequestContext.NONE
        ).whenComplete { response, throwable ->
            if (throwable == null) {
                future.complete(response.value.into())
            } else {
                future.completeExceptionally(throwable)
            }
        }
        return future
    }

    private fun createChatAsyncClient() {
        chatAsyncClient = ChatClientBuilder()
            .endpoint(endPointURL)
            .credential(credential)
            .addPolicy(
                UserAgentPolicy(
                    applicationID,
                    sdkName,
                    sdkVersion
                )
            )
            .buildAsyncClient()
    }

    private fun createChatThreadAsyncClient() {
        chatThreadAsyncClient = ChatThreadClientBuilder()
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
            .buildAsyncClient()
    }
}
