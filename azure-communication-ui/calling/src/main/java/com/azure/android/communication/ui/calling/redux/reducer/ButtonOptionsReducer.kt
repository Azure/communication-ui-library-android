package com.azure.android.communication.ui.calling.redux.reducer

import com.azure.android.communication.ui.calling.redux.action.Action
import com.azure.android.communication.ui.calling.redux.action.ButtonOptionsAction
import com.azure.android.communication.ui.calling.redux.state.ButtonOptionsState

internal interface ButtonOptionsReducer : Reducer<ButtonOptionsState>
internal class ButtonOptionsReducerImpl : ButtonOptionsReducer{
    override fun reduce(state: ButtonOptionsState, action: Action): ButtonOptionsState {
        return when (action) {
            is ButtonOptionsAction.CallScreenCameraButtonIsVisibleUpdated -> {
                state.copy(cameraButtonState = state.cameraButtonState?.copy(isVisible = action.isVisible))
            }
            is ButtonOptionsAction.CallScreenCameraButtonIsEnabledUpdated -> {
                state.copy(cameraButtonState = state.cameraButtonState?.copy(isEnabled = action.isEnabled))
            }
            else -> state
        }
    }
}