package com.azure.android.communication.ui.chat.utilities

import com.azure.android.communication.ui.chat.models.MessageInfoModel

internal fun List<MessageInfoModel>.findMessageIdxById(messageId: String): Int {
    for (i in 0 until size) {
        if (this[i].id == messageId) {
            return i
        }
    }
    throw IllegalArgumentException("Message with id $messageId not found")
}
