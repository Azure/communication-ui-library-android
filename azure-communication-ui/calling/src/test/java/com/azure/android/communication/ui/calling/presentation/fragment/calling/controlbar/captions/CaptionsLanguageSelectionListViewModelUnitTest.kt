// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.calling.controlbar.captions

import com.azure.android.communication.ui.calling.ACSBaseTestCoroutine
import com.azure.android.communication.ui.calling.redux.AppStore
import com.azure.android.communication.ui.calling.redux.action.CaptionsAction
import com.azure.android.communication.ui.calling.redux.action.NavigationAction
import com.azure.android.communication.ui.calling.redux.state.CaptionsState
import com.azure.android.communication.ui.calling.redux.state.NavigationState
import com.azure.android.communication.ui.calling.redux.state.NavigationStatus
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
        viewModel = CaptionsLanguageSelectionListViewModel(store::dispatch)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun captionsLanguageSelectionListViewModel_when_init_shouldSetInitialState() = runScopedTest {
        // Arrange
        val captionsState = CaptionsState(
            captionLanguage = "en",
            supportedCaptionLanguages = listOf("en", "fr"),
            spokenLanguage = "",
            supportedSpokenLanguages = emptyList()
        )
        val navigationState = NavigationState(
            navigationState = NavigationStatus.IN_CALL,
            showSupportedCaptionLanguagesSelections = true,
            showSupportedSpokenLanguagesSelection = false,
        )
        val visibilityState = VisibilityState(VisibilityStatus.VISIBLE)
        // Act
        viewModel.init(captionsState, visibilityState, navigationState)

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
            captionLanguage = "en",
            supportedCaptionLanguages = listOf("en", "fr"),
            spokenLanguage = "",
            supportedSpokenLanguages = emptyList()
        )
        val navigationState = NavigationState(
            navigationState = NavigationStatus.IN_CALL,
            showSupportedCaptionLanguagesSelections = true,
            showSupportedSpokenLanguagesSelection = false,
        )
        val visibilityState = VisibilityState(VisibilityStatus.VISIBLE)

        viewModel.init(initialCaptionsState, visibilityState, navigationState)

        val updatedCaptionsState = CaptionsState(
            captionLanguage = "",
            supportedCaptionLanguages = emptyList(),
            spokenLanguage = "es",
            supportedSpokenLanguages = listOf("es", "de")
        )
        val updatedNavigationState = NavigationState(
            navigationState = NavigationStatus.IN_CALL,
            showSupportedCaptionLanguagesSelections = false,
            showSupportedSpokenLanguagesSelection = true,
        )
        val updatedVisibilityState = VisibilityState(VisibilityStatus.VISIBLE)

        // Act
        viewModel.update(updatedCaptionsState, updatedVisibilityState, updatedNavigationState)

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
        verify(store).dispatch(argThat { action -> action is NavigationAction.HideSupportedLanguagesOptions })
        verify(store).dispatch(argThat { action -> action is NavigationAction.CloseCaptionsOptions })
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun captionsLanguageSelectionListViewModel_when_setActiveLanguageAndCaptionLanguageSelected_shouldDispatchCorrectActions() = runScopedTest {
        // Arrange
        val captionsState = CaptionsState(
            captionLanguage = "en",
            supportedCaptionLanguages = listOf("en", "fr"),
            spokenLanguage = "",
            supportedSpokenLanguages = emptyList()
        )
        val updatedNavigationState = NavigationState(
            navigationState = NavigationStatus.IN_CALL,
            showSupportedCaptionLanguagesSelections = true,
            showSupportedSpokenLanguagesSelection = false,
        )
        val visibilityState = VisibilityState(VisibilityStatus.VISIBLE)

        viewModel.init(captionsState, visibilityState, updatedNavigationState)

        // Act
        viewModel.setActiveLanguage("fr")

        // Assert
        verify(store).dispatch(argThat { action -> action is CaptionsAction.SetCaptionLanguageRequested && action.language == "fr" })
        verify(store).dispatch(argThat { action -> action is NavigationAction.HideSupportedLanguagesOptions })
        verify(store).dispatch(argThat { action -> action is NavigationAction.CloseCaptionsOptions })
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun captionsLanguageSelectionListViewModel_when_setActiveLanguageAndSpokenLanguageSelected_shouldDispatchCorrectActions() = runScopedTest {
        // Arrange
        val captionsState = CaptionsState(
            captionLanguage = "",
            supportedCaptionLanguages = emptyList(),
            spokenLanguage = "en",
            supportedSpokenLanguages = listOf("en", "fr")
        )
        val updatedNavigationState = NavigationState(
            navigationState = NavigationStatus.IN_CALL,
            showSupportedCaptionLanguagesSelections = false,
            showSupportedSpokenLanguagesSelection = true,
        )
        val visibilityState = VisibilityState(VisibilityStatus.VISIBLE)

        viewModel.init(captionsState, visibilityState, updatedNavigationState)

        // Act
        viewModel.setActiveLanguage("fr")

        // Assert
        verify(store).dispatch(argThat { action -> action is CaptionsAction.SetSpokenLanguageRequested && action.language == "fr" })
        verify(store).dispatch(argThat { action -> action is NavigationAction.HideSupportedLanguagesOptions })
        verify(store).dispatch(argThat { action -> action is NavigationAction.CloseCaptionsOptions })
    }
}
