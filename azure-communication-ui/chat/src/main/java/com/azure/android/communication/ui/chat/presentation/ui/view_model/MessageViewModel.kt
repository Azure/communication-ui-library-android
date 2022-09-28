package com.azure.android.communication.ui.chat.presentation.ui.view_model

import com.azure.android.communication.ui.chat.models.MessageInfoModel

internal class MessageViewModel(val message: MessageInfoModel)

internal fun List<MessageInfoModel>.toViewModelList() = InfoModelToViewModelAdapter(this) as List<MessageViewModel>

private class InfoModelToViewModelAdapter(private val messages: List<MessageInfoModel>) : List<MessageViewModel> {
    override val size = messages.size
    override fun contains(element: MessageViewModel) = messages.contains(element.message)

    override fun containsAll(elements: Collection<MessageViewModel>) = messages.containsAll(elements.map { it.message })

    override fun get(index: Int) = MessageViewModel(messages[index])

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
