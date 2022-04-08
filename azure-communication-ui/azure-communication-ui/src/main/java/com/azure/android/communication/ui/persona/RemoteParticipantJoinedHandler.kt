// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.persona

import com.azure.android.communication.ui.configuration.CallCompositeConfiguration
import com.azure.android.communication.ui.redux.Store
import com.azure.android.communication.ui.redux.state.ReduxState
import kotlinx.coroutines.flow.collect

internal class RemoteParticipantJoinedHandler(
    private val configuration: CallCompositeConfiguration,
    private val store: Store<ReduxState>,
) {

    suspend fun start() {
        store.getStateFlow().collect {
            onStateChanged(it)
        }
    }

    private fun onStateChanged(state: ReduxState) {
    }
}
