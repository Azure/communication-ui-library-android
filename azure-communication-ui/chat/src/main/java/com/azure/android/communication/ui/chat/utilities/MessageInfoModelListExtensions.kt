package com.azure.android.communication.ui.chat.utilities

import com.azure.android.communication.ui.chat.models.EMPTY_MESSAGE_INFO_MODEL
import com.azure.android.communication.ui.chat.models.MessageInfoModel
import com.azure.android.communication.ui.chat.repository.MessageRepository

// Returns an index of -1 if item can't be found
internal fun List<MessageInfoModel>.findMessageIdxById(messageId: Long): Int {
    if (messageId == 0L) return -1

    var first = 0
    var last = size - 1
    while (first <= last) {
        var mid = (first + last) / 2

        while (this[mid].normalizedID == null) {
            mid--
            if (mid < first) {
                throw IllegalArgumentException("Message with id $messageId not found")
            }
        }
        val midVal = this[mid].normalizedID!!.toLong()
        if (midVal < messageId.toLong()) {
            first = mid + 1
        } else if (midVal > messageId.toLong()) {
            last = mid - 1
        } else {
            return mid
        }
    }
    return -1
}

// Returns an index of -1 if item can't be found
internal fun MessageRepository.findMessageById(normalizedId: Long): MessageInfoModel {
    var first = 0
    var last = size - 1
    while (first <= last) {
        var mid = (first + last) / 2
        val midVal = get(mid).normalizedID
        if (midVal < normalizedId) {
            first = mid + 1
        } else if (midVal > normalizedId) {
            last = mid - 1
        } else {
            return get(mid)
        }
    }
    return EMPTY_MESSAGE_INFO_MODEL
}
