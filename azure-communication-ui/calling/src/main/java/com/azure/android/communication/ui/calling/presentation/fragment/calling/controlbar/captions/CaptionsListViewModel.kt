// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.calling.controlbar.more.captions

import com.azure.android.communication.ui.calling.redux.Store
import com.azure.android.communication.ui.calling.redux.action.CaptionsAction
import com.azure.android.communication.ui.calling.redux.state.CallingStatus
import com.azure.android.communication.ui.calling.redux.state.CaptionsState
import com.azure.android.communication.ui.calling.redux.state.ReduxState
import kotlinx.coroutines.flow.MutableStateFlow

internal class CaptionsListViewModel(private val store: Store<ReduxState>) {

    val displayStateFlow = MutableStateFlow(false)
    val activeSpokenLanguageStateFlow = MutableStateFlow("")
    val activeCaptionLanguageStateFlow = MutableStateFlow("")
    val isCaptionsEnabledStateFlow = MutableStateFlow(false)
    val isTranscriptionEnabledStateFlow = MutableStateFlow(false)
    val isCaptionsActiveStateFlow = MutableStateFlow(false)
    val canTurnOnCaptionsStateFlow = MutableStateFlow(false)

    fun init(captionsState: CaptionsState, callingStatus: CallingStatus) {
        updateListView(captionsState, callingStatus)
    }

    fun update(captionsState: CaptionsState, callingStatus: CallingStatus) {
        updateListView(captionsState, callingStatus)
    }

    private fun updateListView(captionsState: CaptionsState, callingStatus: CallingStatus) {
        activeCaptionLanguageStateFlow.value = captionsState.activeCaptionLanguage
        activeSpokenLanguageStateFlow.value = captionsState.activeSpokenLanguage
        isCaptionsEnabledStateFlow.value = captionsState.isEnabled
        isTranscriptionEnabledStateFlow.value = captionsState.isTranslationSupported
        isCaptionsActiveStateFlow.value = captionsState.isStarted
        canTurnOnCaptionsStateFlow.value = callingStatus == CallingStatus.CONNECTED
        displayStateFlow.value = captionsState.showCaptionsOptions
    }

    fun close() {
        store.dispatch(CaptionsAction.CloseCaptionsOptions())
    }

    fun toggleCaptions(isChecked: Boolean) {
        if (!isChecked) {
            store.dispatch(CaptionsAction.StopRequested())
        } else {
            store.dispatch(CaptionsAction.StartRequested(activeSpokenLanguageStateFlow.value))
        }
        close()
    }

    fun openCaptionLanguageSelection() {
        store.dispatch(CaptionsAction.ShowSupportedCaptionLanguagesOptions())
        close()
    }

    fun openSpokenLanguageSelection() {
        store.dispatch(CaptionsAction.ShowSupportedSpokenLanguagesOptions())
        close()
    }
}
