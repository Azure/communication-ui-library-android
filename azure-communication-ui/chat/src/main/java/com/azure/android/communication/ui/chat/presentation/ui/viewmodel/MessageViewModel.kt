// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.presentation.ui.viewmodel

import com.azure.android.communication.ui.chat.models.MessageInfoModel
import com.azure.android.communication.ui.chat.repository.MessageRepositoryView

internal class MessageViewModel(val message: MessageInfoModel)

internal fun MessageRepositoryView.toViewModelList() =
    InfoModelToViewModelAdapter(this) as List<MessageViewModel>

private class InfoModelToViewModelAdapter(private val messages: MessageRepositoryView) :
    List<MessageViewModel> {

    override fun get(index: Int): MessageViewModel {
        // Generate Message View Model here
        return MessageViewModel(
            messages.get(index)
        )
    }

    // Rest of List Implementation
    override val size = messages.size()
    override fun contains(element: MessageViewModel) = messages.contains(element.message)

    override fun containsAll(elements: Collection<MessageViewModel>) =
        messages.containsAll(elements.map { it.message })

    override fun indexOf(element: MessageViewModel) = messages.indexOf(element.message)

    override fun isEmpty() = messages.isEmpty()

    override fun iterator(): Iterator<MessageViewModel> {
        // Not Implemented
        TODO("Not Implemented, probably not needed")
    }

    override fun lastIndexOf(element: MessageViewModel) = messages.lastIndexOf(element.message)

    override fun listIterator(): ListIterator<MessageViewModel> {
        // Not Implemented
        TODO("Not Implemented, probably not needed")
    }

    override fun listIterator(index: Int): ListIterator<MessageViewModel> {
        // Not Implemented
        TODO("Not Implemented, probably not needed")
    }

    override fun subList(fromIndex: Int, toIndex: Int): List<MessageViewModel> {
        // Not Implemented
        TODO("Not Implemented, probably not needed")
    }
}
