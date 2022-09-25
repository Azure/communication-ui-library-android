// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.redux.middleware.listener

import com.azure.android.communication.ui.chat.redux.Store
import com.azure.android.communication.ui.chat.redux.state.ReduxState
import com.azure.android.communication.ui.chat.service.ChatService

internal class ChatServiceListener(
    private val chatService: ChatService,
) {

    // Start Listening to the Service, Dispatch to the store
    fun startListening(store: Store<ReduxState>) {

    }

    fun stopListening() {

    }
}
