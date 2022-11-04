// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.repository

import com.azure.android.communication.ui.chat.models.MessageInfoModel
import java.lang.RuntimeException

internal abstract class MessageRepositoryReader : List<MessageInfoModel> {

    // Note:
    //  While we use List<MessageInfoModel> on the MessageRepository
    //  List methods we do not need will be stubbed out
    //  They are defined here so we do not need them
    //
    //  Methods not defined here from List will pass through to the implementor

    override fun isEmpty(): Boolean {
        return size == 0
    }

    final override fun contains(element: MessageInfoModel): Boolean {
        throw RuntimeException("Not implemented on the Message Repository")
    }

    final override fun containsAll(elements: Collection<MessageInfoModel>): Boolean {
        throw RuntimeException("Not implemented on the Message Repository")
    }

    final override fun indexOf(element: MessageInfoModel): Int {
        throw RuntimeException("Not implemented on the Message Repository")
    }

    final override fun iterator(): Iterator<MessageInfoModel> {
        throw RuntimeException("Not implemented on the Message Repository")
    }

    final override fun lastIndexOf(element: MessageInfoModel): Int {
        throw RuntimeException("Not implemented on the Message Repository")
    }

    final override fun listIterator(): ListIterator<MessageInfoModel> {
        throw RuntimeException("Not implemented on the Message Repository")
    }

    final override fun listIterator(index: Int): ListIterator<MessageInfoModel> {
        throw RuntimeException("Not implemented on the Message Repository")
    }

    final override fun subList(fromIndex: Int, toIndex: Int): List<MessageInfoModel> {
        throw RuntimeException("Not implemented on the Message Repository")
    }

    abstract fun getInternalIndex(messageId: Long): Int
}
