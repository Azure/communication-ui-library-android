package com.azure.android.communication.ui.redux.reducer

import com.azure.android.communication.ui.redux.action.Action
import com.azure.android.communication.ui.redux.action.DisplayAction
import com.azure.android.communication.ui.redux.state.DisplayState

internal interface DisplayReducer : Reducer<DisplayState>

internal class DisplayReducerImpl :
    DisplayReducer {
    override fun reduce(state: DisplayState, action: Action): DisplayState {
        return when (action) {
            is DisplayAction.IsConfirmLeaveOverlayDisplayed -> {
                state.copy(confirmLeaveOverlayDisplayState = action.isConfirmLeaveOverlayDisplayed)
            }
            else -> state
        }
    }
}
