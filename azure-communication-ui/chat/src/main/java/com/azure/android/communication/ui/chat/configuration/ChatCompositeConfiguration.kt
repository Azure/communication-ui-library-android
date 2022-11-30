// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.configuration

import com.azure.android.communication.ui.chat.ChatCompositeEventsHandler


internal class ChatCompositeConfiguration {
    var chatConfig: ChatConfiguration? = null
    var chatCompositeEventsHandler = ChatCompositeEventsHandler()
    var eventHandlerRepository: ChatCompositeEventHandlerRepository =
        ChatCompositeEventHandlerRepository()
}
