package com.azure.android.communication.ui.calling.redux.reducer

import com.azure.android.communication.ui.calling.redux.action.Action
import com.azure.android.communication.ui.calling.redux.action.ButtonViewDataAction
import com.azure.android.communication.ui.calling.redux.state.ButtonState

internal interface ButtonViewDataReducer : Reducer<ButtonState>
internal class ButtonViewDataReducerImpl : ButtonViewDataReducer {
    override fun reduce(state: ButtonState, action: Action): ButtonState {
        return when (action) {
            is ButtonViewDataAction.CallScreenCameraButtonIsVisibleUpdated -> {
                state.copy(cameraButtonState = state.cameraButtonState?.copy(isVisible = action.isVisible))
            }
            is ButtonViewDataAction.CallScreenCameraButtonIsEnabledUpdated -> {
                state.copy(cameraButtonState = state.cameraButtonState?.copy(isEnabled = action.isEnabled))
            }
            else -> state
        }
    }
}
