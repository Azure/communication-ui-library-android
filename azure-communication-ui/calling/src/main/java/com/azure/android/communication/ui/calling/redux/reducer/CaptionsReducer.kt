// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.redux.reducer

import com.azure.android.communication.ui.calling.redux.action.Action
import com.azure.android.communication.ui.calling.redux.action.CaptionsAction
import com.azure.android.communication.ui.calling.redux.state.CaptionsState

internal interface CaptionsReducer : Reducer<CaptionsState>

internal class CaptionsReducerImpl : CaptionsReducer {
    override fun reduce(state: CaptionsState, action: Action): CaptionsState {
        return when (action) {
            is CaptionsAction.Started -> {
                state.copy(isStarted = true)
            }
            is CaptionsAction.Stopped -> {
                state.copy(isStarted = false)
            }
            is CaptionsAction.SpokenLanguageChanged -> {
                state.copy(activeSpokenLanguage = action.language)
            }
            is CaptionsAction.CaptionLanguageChanged -> {
                state.copy(activeCaptionLanguage = action.language)
            }
            is CaptionsAction.IsTranslationSupportedChanged -> {
                state.copy(isTranslationSupported = action.isSupported)
            }
            is CaptionsAction.Error -> {
                state.copy(errors = action.errors)
            }
            is CaptionsAction.SupportedSpokenLanguagesChanged -> {
                state.copy(supportedSpokenLanguages = action.languages)
            }
            is CaptionsAction.SupportedCaptionLanguagesChanged -> {
                state.copy(supportedCaptionLanguages = action.languages)
            }
            is CaptionsAction.TypeChanged -> {
                state.copy(activeType = action.type)
            }
            else -> {
                state
            }
        }
    }
}
