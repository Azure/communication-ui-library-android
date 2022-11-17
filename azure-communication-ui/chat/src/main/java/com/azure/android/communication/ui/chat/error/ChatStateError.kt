// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.error

internal data class ChatStateError(
    val errorCode: ErrorCode
)

internal data class ChatStateEvent(
    val eventCode: EventCode
)
