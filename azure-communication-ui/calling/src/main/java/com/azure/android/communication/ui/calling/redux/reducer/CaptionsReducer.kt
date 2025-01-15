// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.redux.reducer

import com.azure.android.communication.ui.calling.redux.action.Action
import com.azure.android.communication.ui.calling.redux.action.CaptionsAction
import com.azure.android.communication.ui.calling.redux.state.CaptionsState
import com.azure.android.communication.ui.calling.redux.state.CaptionsStatus

internal interface CaptionsReducer : Reducer<CaptionsState>

internal class CaptionsReducerImpl : CaptionsReducer {
    override fun reduce(state: CaptionsState, action: Action): CaptionsState {
        return when (action) {
            is CaptionsAction.UpdateStatus -> {
                state.copy(status = action.status)
            }
            is CaptionsAction.StartRequested -> {
                state.copy(status = CaptionsStatus.START_REQUESTED)
            }
            is CaptionsAction.StopRequested -> {
                state.copy(status = CaptionsStatus.STOP_REQUESTED)
            }
            is CaptionsAction.SpokenLanguageChanged -> {
                state.copy(spokenLanguage = action.language)
            }
            is CaptionsAction.CaptionLanguageChanged -> {
                state.copy(captionLanguage = action.language)
            }
            is CaptionsAction.IsTranslationSupportedChanged -> {
                state.copy(isTranslationSupported = action.isSupported)
            }
            is CaptionsAction.SupportedSpokenLanguagesChanged -> {
                state.copy(supportedSpokenLanguages = action.languages)
            }
            is CaptionsAction.SupportedCaptionLanguagesChanged -> {
                state.copy(supportedCaptionLanguages = action.languages)
            }
            is CaptionsAction.TypeChanged -> {
                state.copy(captionsType = action.type)
            }
            else -> {
                state
            }
        }
    }
}
