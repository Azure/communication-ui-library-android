package com.azure.android.communication.ui.chat

import com.azure.android.communication.ui.chat.redux.AppStore
import com.azure.android.communication.ui.chat.redux.state.AppReduxState
import com.azure.android.communication.ui.chat.redux.state.ReduxState
import com.azure.android.communication.ui.chat.utilities.CoroutineContextProvider

internal class MockStore : AppStore<ReduxState>(
    dispatcher = CoroutineContextProvider().SingleThreaded,
    reducer = MockReducer(),
    middlewares = mutableListOf(),
    initialState = AppReduxState()
) {
    val mockReducer: MockReducer = reducer as MockReducer
}
