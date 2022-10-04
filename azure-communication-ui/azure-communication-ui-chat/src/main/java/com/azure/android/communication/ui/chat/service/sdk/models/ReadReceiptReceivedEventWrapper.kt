// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.service.sdk.models

import org.threeten.bp.OffsetDateTime

internal interface ReadReceiptReceivedEvent {
    val chatMessageId: String
    val readOn: OffsetDateTime
}

internal class ReadReceiptReceivedEventWrapper(private val readReceiptReceivedEvent: com.azure.android.communication.chat.models.ReadReceiptReceivedEvent) :
    ReadReceiptReceivedEvent {
    override val chatMessageId: String = readReceiptReceivedEvent.chatMessageId
    override val readOn: OffsetDateTime = readReceiptReceivedEvent.readOn
}
