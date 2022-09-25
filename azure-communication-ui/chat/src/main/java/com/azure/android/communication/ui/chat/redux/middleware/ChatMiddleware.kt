// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.redux.middleware

import com.azure.android.communication.ui.chat.redux.Dispatch
import com.azure.android.communication.ui.chat.redux.Middleware
import com.azure.android.communication.ui.chat.redux.Store
import com.azure.android.communication.ui.chat.redux.middleware.listener.ChatActionListener
import com.azure.android.communication.ui.chat.redux.middleware.listener.ChatServiceListener
import com.azure.android.communication.ui.chat.redux.state.AppReduxState
import com.azure.android.communication.ui.chat.redux.state.ReduxState

internal interface ChatMiddleware

internal class ChatServiceMiddleware(
    private val chatActionListener: ChatActionListener,
    private val chatServiceListener: ChatServiceListener,
) :
    Middleware<AppReduxState>,
    ChatMiddleware {
    override fun invoke(store: Store<AppReduxState>): (next: Dispatch) -> Dispatch {
        TODO("Not yet implemented")
    }
}
