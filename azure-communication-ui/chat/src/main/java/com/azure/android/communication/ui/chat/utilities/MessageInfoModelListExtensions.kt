package com.azure.android.communication.ui.chat.utilities

import com.azure.android.communication.ui.chat.models.MessageInfoModel

// Returns an index of -1 if item can't be found
internal fun List<MessageInfoModel>.findMessageIdxById(messageId: String): Int {
    if (messageId.trim() == "") return -1
    var first = 0
    var last = size - 1
    var mid: Int = size / 2
    while (first <= last) {
        mid = (first + last) / 2


        while (this[mid].id == null) {
            mid--
            if (mid < first) {
                throw IllegalArgumentException("Message with id $messageId not found")
            }
        }
        val midVal = this[mid].id!!.toLong()
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
