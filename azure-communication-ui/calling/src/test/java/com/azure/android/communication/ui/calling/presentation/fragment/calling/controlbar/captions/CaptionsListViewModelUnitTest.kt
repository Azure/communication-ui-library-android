// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.calling.controlbar.captions

import android.content.Context
import com.azure.android.communication.ui.calling.ACSBaseTestCoroutine
import com.azure.android.communication.ui.calling.redux.AppStore
import com.azure.android.communication.ui.calling.redux.action.CaptionsAction
import com.azure.android.communication.ui.calling.redux.action.NavigationAction
import com.azure.android.communication.ui.calling.redux.state.ButtonState
import com.azure.android.communication.ui.calling.redux.state.CallingStatus
import com.azure.android.communication.ui.calling.redux.state.CaptionsState
import com.azure.android.communication.ui.calling.redux.state.CaptionsStatus
import com.azure.android.communication.ui.calling.redux.state.NavigationState
import com.azure.android.communication.ui.calling.redux.state.NavigationStatus
import com.azure.android.communication.ui.calling.redux.state.ReduxState
import com.azure.android.communication.ui.calling.redux.state.RttState
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
            logger = mock(),
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
        )
        val navigationState = NavigationState(
            navigationState = NavigationStatus.IN_CALL,
            showCaptionsToggleUI = true,
        )
        val callingStatus = CallingStatus.CONNECTED
        val visibilityState = VisibilityState(VisibilityStatus.VISIBLE)
        val buttonState = ButtonState()
        val rttState = RttState()

        // Act
        viewModel.init(
            captionsState,
            callingStatus,
            visibilityState,
            buttonState = buttonState,
            navigationState = navigationState,
            rttState = rttState,
        )

        // Assert
        assertEquals("en", viewModel.activeCaptionLanguageStateFlow.value)
        assertEquals("en", viewModel.activeSpokenLanguageStateFlow.value)
        assertTrue(viewModel.isCaptionsEnabledStateFlow.value)
        assertTrue(viewModel.isCaptionsLangButtonVisibleStateFlow.value)
        assertTrue(viewModel.isCaptionsActiveStateFlow.value)
        assertTrue(viewModel.isCaptionsToggleEnabledStateFlow.value)
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
        )
        val navigationState = NavigationState(
            navigationState = NavigationStatus.IN_CALL,
            showCaptionsToggleUI = true,
        )
        val rttState = RttState()
        val initialCallingStatus = CallingStatus.CONNECTED
        val visibilityState = VisibilityState(VisibilityStatus.VISIBLE)
        val buttonState = ButtonState()

        viewModel.init(
            initialCaptionsState,
            initialCallingStatus,
            visibilityState,
            buttonState,
            rttState,
            navigationState,
        )

        val updatedCaptionsState = CaptionsState(
            captionLanguage = "fr",
            spokenLanguage = "fr",
            isCaptionsUIEnabled = false,
            isTranslationSupported = false,
            status = CaptionsStatus.NONE,
        )
        val updatedNavigationState = NavigationState(
            navigationState = NavigationStatus.IN_CALL,
            showCaptionsToggleUI = false
        )
        val updatedCallingStatus = CallingStatus.DISCONNECTED
        val updatedVisibilityState = VisibilityState(VisibilityStatus.PIP_MODE_ENTERED)

        // Act
        viewModel.update(
            updatedCaptionsState,
            updatedCallingStatus,
            updatedVisibilityState,
            buttonState,
            rttState,
            updatedNavigationState,
        )

        // Assert
        assertEquals("fr", viewModel.activeCaptionLanguageStateFlow.value)
        assertEquals("fr", viewModel.activeSpokenLanguageStateFlow.value)
        assertFalse(viewModel.isCaptionsEnabledStateFlow.value)
        assertFalse(viewModel.isCaptionsLangButtonVisibleStateFlow.value)
        assertFalse(viewModel.isCaptionsActiveStateFlow.value)
        assertFalse(viewModel.isCaptionsToggleEnabledStateFlow.value)
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
        )
        val navigationState = NavigationState(
            navigationState = NavigationStatus.IN_CALL,
            showCaptionsToggleUI = true,
        )
        val rttState = RttState()
        val callingStatus = CallingStatus.CONNECTED
        val visibilityState = VisibilityState(VisibilityStatus.VISIBLE)
        val buttonState = ButtonState()

        viewModel.init(
            captionsState,
            callingStatus,
            visibilityState,
            buttonState,
            rttState,
            navigationState,
        )

        // Act
        viewModel.toggleCaptions(context = mock<Context>(), false)

        // Assert
        verify(store).dispatch(
            argThat { action ->
                action is CaptionsAction.StopRequested
            }
        )
        verify(store).dispatch(
            argThat { action ->
                action is NavigationAction.CloseCaptionsOptions
            }
        )

        // Act
        viewModel.toggleCaptions(context = mock<Context>(), true)

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
        viewModel.openCaptionLanguageSelection(context = mock<Context>())

        // Assert
        verify(store).dispatch(
            argThat { action ->
                action is NavigationAction.ShowSupportedCaptionLanguagesOptions
            }
        )
        verify(store).dispatch(
            argThat { action ->
                action is NavigationAction.CloseCaptionsOptions
            }
        )
    }

    @Test
    fun captionsListViewModelUnitTest_when_openSpokenLanguageSelection_shouldDispatchCorrectAction() {
        // Act
        viewModel.openSpokenLanguageSelection(context = mock<Context>())

        // Assert
        verify(store).dispatch(
            argThat { action ->
                action is NavigationAction.ShowSupportedSpokenLanguagesOptions
            }
        )
        verify(store).dispatch(
            argThat { action ->
                action is NavigationAction.CloseCaptionsOptions
            }
        )
    }
}
