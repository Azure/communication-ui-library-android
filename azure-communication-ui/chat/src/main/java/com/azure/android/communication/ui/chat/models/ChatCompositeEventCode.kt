// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.models

import com.azure.android.core.util.ExpandableStringEnum

internal class ChatCompositeEventCode : ExpandableStringEnum<ChatCompositeEventCode?>() {
    companion object {
        val CHAT_EVICTED = fromString("chatEvicted", ChatCompositeEventCode::class.java)
    }

    private fun fromString(name: String): ChatCompositeEventCode {
        return fromString(name, ChatCompositeEventCode::class.java)
    }

    fun values(): Collection<ChatCompositeEventCode> {
        return values(ChatCompositeEventCode::class.java)
    }
}
