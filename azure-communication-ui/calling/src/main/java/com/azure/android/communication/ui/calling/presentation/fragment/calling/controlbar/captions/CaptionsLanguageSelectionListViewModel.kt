// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.calling.controlbar.captions

import com.azure.android.communication.ui.calling.redux.Dispatch
import com.azure.android.communication.ui.calling.redux.action.CaptionsAction
import com.azure.android.communication.ui.calling.redux.action.NavigationAction
import com.azure.android.communication.ui.calling.redux.state.CaptionsState
import com.azure.android.communication.ui.calling.redux.state.NavigationState
import com.azure.android.communication.ui.calling.redux.state.VisibilityState
import com.azure.android.communication.ui.calling.redux.state.VisibilityStatus
import kotlinx.coroutines.flow.MutableStateFlow

internal enum class LanguageSelectionType {
    CAPTION,
    SPOKEN
}

internal class CaptionsLanguageSelectionListViewModel(
    private val dispatch: Dispatch,
) {

    val displayLanguageListStateFlow = MutableStateFlow(false)
    val updateActiveLanguageStateFlow = MutableStateFlow<String?>(null)
    val languagesListStateFlow = MutableStateFlow(emptyList<String>())
    var languageSelectionTypeStateFlow: LanguageSelectionType? = null

    fun init(
        captionsState: CaptionsState,
        visibilityState: VisibilityState,
        navigationState: NavigationState,
    ) {
        updateListView(captionsState, visibilityState.status, navigationState)
    }

    fun update(
        captionsState: CaptionsState,
        visibilityState: VisibilityState,
        navigationState: NavigationState,
    ) {
        updateListView(captionsState, visibilityState.status, navigationState)
    }

    private fun updateListView(
        captionsState: CaptionsState,
        status: VisibilityStatus,
        navigationState: NavigationState,
    ) {
        if (navigationState.showSupportedCaptionLanguagesSelections) {
            languageSelectionTypeStateFlow = LanguageSelectionType.CAPTION
            updateActiveLanguageStateFlow.value = captionsState.captionLanguage
            languagesListStateFlow.value = captionsState.supportedCaptionLanguages
        } else if (navigationState.showSupportedSpokenLanguagesSelection) {
            languageSelectionTypeStateFlow = LanguageSelectionType.SPOKEN
            updateActiveLanguageStateFlow.value = captionsState.spokenLanguage
            languagesListStateFlow.value = captionsState.supportedSpokenLanguages
        } else {
            languageSelectionTypeStateFlow = null
        }
        displayLanguageListStateFlow.value = languageSelectionTypeStateFlow != null && status == VisibilityStatus.VISIBLE
    }

    fun close() {
        dispatch(NavigationAction.HideSupportedLanguagesOptions())
        dispatch(NavigationAction.CloseCaptionsOptions())
    }

    fun setActiveLanguage(language: String) {
        if (languageSelectionTypeStateFlow == LanguageSelectionType.CAPTION) {
            dispatch(CaptionsAction.SetCaptionLanguageRequested(language))
        } else if (languageSelectionTypeStateFlow == LanguageSelectionType.SPOKEN) {
            dispatch(CaptionsAction.SetSpokenLanguageRequested(language))
        }
        close()
    }
}
