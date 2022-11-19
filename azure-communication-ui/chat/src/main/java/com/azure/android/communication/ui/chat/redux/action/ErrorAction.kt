// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.redux.action

import com.azure.android.communication.ui.chat.error.ChatCompositeErrorEvent

internal sealed class ErrorAction : Action {
    class ChatStateErrorOccurred(val chatCompositeErrorEvent: ChatCompositeErrorEvent) : ErrorAction()
}
