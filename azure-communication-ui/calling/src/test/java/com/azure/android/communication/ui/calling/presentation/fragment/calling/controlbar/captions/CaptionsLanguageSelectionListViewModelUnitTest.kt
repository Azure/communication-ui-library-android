// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.calling.controlbar.captions

import com.azure.android.communication.ui.calling.ACSBaseTestCoroutine
import com.azure.android.communication.ui.calling.redux.AppStore
import com.azure.android.communication.ui.calling.redux.action.CaptionsAction
import com.azure.android.communication.ui.calling.redux.state.CaptionsState
import com.azure.android.communication.ui.calling.redux.state.ReduxState
import com.azure.android.communication.ui.calling.redux.state.VisibilityState
import com.azure.android.communication.ui.calling.redux.state.VisibilityStatus
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.argThat
import org.mockito.kotlin.mock

@RunWith(MockitoJUnitRunner::class)
internal class CaptionsLanguageSelectionListViewModelUnitTest : ACSBaseTestCoroutine() {
    private lateinit var store: AppStore<ReduxState>
    private lateinit var viewModel: CaptionsLanguageSelectionListViewModel

    @Before
    fun setUp() {
        store = mock<AppStore<ReduxState>> {}
        `when`(store.dispatch(any())).then { }
        viewModel = CaptionsLanguageSelectionListViewModel(store)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun captionsLanguageSelectionListViewModel_when_init_shouldSetInitialState() = runScopedTest {
        // Arrange
        val captionsState = CaptionsState(
            showSupportedCaptionLanguagesSelections = true,
            captionLanguage = "en",
            supportedCaptionLanguages = listOf("en", "fr"),
            showSupportedSpokenLanguagesSelection = false,
            spokenLanguage = "",
            supportedSpokenLanguages = emptyList()
        )
        val visibilityState = VisibilityState(VisibilityStatus.VISIBLE)
        // Act
        viewModel.init(captionsState, visibilityState)

        // Assert
        assertEquals(LanguageSelectionType.CAPTION, viewModel.languageSelectionTypeStateFlow)
        assertEquals("en", viewModel.updateActiveLanguageStateFlow.value)
        assertEquals(listOf("en", "fr"), viewModel.languagesListStateFlow.value)
        assertTrue(viewModel.displayLanguageListStateFlow.value)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun captionsLanguageSelectionListViewModel_when_update_shouldUpdateState() = runScopedTest {
        // Arrange
        val initialCaptionsState = CaptionsState(
            showSupportedCaptionLanguagesSelections = true,
            captionLanguage = "en",
            supportedCaptionLanguages = listOf("en", "fr"),
            showSupportedSpokenLanguagesSelection = false,
            spokenLanguage = "",
            supportedSpokenLanguages = emptyList()
        )
        val visibilityState = VisibilityState(VisibilityStatus.VISIBLE)

        viewModel.init(initialCaptionsState, visibilityState)

        val updatedCaptionsState = CaptionsState(
            showSupportedCaptionLanguagesSelections = false,
            captionLanguage = "",
            supportedCaptionLanguages = emptyList(),
            showSupportedSpokenLanguagesSelection = true,
            spokenLanguage = "es",
            supportedSpokenLanguages = listOf("es", "de")
        )
        val updatedVisibilityState = VisibilityState(VisibilityStatus.VISIBLE)

        // Act
        viewModel.update(updatedCaptionsState, updatedVisibilityState)

        // Assert
        assertEquals(LanguageSelectionType.SPOKEN, viewModel.languageSelectionTypeStateFlow)
        assertEquals("es", viewModel.updateActiveLanguageStateFlow.value)
        assertEquals(listOf("es", "de"), viewModel.languagesListStateFlow.value)
        assertTrue(viewModel.displayLanguageListStateFlow.value)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun captionsLanguageSelectionListViewModel_when_close_shouldDispatchCorrectActions() = runScopedTest {
        // Act
        viewModel.close()

        // Assert
        verify(store).dispatch(argThat { action -> action is CaptionsAction.HideSupportedLanguagesOptions })
        verify(store).dispatch(argThat { action -> action is CaptionsAction.CloseCaptionsOptions })
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun captionsLanguageSelectionListViewModel_when_setActiveLanguageAndCaptionLanguageSelected_shouldDispatchCorrectActions() = runScopedTest {
        // Arrange
        val captionsState = CaptionsState(
            showSupportedCaptionLanguagesSelections = true,
            captionLanguage = "en",
            supportedCaptionLanguages = listOf("en", "fr"),
            showSupportedSpokenLanguagesSelection = false,
            spokenLanguage = "",
            supportedSpokenLanguages = emptyList()
        )
        val visibilityState = VisibilityState(VisibilityStatus.VISIBLE)

        viewModel.init(captionsState, visibilityState)

        // Act
        viewModel.setActiveLanguage("fr")

        // Assert
        verify(store).dispatch(argThat { action -> action is CaptionsAction.SetCaptionLanguageRequested && action.language == "fr" })
        verify(store).dispatch(argThat { action -> action is CaptionsAction.HideSupportedLanguagesOptions })
        verify(store).dispatch(argThat { action -> action is CaptionsAction.CloseCaptionsOptions })
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun captionsLanguageSelectionListViewModel_when_setActiveLanguageAndSpokenLanguageSelected_shouldDispatchCorrectActions() = runScopedTest {
        // Arrange
        val captionsState = CaptionsState(
            showSupportedCaptionLanguagesSelections = false,
            captionLanguage = "",
            supportedCaptionLanguages = emptyList(),
            showSupportedSpokenLanguagesSelection = true,
            spokenLanguage = "en",
            supportedSpokenLanguages = listOf("en", "fr")
        )
        val visibilityState = VisibilityState(VisibilityStatus.VISIBLE)

        viewModel.init(captionsState, visibilityState)

        // Act
        viewModel.setActiveLanguage("fr")

        // Assert
        verify(store).dispatch(argThat { action -> action is CaptionsAction.SetSpokenLanguageRequested && action.language == "fr" })
        verify(store).dispatch(argThat { action -> action is CaptionsAction.HideSupportedLanguagesOptions })
        verify(store).dispatch(argThat { action -> action is CaptionsAction.CloseCaptionsOptions })
    }
}
