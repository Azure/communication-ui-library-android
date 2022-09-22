// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.error

import com.azure.android.core.util.ExpandableStringEnum

internal class ErrorCode : ExpandableStringEnum<ErrorCode?>() {
    companion object {
        val CHAT_JOIN_FAILED = fromString("chatJoinFailed")

        private fun fromString(name: String): ErrorCode {
            return fromString(name, ErrorCode::class.java)
        }
    }
}
