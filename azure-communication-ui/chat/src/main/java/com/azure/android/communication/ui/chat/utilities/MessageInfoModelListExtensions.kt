package com.azure.android.communication.ui.chat.utilities

import com.azure.android.communication.ui.chat.models.MessageInfoModel

internal fun List<MessageInfoModel>.findMessageIdxById(messageId: String): Int {
    var first = 0
    var last = size - 1
    var mid: Int = size / 2
    while (first <= last) {
        mid = (first + last) / 2
        val midVal = this[mid].id!!.toInt()
        if (midVal!!.toInt() < messageId.toInt()) {
            first = mid + 1
        } else if (midVal > messageId.toInt()) {
            last = mid - 1
        } else {
            return mid
        }
    }
    throw IllegalArgumentException("Message with id $messageId not found")
}
