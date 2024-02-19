// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.redux.reducer

import com.azure.android.communication.ui.calling.redux.action.Action
import com.azure.android.communication.ui.calling.redux.action.PipAction
import com.azure.android.communication.ui.calling.redux.state.PictureInPictureState
import com.azure.android.communication.ui.calling.redux.state.PictureInPictureStatus

// TODO: VisibilityReducer
internal interface PipReducer : Reducer<PictureInPictureState>

internal class PipReducerImpl : PipReducer {
    override fun reduce(
        state: PictureInPictureState,
        action: Action,
    ): PictureInPictureState {
        return when (action) {
            is PipAction.PipModeEntered -> {
                state.copy(status = PictureInPictureStatus.PIP_MODE_ENTERED)
            }
            is PipAction.ShowNormalEntered -> {
                state.copy(status = PictureInPictureStatus.VISIBLE)
            }
            is PipAction.HideRequested -> {
                state.copy(status = PictureInPictureStatus.HIDE_REQUESTED)
            }
            is PipAction.HideEntered -> {
                state.copy(status = PictureInPictureStatus.HIDDEN)
            }
            else -> state
        }
    }
}
