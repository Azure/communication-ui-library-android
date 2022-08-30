// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.redux.middleware

import com.azure.android.communication.ui.chat.redux.Dispatch
import com.azure.android.communication.ui.chat.redux.Middleware
import com.azure.android.communication.ui.chat.redux.Store
import com.azure.android.communication.ui.chat.redux.state.ReduxState

internal interface ChatMiddleware

internal class ChatMiddlewareImpl :
    Middleware<ReduxState>,
    ChatMiddleware {
    override fun invoke(store: Store<ReduxState>): (next: Dispatch) -> Dispatch {
        TODO("Not yet implemented")
    }
}
