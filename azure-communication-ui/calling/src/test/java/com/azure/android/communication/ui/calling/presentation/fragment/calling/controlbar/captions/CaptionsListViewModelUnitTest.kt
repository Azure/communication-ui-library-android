// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.calling.controlbar.captions

import com.azure.android.communication.ui.calling.ACSBaseTestCoroutine
import com.azure.android.communication.ui.calling.redux.AppStore
import com.azure.android.communication.ui.calling.redux.action.CaptionsAction
import com.azure.android.communication.ui.calling.redux.state.CallingStatus
import com.azure.android.communication.ui.calling.redux.state.CaptionsState
import com.azure.android.communication.ui.calling.redux.state.CaptionsStatus
import com.azure.android.communication.ui.calling.redux.state.ReduxState
import com.azure.android.communication.ui.calling.redux.state.VisibilityState
import com.azure.android.communication.ui.calling.redux.state.VisibilityStatus
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
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
internal class CaptionsListViewModelUnitTest : ACSBaseTestCoroutine() {
    private lateinit var store: AppStore<ReduxState>
    private lateinit var viewModel: CaptionsListViewModel

    @Before
    fun setUp() {
        store = mock<AppStore<ReduxState>> {}
        `when`(store.dispatch(any())).then { }
        viewModel = CaptionsListViewModel(
            store::dispatch,
            liveCaptionsToggleButton = null,
            spokenLanguageButton = null,
            captionsLanguageButton = null,
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun captionsListViewModelUnitTest_when_init_shouldSetInitialState() = runScopedTest {
        // Arrange
        val captionsState = CaptionsState(
            captionLanguage = "en",
            spokenLanguage = "en",
            isCaptionsUIEnabled = true,
            isTranslationSupported = true,
            status = CaptionsStatus.STARTED,
            showCaptionsToggleUI = true
        )
        val callingStatus = CallingStatus.CONNECTED
        val visibilityState = VisibilityState(VisibilityStatus.VISIBLE)

        // Act
        viewModel.init(captionsState, callingStatus, visibilityState)

        // Assert
        assertEquals("en", viewModel.activeCaptionLanguageStateFlow.value)
        assertEquals("en", viewModel.activeSpokenLanguageStateFlow.value)
        assertTrue(viewModel.isCaptionsEnabledStateFlow.value)
        assertTrue(viewModel.isTranscriptionEnabledStateFlow.value)
        assertTrue(viewModel.isCaptionsActiveStateFlow.value)
        assertTrue(viewModel.canTurnOnCaptionsStateFlow.value)
        assertTrue(viewModel.displayStateFlow.value)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun captionsListViewModelUnitTest_when_update_shouldUpdateState() = runScopedTest {
        // Arrange
        val initialCaptionsState = CaptionsState(
            captionLanguage = "en",
            spokenLanguage = "en",
            isCaptionsUIEnabled = true,
            isTranslationSupported = true,
            status = CaptionsStatus.STARTED,
            showCaptionsToggleUI = true
        )
        val initialCallingStatus = CallingStatus.CONNECTED
        val visibilityState = VisibilityState(VisibilityStatus.VISIBLE)

        viewModel.init(initialCaptionsState, initialCallingStatus, visibilityState)

        val updatedCaptionsState = CaptionsState(
            captionLanguage = "fr",
            spokenLanguage = "fr",
            isCaptionsUIEnabled = false,
            isTranslationSupported = false,
            status = CaptionsStatus.NONE,
            showCaptionsToggleUI = false
        )
        val updatedCallingStatus = CallingStatus.DISCONNECTED
        val updatedVisibilityState = VisibilityState(VisibilityStatus.PIP_MODE_ENTERED)

        // Act
        viewModel.update(updatedCaptionsState, updatedCallingStatus, updatedVisibilityState)

        // Assert
        assertEquals("fr", viewModel.activeCaptionLanguageStateFlow.value)
        assertEquals("fr", viewModel.activeSpokenLanguageStateFlow.value)
        assertFalse(viewModel.isCaptionsEnabledStateFlow.value)
        assertFalse(viewModel.isTranscriptionEnabledStateFlow.value)
        assertFalse(viewModel.isCaptionsActiveStateFlow.value)
        assertFalse(viewModel.canTurnOnCaptionsStateFlow.value)
        assertFalse(viewModel.displayStateFlow.value)
    }

    @Test
    fun captionsListViewModelUnitTest_when_toggleCaptions_shouldDispatchCorrectAction() {
        // Arrange
        val captionsState = CaptionsState(
            captionLanguage = "en",
            spokenLanguage = "en",
            isCaptionsUIEnabled = true,
            isTranslationSupported = true,
            status = CaptionsStatus.STARTED,
            showCaptionsToggleUI = true
        )
        val callingStatus = CallingStatus.CONNECTED
        val visibilityState = VisibilityState(VisibilityStatus.VISIBLE)

        viewModel.init(captionsState, callingStatus, visibilityState)

        // Act
        viewModel.toggleCaptions(false)

        // Assert
        verify(store).dispatch(
            argThat { action ->
                action is CaptionsAction.StopRequested
            }
        )
        verify(store).dispatch(
            argThat { action ->
                action is CaptionsAction.CloseCaptionsOptions
            }
        )

        // Act
        viewModel.toggleCaptions(true)

        // Assert
        verify(store).dispatch(
            argThat { action ->
                action is CaptionsAction.StartRequested
            }
        )
    }

    @Test
    fun captionsListViewModelUnitTest_when_openCaptionLanguageSelection_shouldDispatchCorrectAction() {
        // Act
        viewModel.openCaptionLanguageSelection()

        // Assert
        verify(store).dispatch(
            argThat { action ->
                action is CaptionsAction.ShowSupportedCaptionLanguagesOptions
            }
        )
        verify(store).dispatch(
            argThat { action ->
                action is CaptionsAction.CloseCaptionsOptions
            }
        )
    }

    @Test
    fun captionsListViewModelUnitTest_when_openSpokenLanguageSelection_shouldDispatchCorrectAction() {
        // Act
        viewModel.openSpokenLanguageSelection()

        // Assert
        verify(store).dispatch(
            argThat { action ->
                action is CaptionsAction.ShowSupportedSpokenLanguagesOptions
            }
        )
        verify(store).dispatch(
            argThat { action ->
                action is CaptionsAction.CloseCaptionsOptions
            }
        )
    }
}
