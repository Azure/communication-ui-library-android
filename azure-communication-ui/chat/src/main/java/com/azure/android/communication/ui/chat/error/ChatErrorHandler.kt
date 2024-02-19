// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.error

import com.azure.android.communication.ui.chat.configuration.ChatCompositeConfiguration
import com.azure.android.communication.ui.chat.models.ChatCompositeErrorCode
import com.azure.android.communication.ui.chat.models.ChatCompositeErrorEvent
import com.azure.android.communication.ui.chat.redux.AppStore
import com.azure.android.communication.ui.chat.redux.state.ReduxState
import com.azure.android.communication.ui.chat.utilities.CoroutineContextProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

internal class ChatErrorHandler(
    coroutineContextProvider: CoroutineContextProvider,
    private val store: AppStore<ReduxState>,
    private val configuration: ChatCompositeConfiguration,
) {
    private val coroutineScope = CoroutineScope((coroutineContextProvider.Default))
    private var lastChatErrorEvent: ChatCompositeErrorEvent? = null

    fun start() {
        coroutineScope.launch(Dispatchers.Default) {
            store.getStateFlow().collect {
                onStateChanged(it)
            }
        }
    }

    private fun onStateChanged(state: ReduxState) {
        checkIfCallStateErrorIsNewAndNotify(
            state.errorState.chatCompositeErrorEvent,
            lastChatErrorEvent,
        ) { lastChatErrorEvent = it }
    }

    private fun chatStateErrorCallback(chatStateError: ChatCompositeErrorEvent) {
        try {
            configuration.eventHandlerRepository.getOnErrorHandlers()
                .forEach { it.handle(chatStateError) }
        } catch (error: Throwable) {
            // suppress any possible application errors
        }
    }

    private fun checkIfCallStateErrorIsNewAndNotify(
        newChatErrorEvent: ChatCompositeErrorEvent?,
        lastChatErrorEvent: ChatCompositeErrorEvent?,
        function: (ChatCompositeErrorEvent) -> Unit,
    ) {
        if (newChatErrorEvent != null && newChatErrorEvent != lastChatErrorEvent) {
            if (shouldNotifyError(newChatErrorEvent)) {
                function(newChatErrorEvent)
                chatStateErrorCallback(newChatErrorEvent)
            }
        }
    }

    // TODO: Check the logic again when we need to expose more error
    private fun shouldNotifyError(newCallStateError: ChatCompositeErrorEvent) =
        newCallStateError.errorCode == ChatCompositeErrorCode.JOIN_FAILED

    fun stop() {
        coroutineScope.cancel()
    }
}
