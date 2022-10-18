// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.repository

import com.azure.android.communication.ui.chat.models.MessageInfoModel

// Interface for Message Repository Middleware to use
// I.e.
// - addLocalMessage
// - messageRetrieved,
// - pageRetrieved
// - messageEdited
// - messageDeleted
internal interface MessageRepositoryMiddlewareInterface {
    fun addLocalMessage(messageInfoModel: MessageInfoModel)
    fun addPage(page: List<MessageInfoModel>)
    fun addServerMessage(message: MessageInfoModel)
    fun removeMessage(message: MessageInfoModel)
    fun editMessage(message: MessageInfoModel)
    fun getLastMessage(): MessageInfoModel?
}
