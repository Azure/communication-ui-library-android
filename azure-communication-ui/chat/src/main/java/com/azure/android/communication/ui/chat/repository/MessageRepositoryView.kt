// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.repository

import com.azure.android.communication.ui.chat.models.MessageInfoModel

internal interface MessageRepositoryView {
    fun size(): Int
    fun indexOf(element: MessageInfoModel): Int
    fun get(index: Int): MessageInfoModel
    fun isEmpty(): Boolean
    fun contains(element: MessageInfoModel): Boolean
    fun containsAll(elements: Collection<MessageInfoModel>): Boolean
    fun iterator(): Iterator<MessageInfoModel>
    fun lastIndexOf(element: MessageInfoModel): Int
    fun listIterator(): Iterator<MessageInfoModel>
    fun listIterator(index: Int): Iterator<MessageInfoModel>
    fun subList(fromIndex: Int, toIndex: Int): List<MessageInfoModel>
}

internal interface MessageStorage : MessageRepositoryMiddleware, MessageRepositoryView
