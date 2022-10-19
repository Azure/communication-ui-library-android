/*
 * *
 *  * Copyright (c) Microsoft Corporation. All rights reserved.
 *  * Licensed under the MIT License.
 *
 */

package com.azure.android.communication.ui.chat.repository.storage

import com.azure.android.communication.ui.chat.models.MessageInfoModel
import com.azure.android.communication.ui.chat.repository.MessageRepositoryListInterface
import com.azure.android.communication.ui.chat.repository.MessageRepositoryMiddlewareInterface

internal class MessageStorage : MessageRepositoryListInterface(), MessageRepositoryMiddlewareInterface {



    override val size: Int
        get() = TODO("Not yet implemented")

    override fun get(index: Int): MessageInfoModel {
        TODO("Not yet implemented")
    }

    override fun isEmpty(): Boolean {
        TODO("Not yet implemented")
    }

    override fun addLocalMessage(messageInfoModel: MessageInfoModel) {
        TODO("Not yet implemented")

    }

    override fun addPage(page: List<MessageInfoModel>) {
        TODO("Not yet implemented")
    }

    override fun addServerMessage(message: MessageInfoModel) {
        TODO("Not yet implemented")
    }

    override fun removeMessage(message: MessageInfoModel) {
        TODO("Not yet implemented")
    }

    override fun editMessage(message: MessageInfoModel) {
        TODO("Not yet implemented")
    }

    override fun getLastMessage(): MessageInfoModel? {
        TODO("Not yet implemented")
    }
}