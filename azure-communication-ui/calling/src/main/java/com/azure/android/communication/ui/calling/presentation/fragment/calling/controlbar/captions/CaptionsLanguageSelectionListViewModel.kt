// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.calling.controlbar.captions

import com.azure.android.communication.ui.calling.redux.Store
import com.azure.android.communication.ui.calling.redux.action.CaptionsAction
import com.azure.android.communication.ui.calling.redux.state.CaptionsState
import com.azure.android.communication.ui.calling.redux.state.ReduxState
import com.azure.android.communication.ui.calling.redux.state.VisibilityState
import com.azure.android.communication.ui.calling.redux.state.VisibilityStatus
import kotlinx.coroutines.flow.MutableStateFlow

internal enum class LanguageSelectionType {
    CAPTION,
    SPOKEN
}

internal class CaptionsLanguageSelectionListViewModel(private val store: Store<ReduxState>) {

    val displayLanguageListStateFlow = MutableStateFlow(false)
    val updateActiveLanguageStateFlow = MutableStateFlow<String?>(null)
    val languagesListStateFlow = MutableStateFlow(emptyList<String>())
    var languageSelectionTypeStateFlow: LanguageSelectionType? = null

    fun init(captionsState: CaptionsState) {
        updateListView(captionsState, store.getCurrentState().visibilityState.status)
    }

    fun update(captionsState: CaptionsState, visibilityState: VisibilityState) {
        updateListView(captionsState, visibilityState.status)
    }

    private fun updateListView(captionsState: CaptionsState, status: VisibilityStatus) {
        if (captionsState.showSupportedCaptionLanguagesSelections) {
            languageSelectionTypeStateFlow = LanguageSelectionType.CAPTION
            updateActiveLanguageStateFlow.value = captionsState.captionLanguage
            languagesListStateFlow.value = captionsState.supportedCaptionLanguages
        } else if (captionsState.showSupportedSpokenLanguagesSelection) {
            languageSelectionTypeStateFlow = LanguageSelectionType.SPOKEN
            updateActiveLanguageStateFlow.value = captionsState.spokenLanguage
            languagesListStateFlow.value = captionsState.supportedSpokenLanguages
        } else {
            languageSelectionTypeStateFlow = null
        }
        displayLanguageListStateFlow.value = languageSelectionTypeStateFlow != null && status == VisibilityStatus.VISIBLE
    }

    fun close() {
        store.dispatch(CaptionsAction.HideSupportedLanguagesOptions())
        store.dispatch(CaptionsAction.CloseCaptionsOptions())
    }

    fun setActiveLanguage(language: String) {
        if (languageSelectionTypeStateFlow == LanguageSelectionType.CAPTION) {
            store.dispatch(CaptionsAction.SetCaptionLanguageRequested(language))
        } else if (languageSelectionTypeStateFlow == LanguageSelectionType.SPOKEN) {
            store.dispatch(CaptionsAction.SetSpokenLanguageRequested(language))
        }
        close()
    }
}
