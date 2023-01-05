// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.repository.storage

import com.azure.android.communication.ui.chat.models.EMPTY_MESSAGE_INFO_MODEL
import com.azure.android.communication.ui.chat.models.MessageInfoModel
import com.azure.android.communication.ui.chat.repository.MessageRepository
import com.azure.android.communication.ui.chat.utilities.PagedList
import java.util.Collections

internal class MessageRepositoryPagedImpl : MessageRepository() {
    // !! This should be the same as the server page size request
    val pageSize = 50;
    val pages : MutableList<List<MessageInfoModel>> = mutableListOf()

    override fun replaceMessage(oldMessage: MessageInfoModel, newMessage: MessageInfoModel) {
        // Find the message
        // Create a new page
        // Move all items over, with replaced item
        val internalView = PagedList(pages);
        val page = internalView.pageOf(oldMessage);
        if (page != -1) {
            val updatedPage = pages[page].toMutableList()
            updatedPage[updatedPage.indexOf(oldMessage)] = newMessage;
            pages[page] = updatedPage;
        }
    }

    override fun addPage(page: List<MessageInfoModel>) {
        pages.add(0, page);
    }

    override fun addMessage(message: MessageInfoModel) {
        // If no pages, add a page
        if (pages.size == 0) {
            pages.add(0, listOf(message))
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
        return PagedList(pages.toList());
    }

    override fun removeMessage(message: MessageInfoModel) {
        // Find the message
        // Create a new page
        // Move all items over, with replaced item
        val internalView = PagedList(pages);
        val page = internalView.pageOf(message);
        if (page != -1) {
            val updatedPage = pages[page].filter { it.normalizedID != message.normalizedID }
            pages[page] = updatedPage;
        }
    }

    override fun get(i: Int): MessageInfoModel {
        return PagedList(pages)[i]
    }

    override val size: Int
        get() = PagedList(pages).size
}


