// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.service.sdk.models

internal enum class ChatMessageType {
    TEXT,
    HTML
}

internal fun ChatMessageType.into(): com.azure.android.communication.chat.models.ChatMessageType {
    return when (this) {
        ChatMessageType.TEXT -> com.azure.android.communication.chat.models.ChatMessageType.TEXT
        ChatMessageType.HTML -> com.azure.android.communication.chat.models.ChatMessageType.HTML
        else -> {
            throw IllegalStateException("Unknown type of ChatMessageType: $this")
        }
    }
}

internal fun com.azure.android.communication.chat.models.ChatMessageType.into(): ChatMessageType {
    return when (this) {
        com.azure.android.communication.chat.models.ChatMessageType.TEXT -> ChatMessageType.TEXT
        com.azure.android.communication.chat.models.ChatMessageType.HTML -> ChatMessageType.HTML
        else -> {
            throw IllegalStateException("Unknown type of ChatMessageType: $this")
        }
    }
}
