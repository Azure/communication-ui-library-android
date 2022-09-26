// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.service.sdk

import android.content.Context
import com.azure.android.communication.chat.ChatClientBuilder
import com.azure.android.communication.chat.ChatThreadClientBuilder
import com.azure.android.communication.chat.models.SendChatMessageOptions
import com.azure.android.communication.common.CommunicationTokenCredential
import com.azure.android.communication.ui.chat.configuration.ChatConfiguration
import com.azure.android.communication.ui.chat.service.sdk.wrapper.ChatMessageType
import com.azure.android.communication.ui.chat.service.sdk.wrapper.SendChatMessageResult
import com.azure.android.communication.ui.chat.service.sdk.wrapper.into
import com.azure.android.core.http.policy.UserAgentPolicy
import com.azure.android.core.util.RequestContext
import java9.util.concurrent.CompletableFuture

internal class ChatSDKWrapper(
    val context: Context,
    chatConfig: ChatConfiguration,
) : ChatSDK {

    private val chatAsyncClient by lazy {
        ChatClientBuilder()
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

    private val chatThreadAsyncClient by lazy {
        ChatThreadClientBuilder()
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

    private val endPointURL = chatConfig.endPointURL
    private val credential: CommunicationTokenCredential = chatConfig.credential
    private val applicationID = chatConfig.applicationID
    private val sdkName = chatConfig.sdkName
    private val sdkVersion = chatConfig.sdkVersion
    private val threadId = chatConfig.threadId
    private val senderDisplayName = chatConfig.senderDisplayName

    override fun init() = chatAsyncClient.startRealtimeNotifications(context.applicationContext) { /* Error Handler */ }
    override fun dispose() = chatAsyncClient.stopRealtimeNotifications()

    override fun sendMessage(
        type: ChatMessageType,
        content: String,
    ): CompletableFuture<SendChatMessageResult> {
        val future = CompletableFuture<SendChatMessageResult>()
        val chatMessageOptions = SendChatMessageOptions()
            .setType(type.into())
            .setContent(content)
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
}
