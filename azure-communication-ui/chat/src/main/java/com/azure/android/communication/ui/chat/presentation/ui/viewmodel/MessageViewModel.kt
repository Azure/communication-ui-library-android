// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.presentation.ui.viewmodel

import com.azure.android.communication.ui.chat.models.MessageInfoModel
import com.azure.android.core.rest.annotation.Immutable

@Immutable
internal class MessageViewModel(
    val message: MessageInfoModel,
    val showUsername: Boolean,
    val showTime: Boolean,
    val showDateHeader: Boolean,
    val isLocalUser: Boolean,
)

internal fun List<MessageInfoModel>.toViewModelList(localUserIdentifier: String) =
    InfoModelToViewModelAdapter(this, localUserIdentifier) as List<MessageViewModel>

private class InfoModelToViewModelAdapter(
    private val messages: List<MessageInfoModel>,
    private val localUserIdentifier: String
) :
    List<MessageViewModel> {

    override fun get(index: Int): MessageViewModel {
        // Generate Message View Model here
        val lastMessage = messages[index - 1]
        val thisMessage = messages[index]
        return MessageViewModel(
            messages[index],
            showUsername =
            (lastMessage.senderCommunicationIdentifier?.id ?: "")
                != (thisMessage.senderCommunicationIdentifier?.id ?: ""),

            showTime =
            (lastMessage.senderCommunicationIdentifier?.id ?: "")
                != (thisMessage.senderCommunicationIdentifier?.id ?: ""),

            showDateHeader = lastMessage.createdOn?.dayOfYear != thisMessage.createdOn?.dayOfYear,
            isLocalUser = thisMessage.senderCommunicationIdentifier?.id == localUserIdentifier,
        )
    }

    // Rest of List Implementation
    override val size = messages.size
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
