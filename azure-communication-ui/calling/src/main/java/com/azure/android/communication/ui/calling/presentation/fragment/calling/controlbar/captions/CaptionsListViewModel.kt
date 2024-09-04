// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.calling.controlbar.captions

import com.azure.android.communication.ui.calling.models.CallCompositeButtonViewData
import com.azure.android.communication.ui.calling.redux.Store
import com.azure.android.communication.ui.calling.redux.action.CaptionsAction
import com.azure.android.communication.ui.calling.redux.state.CallingStatus
import com.azure.android.communication.ui.calling.redux.state.CaptionsState
import com.azure.android.communication.ui.calling.redux.state.CaptionsStatus
import com.azure.android.communication.ui.calling.redux.state.ReduxState
import com.azure.android.communication.ui.calling.redux.state.VisibilityState
import com.azure.android.communication.ui.calling.redux.state.VisibilityStatus
import kotlinx.coroutines.flow.MutableStateFlow

internal class CaptionsListViewModel(
    private val store: Store<ReduxState>,
    val liveCaptionsToggleButton: CallCompositeButtonViewData?,
    val spokenLanguageButtonOptions: CallCompositeButtonViewData?,
    val captionsLanguageButtonOptions: CallCompositeButtonViewData?,
) {

    val displayStateFlow = MutableStateFlow(false)
    val activeSpokenLanguageStateFlow = MutableStateFlow<String?>(null)
    val activeCaptionLanguageStateFlow = MutableStateFlow<String?>(null)
    val isCaptionsEnabledStateFlow = MutableStateFlow(false)
    val isTranscriptionEnabledStateFlow = MutableStateFlow(false)
    val isCaptionsActiveStateFlow = MutableStateFlow(false)
    val canTurnOnCaptionsStateFlow = MutableStateFlow(false)

    fun init(captionsState: CaptionsState, callingStatus: CallingStatus) {
        updateListView(captionsState, callingStatus, store.getCurrentState().visibilityState.status)
    }

    fun update(
        captionsState: CaptionsState,
        callingStatus: CallingStatus,
        visibilityState: VisibilityState
    ) {
        updateListView(captionsState, callingStatus, visibilityState.status)
    }

    private fun updateListView(
        captionsState: CaptionsState,
        callingStatus: CallingStatus,
        visibilityStatus: VisibilityStatus
    ) {
        activeCaptionLanguageStateFlow.value = captionsState.captionLanguage
        activeSpokenLanguageStateFlow.value = captionsState.spokenLanguage
        isCaptionsEnabledStateFlow.value = captionsState.isCaptionsUIEnabled
        isTranscriptionEnabledStateFlow.value = captionsState.isTranslationSupported
        isCaptionsActiveStateFlow.value = captionsState.status == CaptionsStatus.STARTED
        canTurnOnCaptionsStateFlow.value = callingStatus == CallingStatus.CONNECTED
        displayStateFlow.value = captionsState.showCaptionsToggleUI && visibilityStatus == VisibilityStatus.VISIBLE
    }

    fun close() {
        store.dispatch(CaptionsAction.CloseCaptionsOptions())
    }

    fun toggleCaptions(isChecked: Boolean) {
        if (!isChecked) {
            store.dispatch(CaptionsAction.StopRequested())
            close()
        } else {
            store.dispatch(CaptionsAction.StartRequested(activeSpokenLanguageStateFlow.value))
        }
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
