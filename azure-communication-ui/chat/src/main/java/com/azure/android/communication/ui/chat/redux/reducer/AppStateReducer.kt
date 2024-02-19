// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.redux.reducer

import com.azure.android.communication.ui.chat.redux.action.Action
import com.azure.android.communication.ui.chat.redux.state.AppReduxState
import com.azure.android.communication.ui.chat.redux.state.ReduxState

internal class AppStateReducer(
    private val chatReducer: ChatReducer,
    private val participantReducer: ParticipantsReducer,
    private val lifecycleReducer: LifecycleReducer,
    private val errorReducer: ErrorReducer,
    private val navigationReducer: NavigationReducer,
    private val repositoryReducer: RepositoryReducer,
    private val networkReducer: NetworkReducer,
    private val accessibilityReducer: Reducer<ReduxState> =
        object : Reducer<ReduxState> {
            override fun reduce(
                previousState: ReduxState,
                action: Action,
            ): ReduxState {
                return previousState
            }
        },
) :
    Reducer<ReduxState> {
    override fun reduce(
        state: ReduxState,
        action: Action,
    ): ReduxState {
        val appState =
            AppReduxState(
                threadID = state.chatState.chatInfoModel.threadId,
                localParticipantIdentifier = state.participantState.localParticipantInfoModel.userIdentifier,
                localParticipantDisplayName = state.participantState.localParticipantInfoModel.displayName,
            )

        appState.chatState =
            chatReducer.reduce(
                state = state.chatState,
                action = action,
            )
        appState.participantState =
            participantReducer.reduce(
                state = state.participantState,
                action = action,
            )
        appState.lifecycleState =
            lifecycleReducer.reduce(
                state = state.lifecycleState,
                action = action,
            )
        appState.errorState =
            errorReducer.reduce(
                state = state.errorState,
                action = action,
            )
        appState.navigationState =
            navigationReducer.reduce(
                state = state.navigationState,
                action = action,
            )
        appState.repositoryState =
            repositoryReducer.reduce(
                state = state.repositoryState,
                action = action,
            )
        appState.networkState =
            networkReducer.reduce(
                state = state.networkState,
                action = action,
            )
        return accessibilityReducer.reduce(appState, action = action)
    }
}
