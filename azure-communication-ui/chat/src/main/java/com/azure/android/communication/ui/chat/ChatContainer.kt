// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat

import android.content.Context
import com.azure.android.communication.ui.chat.configuration.ChatCompositeConfiguration
import com.azure.android.communication.ui.chat.locator.ServiceLocator
import com.azure.android.communication.ui.chat.models.ChatCompositeLocalOptions
import com.azure.android.communication.ui.chat.models.ChatCompositeRemoteOptions
import com.azure.android.communication.ui.chat.models.ChatCompositeUnreadMessageChangedEvent

internal class ChatContainer(
    private val configuration: ChatCompositeConfiguration,
    private val instanceId: Int,
) {
    var started = false
    private val locator = ServiceLocator.getInstance(instanceId = instanceId)
    private val onUnreadMessageChangedHandlers =
        mutableSetOf<ChatCompositeEventHandler<ChatCompositeUnreadMessageChangedEvent>>()
    
    private fun start(
        context: Context,
        remoteOptions: ChatCompositeRemoteOptions,
        localOptions: ChatCompositeLocalOptions,
    ) {
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
        locator.clear()
    }
}
