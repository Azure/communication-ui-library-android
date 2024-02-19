// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.error

import com.azure.android.communication.ui.chat.configuration.ChatCompositeConfiguration
import com.azure.android.communication.ui.chat.redux.AppStore
import com.azure.android.communication.ui.chat.redux.state.ReduxState
import com.azure.android.communication.ui.chat.utilities.CoroutineContextProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

internal class EventHandler(
    coroutineContextProvider: CoroutineContextProvider,
    private val store: AppStore<ReduxState>,
    private val configuration: ChatCompositeConfiguration,
) {
    private var isActiveChatThreadParticipantStateFlow =
        MutableStateFlow(
            store.getCurrentState().participantState.localParticipantInfoModel.isActiveChatThreadParticipant,
        )

    private val coroutineScope = CoroutineScope((coroutineContextProvider.Default))

    fun start() {
        coroutineScope.launch(Dispatchers.Default) {
            store.getStateFlow().collect {
                isActiveChatThreadParticipantStateFlow.value =
                    it.participantState.localParticipantInfoModel.isActiveChatThreadParticipant
            }
        }

        coroutineScope.launch(Dispatchers.Default) {
            isActiveChatThreadParticipantStateFlow.collect {
                onIsActiveChanged(it)
            }
        }
    }

    fun stop() {
        coroutineScope.cancel()
    }

    private fun onIsActiveChanged(isActiveChatThreadParticipant: Boolean) {
        if (!isActiveChatThreadParticipant) {
            configuration.eventHandlerRepository.getLocalParticipantRemovedHandlers().forEach {
                it.handle(store.getCurrentState().participantState.localParticipantInfoModel.userIdentifier)
            }
        }
    }
}
