// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.repository

import com.azure.android.communication.ui.chat.models.MessageInfoModel
import java.lang.RuntimeException

internal abstract class MessageRepositoryReader : List<MessageInfoModel> {

    fun getSnapshotList() : List<MessageInfoModel> {
        // This is a inefficient implementation
        // but is generic and will work with any backing data
        val result = ArrayList<MessageInfoModel>()
        for (i in 0 until size) {
            result.add(get(i))
        }
        return result
    }

    override fun isEmpty(): Boolean {
        return size == 0
    }

    final override fun contains(element: MessageInfoModel): Boolean {
        throw RuntimeException("Not implemented on the Message Repository")
    }

    final override fun containsAll(elements: Collection<MessageInfoModel>): Boolean {
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
}
