// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.manager

import com.azure.android.communication.ui.calling.configuration.CallCompositeConfiguration
import com.azure.android.communication.ui.calling.models.CallCompositePictureInPictureChangedEvent
import com.azure.android.communication.ui.calling.redux.AppStore
import com.azure.android.communication.ui.calling.redux.state.PictureInPictureStatus
import com.azure.android.communication.ui.calling.redux.state.ReduxState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

internal class MultitaskingManager(
    private val store: AppStore<ReduxState>,
    private val configuration: CallCompositeConfiguration,
) {
    private var pipStatus = store.getCurrentState().pipState.status

    fun start(coroutineScope: CoroutineScope) {
        coroutineScope.launch {
            store.getStateFlow().collect {
                if (pipStatus != it.pipState.status) {
                    pipStatus = it.pipState.status
                    notify(it.pipState.status)
                }
            }
        }
    }

    private fun notify(status: PictureInPictureStatus) {
        try {
            configuration.callCompositeEventsHandler.getOnMultitaskingStateChangedEventHandlers().forEach { handler ->
                handler.handle(
                    CallCompositePictureInPictureChangedEvent(
                        status == PictureInPictureStatus.PIP_MODE_ENTERED,
                    ),
                )
            }
        } catch (error: Throwable) {
            // suppress any possible application errors
        }
    }
}
