// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.redux.state

internal enum class ChatStatus {
    NONE,
}

internal data class ChatState(val chatStatus: ChatStatus)
