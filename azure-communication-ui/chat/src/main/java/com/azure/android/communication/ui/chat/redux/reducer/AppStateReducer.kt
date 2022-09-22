// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.redux.reducer

import com.azure.android.communication.ui.chat.redux.action.Action
import com.azure.android.communication.ui.chat.redux.state.AppReduxState

internal class AppStateReducer(
    private val chatReducer: ChatReducer,
    private val participantReducer: ParticipantsReducer,
    private val lifecycleReducer: LifecycleReducer,
    private val errorReducer: ErrorReducer,
    private val navigationReducer: NavigationReducer,
) :
    Reducer<AppReduxState> {
    override fun reduce(state: AppReduxState, action: Action): AppReduxState {

        val appState = AppReduxState()

        appState.chatState = chatReducer.reduce(
            state.chatState,
            action
        )

        appState.participantState = participantReducer.reduce(
            state.participantState,
            action
        )

        appState.lifecycleState = lifecycleReducer.reduce(state.lifecycleState, action)
        appState.errorState = errorReducer.reduce(state.errorState, action)
        appState.navigationState = navigationReducer.reduce(state.navigationState, action)
        return appState
    }
}
