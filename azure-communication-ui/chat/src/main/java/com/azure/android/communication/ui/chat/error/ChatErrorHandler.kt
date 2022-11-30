package com.azure.android.communication.ui.chat.error

import com.azure.android.communication.ui.chat.configuration.ChatCompositeConfiguration
import com.azure.android.communication.ui.chat.models.ChatCompositeErrorEvent
import com.azure.android.communication.ui.chat.redux.AppStore
import com.azure.android.communication.ui.chat.redux.state.ReduxState
import com.azure.android.communication.ui.chat.utilities.CoroutineContextProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

internal class ChatErrorHandler(
    coroutineContextProvider: CoroutineContextProvider,
    private val store: AppStore<ReduxState>,
    private val configuration: ChatCompositeConfiguration,
) {
    private val coroutineScope = CoroutineScope((coroutineContextProvider.Default))

    fun start() {
        coroutineScope.launch(Dispatchers.Default) {
            store.getStateFlow().collect {
                onStateChanged(it)
            }
        }
    }
    private fun onStateChanged(state: ReduxState) {
        if (state.errorState.chatCompositeErrorEvent != null) {
            chatStateErrorCallback(state.errorState.chatCompositeErrorEvent)
        }
    }

    private fun chatStateErrorCallback(chatStateError: ChatCompositeErrorEvent?) {
        try {
            val eventArgs = ChatCompositeErrorEvent(
                null,
                chatStateError?.cause,
            )
            configuration.chatCompositeEventsHandler.getOnErrorHandlers()
                .forEach { it.handle(eventArgs) }
        } catch (error: Throwable) {
            // suppress any possible application errors
        }
    }
    fun stop() {
        coroutineScope.cancel()
    }
}
