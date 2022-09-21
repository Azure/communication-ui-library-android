// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat

import android.content.Context
import com.azure.android.communication.ui.chat.configuration.ChatCompositeConfiguration
import com.azure.android.communication.ui.chat.configuration.ChatConfiguration
import com.azure.android.communication.ui.chat.locator.ServiceLocator
import com.azure.android.communication.ui.chat.models.ChatCompositeLocalOptions
import com.azure.android.communication.ui.chat.models.ChatCompositeRemoteOptions
import com.azure.android.communication.ui.chat.models.ChatCompositeUnreadMessageChangedEvent
import com.azure.android.communication.ui.chat.service.ChatService
import com.azure.android.communication.ui.chat.service.sdk.ChatSDK
import com.azure.android.communication.ui.chat.service.sdk.ChatSDKWrapper

internal class ChatContainer(
    private val configuration: ChatCompositeConfiguration,
) {
    var started = false
    private var locator: ServiceLocator? = null
    private val onUnreadMessageChangedHandlers =
        mutableSetOf<ChatCompositeEventHandler<ChatCompositeUnreadMessageChangedEvent>>()

    fun start(
        context: Context,
        remoteOptions: ChatCompositeRemoteOptions,
        localOptions: ChatCompositeLocalOptions,
        instanceId: Int,
    ) {
        // currently only single instance is supported
        if (!started) {
            started = true
            configuration.chatConfig =
                ChatConfiguration(
                    endPointURL = remoteOptions.locator.endpointURL,
                    credential = remoteOptions.credential,
                    applicationID = "azure_communication_ui_chat", // TODO: modify while working on diagnostics config
                    sdkName = "com.azure.android:azure-communication-chat",
                    sdkVersion = "2.0.0",
                    threadId = remoteOptions.locator.chatThreadId,
                    senderDisplayName = remoteOptions.displayName
                )

            locator = ServiceLocator.getInstance(instanceId = instanceId)
            locator?.let {
                it.addTypedBuilder { localOptions }
                it.addTypedBuilder { remoteOptions }
                it.addTypedBuilder {
                    ChatSDKWrapper(
                        context = context,
                        instanceId = instanceId,
                    )
                }
                it.addTypedBuilder {
                    ChatService(it.locate<ChatSDK>())
                }
            }
        }
    }

    fun addOnViewClosedEventHandler(handler: ChatCompositeEventHandler<Any>) {
    }

    fun removeOnViewClosedEventHandler(handler: ChatCompositeEventHandler<Any>) {
    }

    fun addOnUnreadMessagesChangedEventHandler(handler: ChatCompositeEventHandler<ChatCompositeUnreadMessageChangedEvent>) {
        onUnreadMessageChangedHandlers.add(handler)
    }

    fun removeOnUnreadMessagesChangedEventHandler(handler: ChatCompositeEventHandler<ChatCompositeUnreadMessageChangedEvent>) {
        onUnreadMessageChangedHandlers.remove(handler)
    }

    fun stop() {
        locator?.clear()
    }
}
