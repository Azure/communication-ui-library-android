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
internal interface IMessageRepositoryDelegate {
    fun addPage(page: List<MessageInfoModel>)
    fun addMessage(message: MessageInfoModel)
    fun removeMessage(message: MessageInfoModel)
    fun get(i: Int): MessageInfoModel
    val size: Int

    fun replaceMessage(oldMessage: MessageInfoModel, newMessage: MessageInfoModel) {
        removeMessage(oldMessage)
        addMessage(newMessage)
    }

    fun getSnapshotList(): List<MessageInfoModel> {
        // This is a inefficient implementation
        // but is generic and will work with any backing data
        val result = ArrayList<MessageInfoModel>()
        for (i in 0 until size) {
            result.add(get(i))
        }
        return result
    }
}
