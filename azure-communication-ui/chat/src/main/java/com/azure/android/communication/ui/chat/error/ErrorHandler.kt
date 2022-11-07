// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.error

import com.azure.android.communication.ui.chat.configuration.ChatCompositeEventsHandler
import com.azure.android.communication.ui.chat.error.ErrorCode.Companion.CHAT_LOCAL_PARTICIPANT_EVICTED
import com.azure.android.communication.ui.chat.models.ChatCompositeErrorEvent
import com.azure.android.communication.ui.chat.models.ChatCompositeEventCode
import com.azure.android.communication.ui.chat.models.ChatCompositeEventCode.Companion.CHAT_EVICTED
import com.azure.android.communication.ui.chat.redux.AppStore
import com.azure.android.communication.ui.chat.redux.state.ErrorState
import com.azure.android.communication.ui.chat.redux.state.ReduxState
import com.azure.android.communication.ui.chat.utilities.CoroutineContextProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

internal class ErrorHandler(
    coroutineContextProvider: CoroutineContextProvider,
    private val store: AppStore<ReduxState>,
    private val errorHandlers: ChatCompositeEventsHandler,
) {
    private var lastFatalError: FatalError? = null
    private var lastChatStateError: ChatStateError? = null
    private val coroutineScope = CoroutineScope((coroutineContextProvider.Default))

    fun start() {
        coroutineScope.launch(Dispatchers.Default) {
            store.getStateFlow().collect {
                onErrorStateChanged(it.errorState)
            }
        }
    }

    fun stop() {
        coroutineScope.cancel()
    }

    private fun onErrorStateChanged(errorState: ErrorState) {
        checkIfCallStateErrorIsNewAndNotify(errorState.chatStateError, lastChatStateError,) {
            lastChatStateError = it
        }
    }
    private fun checkIfCallStateErrorIsNewAndNotify(
        newCallStateError: ChatStateError?,
        lastCallStateError: ChatStateError?,
        function: (ChatStateError) -> Unit,
    ) {
        if (newCallStateError != null && newCallStateError != lastCallStateError) {
            if (shouldNotifyError(newCallStateError)) {
                function(newCallStateError)
                chatStateErrorCallback(newCallStateError)
            }
        }
    }

    private fun chatStateErrorCallback(chatStateError: ChatStateError) {
        try {
            val eventArgs =
                ChatCompositeErrorEvent(
                    getChatCompositeErrorCode(chatStateError.errorCode),
                    null,
                )
            errorHandlers.getOnErrorHandlers().forEach { it.handle(eventArgs) }
        } catch (error: Throwable) {
            // suppress any possible application errors
        }
    }

    private fun getChatCompositeErrorCode(errorCode: ErrorCode): ChatCompositeEventCode {
        return when (errorCode) {
            CHAT_LOCAL_PARTICIPANT_EVICTED -> CHAT_EVICTED
            else -> throw IllegalArgumentException("Unknown error code: $errorCode")
        }
    }

    private fun shouldNotifyError(newCallStateError: ChatStateError) =
        newCallStateError.errorCode != ErrorCode.CHAT_JOIN_FAILED
}
