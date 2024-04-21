// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.calling.controlbar.more.captions

import com.azure.android.communication.ui.calling.redux.Store
import com.azure.android.communication.ui.calling.redux.action.CaptionsAction
import com.azure.android.communication.ui.calling.redux.state.CaptionsState
import com.azure.android.communication.ui.calling.redux.state.ReduxState
import kotlinx.coroutines.flow.MutableStateFlow

internal enum class LanguageSelectionType {
    CAPTION,
    SPOKEN
}

internal class CaptionsLanguageSelectionListViewModel(private val store: Store<ReduxState>) {

    val displayStateFlow = MutableStateFlow(false)
    val languagesListStateFlow = MutableStateFlow(emptyList<String>())
    var languageSelectionTypeStateFlow: LanguageSelectionType? = null
    var captionsActiveLanguage: String = ""

    fun init(captionsState: CaptionsState) {
        updateListView(captionsState)
    }

    fun update(captionsState: CaptionsState) {
        updateListView(captionsState)
    }

    private fun updateListView(captionsState: CaptionsState) {
        if (captionsState.showSupportedCaptionLanguages) {
            languageSelectionTypeStateFlow = LanguageSelectionType.CAPTION
            languagesListStateFlow.value = captionsState.supportedCaptionLanguages
            captionsActiveLanguage = captionsState.activeCaptionLanguage
        } else if (captionsState.showSupportedSpokenLanguages) {
            languageSelectionTypeStateFlow = LanguageSelectionType.SPOKEN
            languagesListStateFlow.value = captionsState.supportedSpokenLanguages
            captionsActiveLanguage = captionsState.activeSpokenLanguage
        } else {
            languageSelectionTypeStateFlow = null
        }
        displayStateFlow.value = languageSelectionTypeStateFlow != null
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
