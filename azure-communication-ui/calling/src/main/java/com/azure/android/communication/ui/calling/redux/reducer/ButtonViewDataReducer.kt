package com.azure.android.communication.ui.calling.redux.reducer

import com.azure.android.communication.ui.calling.redux.action.Action
import com.azure.android.communication.ui.calling.redux.action.ButtonViewDataAction
import com.azure.android.communication.ui.calling.redux.state.ButtonState

internal interface ButtonViewDataReducer : Reducer<ButtonState>
internal class ButtonViewDataReducerImpl : ButtonViewDataReducer {
    override fun reduce(state: ButtonState, action: Action): ButtonState {
        return when (action) {
            is ButtonViewDataAction.CallScreenCameraButtonIsVisibleUpdated -> {
                state.copy(callScreenCameraButtonState = state.callScreenCameraButtonState?.copy(isVisible = action.isVisible))
            }
            is ButtonViewDataAction.CallScreenCameraButtonIsEnabledUpdated -> {
                state.copy(callScreenCameraButtonState = state.callScreenCameraButtonState?.copy(isEnabled = action.isEnabled))
            }
            is ButtonViewDataAction.CallScreenMicButtonIsVisibleUpdated -> {
                state.copy(callScreenMicButtonState = state.callScreenMicButtonState?.copy(isVisible = action.isVisible))
            }
            is ButtonViewDataAction.CallScreenMicButtonIsEnabledUpdated -> {
                state.copy(callScreenMicButtonState = state.callScreenMicButtonState?.copy(isEnabled = action.isEnabled))
            }
            is ButtonViewDataAction.CallScreenAudioDeviceButtonIsVisibleUpdated -> {
                state.copy(callScreenAudioDeviceButtonState = state.callScreenAudioDeviceButtonState?.copy(isVisible = action.isVisible))
            }
            is ButtonViewDataAction.CallScreenAudioDeviceButtonIsEnabledUpdated -> {
                state.copy(callScreenAudioDeviceButtonState = state.callScreenAudioDeviceButtonState?.copy(isEnabled = action.isEnabled))
            }
            is ButtonViewDataAction.CallScreenLiveCaptionsButtonIsVisibleUpdated -> {
                state.copy(liveCaptionsButton = state.liveCaptionsButton?.copy(isVisible = action.isVisible))
            }
            is ButtonViewDataAction.CallScreenLiveCaptionsButtonIsEnabledUpdated -> {
                state.copy(liveCaptionsButton = state.liveCaptionsButton?.copy(isEnabled = action.isEnabled))
            }
            is ButtonViewDataAction.CallScreenLiveCaptionsToggleButtonIsVisibleUpdated -> {
                state.copy(liveCaptionsToggleButton = state.liveCaptionsToggleButton?.copy(isVisible = action.isVisible))
            }
            is ButtonViewDataAction.CallScreenLiveCaptionsToggleButtonIsEnabledUpdated -> {
                state.copy(liveCaptionsToggleButton = state.liveCaptionsToggleButton?.copy(isEnabled = action.isEnabled))
            }
            is ButtonViewDataAction.CallScreenSpokenLanguageButtonIsVisibleUpdated -> {
                state.copy(spokenLanguageButton = state.spokenLanguageButton?.copy(isVisible = action.isVisible))
            }
            is ButtonViewDataAction.CallScreenSpokenLanguageButtonIsEnabledUpdated -> {
                state.copy(spokenLanguageButton = state.spokenLanguageButton?.copy(isEnabled = action.isEnabled))
            }
            is ButtonViewDataAction.CallScreenCaptionsLanguageButtonIsVisibleUpdated -> {
                state.copy(captionsLanguageButton = state.captionsLanguageButton?.copy(isVisible = action.isVisible))
            }
            is ButtonViewDataAction.CallScreenCaptionsLanguageButtonIsEnabledUpdated -> {
                state.copy(captionsLanguageButton = state.captionsLanguageButton?.copy(isEnabled = action.isEnabled))
            }
            is ButtonViewDataAction.CallScreenShareDiagnosticsButtonIsVisibleUpdated -> {
                state.copy(shareDiagnosticsButton = state.shareDiagnosticsButton?.copy(isVisible = action.isVisible))
            }
            is ButtonViewDataAction.CallScreenShareDiagnosticsButtonIsEnabledUpdated -> {
                state.copy(shareDiagnosticsButton = state.shareDiagnosticsButton?.copy(isEnabled = action.isEnabled))
            }
            is ButtonViewDataAction.CallScreenReportIssueButtonIsVisibleUpdated -> {
                state.copy(reportIssueButton = state.reportIssueButton?.copy(isVisible = action.isVisible))
            }
            is ButtonViewDataAction.CallScreenReportIssueButtonIsEnabledUpdated -> {
                state.copy(reportIssueButton = state.reportIssueButton?.copy(isEnabled = action.isEnabled))
            }
            is ButtonViewDataAction.CallScreenCustomButtonIsVisibleUpdated -> {
                state.copy(callScreenCustomButtonsState = state.callScreenCustomButtonsState.map {
                    if (it.id == action.id) {
                        it.copy(isVisible = action.isVisible)
                    } else {
                        it
                    }
                })
            }
            is ButtonViewDataAction.CallScreenCustomButtonIsEnabledUpdated -> {
                state.copy(callScreenCustomButtonsState = state.callScreenCustomButtonsState.map {
                    if (it.id == action.id) {
                        it.copy(isEnabled = action.isEnabled)
                    } else {
                        it
                    }
                })
            }
            else -> state
        }
    }
}
