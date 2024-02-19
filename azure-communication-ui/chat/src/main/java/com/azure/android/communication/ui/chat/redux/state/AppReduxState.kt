// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.redux.state

import com.azure.android.communication.ui.chat.models.ChatInfoModel
import com.azure.android.communication.ui.chat.models.EMPTY_MESSAGE_INFO_MODEL
import com.azure.android.communication.ui.chat.models.LocalParticipantInfoModel
import com.azure.android.communication.ui.chat.models.MessageContextMenuModel
import org.threeten.bp.OffsetDateTime

internal class AppReduxState(
    threadID: String,
    localParticipantIdentifier: String,
    localParticipantDisplayName: String?,
) : ReduxState {
    override var chatState: ChatState =
        ChatState(
            chatStatus = ChatStatus.NONE,
            chatInfoModel =
                ChatInfoModel(
                    threadId = threadID,
                    topic = null,
                    allMessagesFetched = false,
                    isThreadDeleted = false,
                ),
            lastReadMessageId = "",
            messageContextMenu = MessageContextMenuModel(EMPTY_MESSAGE_INFO_MODEL, emptyList()),
        )

    override var participantState: ParticipantsState =
        ParticipantsState(
            localParticipantInfoModel =
                LocalParticipantInfoModel(
                    userIdentifier = localParticipantIdentifier,
                    displayName = localParticipantDisplayName,
                ),
            participants = mapOf(),
            participantTyping = mapOf(),
            participantsReadReceiptMap = mapOf(),
            latestReadMessageTimestamp = OffsetDateTime.MIN,
            hiddenParticipant = setOf(),
        )

    override var lifecycleState: LifecycleState = LifecycleState(LifecycleStatus.FOREGROUND)

    override var errorState: ErrorState = ErrorState(fatalError = null, chatCompositeErrorEvent = null)

    override var navigationState: NavigationState = NavigationState(NavigationStatus.NONE)

    override var repositoryState: RepositoryState =
        RepositoryState(lastUpdatedTimestamp = System.currentTimeMillis())

    override var networkState: NetworkState =
        NetworkState(networkStatus = NetworkStatus.CONNECTED, disconnectOffsetDateTime = null)
}
