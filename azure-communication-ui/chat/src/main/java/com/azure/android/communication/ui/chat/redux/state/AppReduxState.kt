// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.redux.state

internal class AppReduxState : ReduxState {
    override var chatState: ChatState = ChatState(ChatStatus.NONE)

    override var participantState: ParticipantsState = ParticipantsState(HashMap())

    override var lifecycleState: LifecycleState = LifecycleState(LifecycleStatus.FOREGROUND)

    override var errorState: ErrorState = ErrorState(fatalError = null, chatStateError = null)

    override var navigationState: NavigationState = NavigationState(NavigationStatus.NONE)
}
