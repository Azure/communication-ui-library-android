// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.models

import com.azure.android.communication.ui.chat.service.sdk.wrapper.ChatMessageType

internal data class MessageInfoModel(
    val id: String?,
    val internalId: String?,
    val messageType: ChatMessageType,
    val content: String?
)
