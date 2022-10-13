// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.service.sdk.wrapper

internal data class SendReadReceiptResult(val id: String)

internal fun SendReadReceiptResult.into(): com.azure.android.communication.chat.models.ChatMessageReadReceipt {
    return com.azure.android.communication.chat.models.ChatMessageReadReceipt().setChatMessageId(this.id)
}

internal fun com.azure.android.communication.chat.models.ChatMessageReadReceipt.into(): SendReadReceiptResult {
    return SendReadReceiptResult(this.chatMessageId)
}
