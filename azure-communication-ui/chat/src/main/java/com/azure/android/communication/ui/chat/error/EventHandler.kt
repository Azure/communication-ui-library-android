// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.error

import com.azure.android.communication.ui.chat.configuration.ChatCompositeConfiguration
import com.azure.android.communication.ui.chat.models.ChatCompositeEvent
import com.azure.android.communication.ui.chat.models.ChatCompositeEventCode.Companion.CHAT_REMOVED
import com.azure.android.communication.ui.chat.redux.AppStore
import com.azure.android.communication.ui.chat.redux.state.ReduxState
import com.azure.android.communication.ui.chat.utilities.CoroutineContextProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

internal class EventHandler(
    coroutineContextProvider: CoroutineContextProvider,
    private val store: AppStore<ReduxState>,
    private val configuration: ChatCompositeConfiguration,
) {
    private var localParticipantRemovedPreviousState = false
    private val coroutineScope = CoroutineScope((coroutineContextProvider.Default))

    fun start() {
        coroutineScope.launch(Dispatchers.Default) {
            store.getStateFlow().collect {
                onLocalParticipantRemoved(it.chatState.localParticipantInfoModel.isActiveChatThreadParticipant)
            }
        }
    }

    fun stop() {
        coroutineScope.cancel()
    }

    private fun onLocalParticipantRemoved(isLocalParticipantActive: Boolean) {
        checkIfLocalParticipantRemovedEventIsNewAndNotify(
            isLocalParticipantActive,
            localParticipantRemovedPreviousState
        ) {
            localParticipantRemovedPreviousState = it
        }
    }
    private fun checkIfLocalParticipantRemovedEventIsNewAndNotify(
        localParticipantActiveCurrent: Boolean,
        localParticipantRemovedPrevious: Boolean,
        function: (Boolean) -> Unit,
    ) {
        if (localParticipantActiveCurrent != localParticipantRemovedPrevious && !localParticipantActiveCurrent) {
            function(localParticipantActiveCurrent)
            configuration.eventHandlerRepository.getLocalParticipantRemovedHandlers().forEach {
                it.handle(ChatCompositeEvent(CHAT_REMOVED))
            }
        }
    }
}
