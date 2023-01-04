// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.repository.storage

import com.azure.android.communication.ui.chat.models.EMPTY_MESSAGE_INFO_MODEL
import com.azure.android.communication.ui.chat.models.MessageInfoModel
import com.azure.android.communication.ui.chat.repository.MessageRepository
import java.util.Collections

internal class MessageRepositoryPagedImpl : MessageRepository() {
    // !! This should be the same as the server page size request
    val pageSize = 50;

    val pages : MutableList<List<MessageInfoModel>> = mutableListOf()

    override fun addPage(page: List<MessageInfoModel>) {
        pages.add(0, page);
    }

    override fun addMessage(message: MessageInfoModel) {
        // If no pages, add a page
        if (pages.size == 0) {
            pages.add(listOf(message))
            return
        }


        val lastPage = pages.last()

        // Create a new page
        if (lastPage.size > pageSize) {
            pages.add(listOf(message))
            return
        } else {
            val replacedPage = mutableListOf<MessageInfoModel>()
            replacedPage.addAll(lastPage)
            replacedPage.add(message)
            pages.removeLast()
            pages.add(replacedPage)
        }
    }

    override fun buildSnapshotList(): List<MessageInfoModel> {
        return super.buildSnapshotList()
    }

    override fun removeMessage(message: MessageInfoModel) {
        throw RuntimeException("We can not remove messages from this repo")
    }

    override fun get(i: Int): MessageInfoModel {
        throw RuntimeException("We can not get yet")
    }

    override val size: Int
        get() = TODO("Not yet implemented")
}


