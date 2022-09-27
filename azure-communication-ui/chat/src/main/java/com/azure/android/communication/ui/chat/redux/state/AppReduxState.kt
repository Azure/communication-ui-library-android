// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.redux.state

import com.azure.android.communication.ui.chat.models.ChatInfoModel
import com.azure.android.communication.ui.chat.models.ParticipantInfoModel

internal class AppReduxState(
    threadID: String,
    localParticipantIdentifier: String,
    localParticipantDisplayName: String?
) : ReduxState {
    override var chatState: ChatState = ChatState(
        chatStatus = ChatStatus.NONE,
        localParticipantInfoModel = ParticipantInfoModel(
            userIdentifier = localParticipantIdentifier,
            displayName = localParticipantDisplayName
        ),
        chatInfoModel = ChatInfoModel(
            threadId = threadID,
            topic = null
        )
    )

    override var participantState: ParticipantsState = ParticipantsState(HashMap())

    override var lifecycleState: LifecycleState = LifecycleState(LifecycleStatus.FOREGROUND)

    override var errorState: ErrorState = ErrorState(fatalError = null, chatStateError = null)

    override var navigationState: NavigationState = NavigationState(NavigationStatus.NONE)
}
