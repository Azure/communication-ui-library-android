// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.manager

import com.azure.android.communication.ui.calling.configuration.CallCompositeConfiguration
import com.azure.android.communication.ui.calling.models.CallCompositeMultitaskingStateChangedEvent
import com.azure.android.communication.ui.calling.redux.AppStore
import com.azure.android.communication.ui.calling.redux.state.PictureInPictureStatus
import com.azure.android.communication.ui.calling.redux.state.ReduxState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

internal class MultitaskingManager(
    private val store: AppStore<ReduxState>,
    private val configuration: CallCompositeConfiguration
) {

    private val pipStatusFlow = MutableStateFlow(store.getCurrentState().pipState.status)

    fun start(
        coroutineScope: CoroutineScope,
    ) {
        coroutineScope.launch {
            store.getStateFlow().collect {
                pipStatusFlow.value = it.pipState.status
            }
        }

        coroutineScope.launch {
            pipStatusFlow.collect { status ->
                try {
                    configuration.callCompositeEventsHandler.getOnMultitaskingStateChangedEventHandlers().forEach { handler ->
                        handler.handle(
                            CallCompositeMultitaskingStateChangedEvent(
                                status == PictureInPictureStatus.PIP_MODE_ENTERED
                            )
                        )
                    }
                } catch (error: Throwable) {
                    // suppress any possible application errors
                }
            }
        }

        configuration.callCompositeEventsHandler.getOnMultitaskingStateChangedEventHandlers()
    }
}
