// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.redux.action

import com.azure.android.communication.ui.chat.error.ChatStateError

internal sealed class ErrorAction : Action {
    class ChatStateErrorOccurred(val chatStateError: ChatStateError) : ErrorAction()
}
