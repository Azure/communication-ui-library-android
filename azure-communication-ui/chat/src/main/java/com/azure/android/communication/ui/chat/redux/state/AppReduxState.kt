// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.redux.state

import com.azure.android.communication.ui.chat.models.ChatInfoModel
import com.azure.android.communication.ui.chat.models.LocalParticipantInfoModel
import org.threeten.bp.OffsetDateTime

internal class AppReduxState(
    threadID: String,
    localParticipantIdentifier: String,
    localParticipantDisplayName: String?,
) : ReduxState {
    override var chatState: ChatState = ChatState(
        chatStatus = ChatStatus.NONE,
        localParticipantInfoModel = LocalParticipantInfoModel(
            userIdentifier = localParticipantIdentifier,
            displayName = localParticipantDisplayName
        ),
        chatInfoModel = ChatInfoModel(
            threadId = threadID,
            topic = null,
            allMessagesFetched = false,
            isThreadDeleted = false
        ),
        lastReadMessageId = "",
        lastSendMessageId = "",
    )

    override var participantState: ParticipantsState = ParticipantsState(
        participants = mapOf(),
        participantTyping = mapOf(),
        participantsReadReceiptMap = mapOf(),
        latestReadMessageTimestamp = OffsetDateTime.MIN
    )

    override var lifecycleState: LifecycleState = LifecycleState(LifecycleStatus.FOREGROUND)

    override var errorState: ErrorState = ErrorState(fatalError = null, chatStateError = null)

    override var navigationState: NavigationState = NavigationState(NavigationStatus.NONE)

    override var repositoryState: RepositoryState =
        RepositoryState(lastUpdatedTimestamp = System.currentTimeMillis())

    override var networkState: NetworkState =
        NetworkState(networkStatus = NetworkStatus.CONNECTED, disconnectOffsetDateTime = null)
}
