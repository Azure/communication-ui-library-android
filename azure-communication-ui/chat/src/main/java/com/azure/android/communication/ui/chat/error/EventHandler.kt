// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.error

import com.azure.android.communication.ui.chat.configuration.ChatCompositeConfiguration
import com.azure.android.communication.ui.chat.error.EventCode.Companion.CHAT_LOCAL_PARTICIPANT_REMOVED
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
    private var lastChatStateEvent: ChatStateEvent? = null
    private val coroutineScope = CoroutineScope((coroutineContextProvider.Default))

    fun start() {
        coroutineScope.launch(Dispatchers.Default) {
            store.getStateFlow().collect {
                onEventStateChanged(it.errorState.chatStateEvent)
            }
        }
    }

    fun stop() {
        coroutineScope.cancel()
    }

    private fun onEventStateChanged(chatStateEvent: ChatStateEvent?) {
        checkIfCallStateErrorIsNewAndNotify(chatStateEvent, lastChatStateEvent) {
            lastChatStateEvent = it
        }
    }
    private fun checkIfCallStateErrorIsNewAndNotify(
        newChatStateEvent: ChatStateEvent?,
        lastChatStateEvent: ChatStateEvent?,
        function: (ChatStateEvent) -> Unit,
    ) {
        if (newChatStateEvent != null && newChatStateEvent != lastChatStateEvent) {
            function(newChatStateEvent)
            chatStateEventCallback(newChatStateEvent)
        }
    }

    private fun chatStateEventCallback(chatStateEvent: ChatStateEvent) {
        try {
            when (chatStateEvent.eventCode) {
                CHAT_LOCAL_PARTICIPANT_REMOVED -> {
                    configuration.eventsHandler.onLocalParticipantRemovedEventHandler?.run {
                        handle(ChatCompositeEvent(CHAT_REMOVED))
                    }
                }
                else -> throw IllegalArgumentException("Unknown error code: ${chatStateEvent.eventCode}")
            }
        } catch (error: Throwable) {
            // suppress any possible application errors
        }
    }
}
