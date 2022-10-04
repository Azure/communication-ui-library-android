// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.redux.reducer

import com.azure.android.communication.ui.calling.redux.action.Action
import com.azure.android.communication.ui.calling.redux.action.ExternalOverlayAction
import com.azure.android.communication.ui.calling.redux.state.ExternalOverlayState

internal interface ExternalOverlayReducer : Reducer<ExternalOverlayState>

internal class ExternalOverlayReducerImpl : ExternalOverlayReducer {
    override fun reduce(state: ExternalOverlayState, action: Action): ExternalOverlayState {
        return when (action) {
            is ExternalOverlayAction.SetOverlay -> {
                state.copy(externalOverlayViewBuilder = action.viewBuilder)
            }
            is ExternalOverlayAction.RemoveOverlay -> {
                state.copy(externalOverlayViewBuilder = null)
            }
            else -> state
        }
    }
}
