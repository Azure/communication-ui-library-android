// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.redux.reducer

import com.azure.android.communication.ui.calling.models.CallCompositeCaptionsType
import com.azure.android.communication.ui.calling.redux.action.CaptionsAction
import com.azure.android.communication.ui.calling.redux.state.CaptionsState
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
internal class CaptionsReducerUnitTest {
    @Test
    fun captionsReducer_reduce_when_actionStarted_then_changeStateToStarted() {

        // arrange
        val reducer = CaptionsReducerImpl()
        val oldState = CaptionsState(isCaptionsUIEnabled = false, isCaptionsStarted = false)
        val action = CaptionsAction.Started()

        // act
        val newState = reducer.reduce(oldState, action)

        // assert
        Assert.assertEquals(true, newState.isCaptionsStarted)
    }

    @Test
    fun captionsReducer_reduce_when_actionStopped_then_changeStateToStopped() {
        // arrange
        val reducer = CaptionsReducerImpl()
        val oldState = CaptionsState(isCaptionsStarted = true)
        val action = CaptionsAction.Stopped()

        // act
        val newState = reducer.reduce(oldState, action)

        // assert
        Assert.assertEquals(false, newState.isCaptionsStarted)
    }

    @Test
    fun captionsReducer_reduce_when_actionSpokenLanguageChanged_then_changeStateToNewLanguage() {
        // arrange
        val reducer = CaptionsReducerImpl()
        val oldState = CaptionsState(activeSpokenLanguage = "en")
        val action = CaptionsAction.SpokenLanguageChanged(language = "fr")

        // act
        val newState = reducer.reduce(oldState, action)

        // assert
        Assert.assertEquals("fr", newState.activeSpokenLanguage)
    }

    @Test
    fun captionsReducer_reduce_when_actionCaptionLanguageChanged_then_changeStateToNewLanguage() {
        // arrange
        val reducer = CaptionsReducerImpl()
        val oldState = CaptionsState(activeCaptionLanguage = "en")
        val action = CaptionsAction.CaptionLanguageChanged(language = "es")

        // act
        val newState = reducer.reduce(oldState, action)

        // assert
        Assert.assertEquals("es", newState.activeCaptionLanguage)
    }

    @Test
    fun captionsReducer_reduce_when_actionIsTranslationSupportedChanged_then_changeStateToNewSupport() {
        // arrange
        val reducer = CaptionsReducerImpl()
        val oldState = CaptionsState(isTranslationSupported = false)
        val action = CaptionsAction.IsTranslationSupportedChanged(isSupported = true)

        // act
        val newState = reducer.reduce(oldState, action)

        // assert
        Assert.assertEquals(true, newState.isTranslationSupported)
    }

    @Test
    fun captionsReducer_reduce_when_actionSupportedSpokenLanguagesChanged_then_changeStateToNewLanguages() {
        // arrange
        val reducer = CaptionsReducerImpl()
        val oldState = CaptionsState(supportedSpokenLanguages = emptyList())
        val action = CaptionsAction.SupportedSpokenLanguagesChanged(languages = listOf("en", "fr"))

        // act
        val newState = reducer.reduce(oldState, action)

        // assert
        Assert.assertEquals(listOf("en", "fr"), newState.supportedSpokenLanguages)
    }

    @Test
    fun captionsReducer_reduce_when_actionSupportedCaptionLanguagesChanged_then_changeStateToNewLanguages() {
        // arrange
        val reducer = CaptionsReducerImpl()
        val oldState = CaptionsState(supportedCaptionLanguages = emptyList())
        val action = CaptionsAction.SupportedCaptionLanguagesChanged(languages = listOf("en", "es"))

        // act
        val newState = reducer.reduce(oldState, action)

        // assert
        Assert.assertEquals(listOf("en", "es"), newState.supportedCaptionLanguages)
    }

    @Test
    fun captionsReducer_reduce_when_actionTypeChanged_then_changeStateToNewType() {
        // arrange
        val reducer = CaptionsReducerImpl()
        val oldState = CaptionsState(activeType = CallCompositeCaptionsType.TEAMS)
        val action = CaptionsAction.TypeChanged(type = CallCompositeCaptionsType.COMMUNICATION)

        // act
        val newState = reducer.reduce(oldState, action)

        // assert
        Assert.assertEquals(CallCompositeCaptionsType.COMMUNICATION, newState.activeType)
    }

    @Test
    fun captionsReducer_reduce_when_actionShowCaptionsOptions_then_changeStateToShowCaptionsToggleUI() {
        // arrange
        val reducer = CaptionsReducerImpl()
        val oldState = CaptionsState(showCaptionsToggleUI = false)
        val action = CaptionsAction.ShowCaptionsOptions()

        // act
        val newState = reducer.reduce(oldState, action)

        // assert
        Assert.assertEquals(true, newState.showCaptionsToggleUI)
    }

    @Test
    fun captionsReducer_reduce_when_actionCloseCaptionsOptions_then_changeStateToCloseCaptionsToggleUI() {
        // arrange
        val reducer = CaptionsReducerImpl()
        val oldState = CaptionsState(showCaptionsToggleUI = true)
        val action = CaptionsAction.CloseCaptionsOptions()

        // act
        val newState = reducer.reduce(oldState, action)

        // assert
        Assert.assertEquals(false, newState.showCaptionsToggleUI)
    }

    @Test
    fun captionsReducer_reduce_when_actionShowSupportedSpokenLanguagesOptions_then_changeStateToShowSupportedSpokenLanguagesSelection() {
        // arrange
        val reducer = CaptionsReducerImpl()
        val oldState = CaptionsState(showSupportedSpokenLanguagesSelection = false)
        val action = CaptionsAction.ShowSupportedSpokenLanguagesOptions()

        // act
        val newState = reducer.reduce(oldState, action)

        // assert
        Assert.assertEquals(true, newState.showSupportedSpokenLanguagesSelection)
    }

    @Test
    fun captionsReducer_reduce_when_actionShowSupportedCaptionLanguagesOptions_then_changeStateToShowSupportedCaptionLanguagesSelections() {
        // arrange
        val reducer = CaptionsReducerImpl()
        val oldState = CaptionsState(showSupportedCaptionLanguagesSelections = false)
        val action = CaptionsAction.ShowSupportedCaptionLanguagesOptions()

        // act
        val newState = reducer.reduce(oldState, action)

        // assert
        Assert.assertEquals(true, newState.showSupportedCaptionLanguagesSelections)
    }

    @Test
    fun captionsReducer_reduce_when_actionHideSupportedLanguagesOptions_then_changeStateToHideSupportedLanguagesSelections() {
        // arrange
        val reducer = CaptionsReducerImpl()
        val oldState = CaptionsState(showSupportedSpokenLanguagesSelection = true, showSupportedCaptionLanguagesSelections = true)
        val action = CaptionsAction.HideSupportedLanguagesOptions()

        // act
        val newState = reducer.reduce(oldState, action)

        // assert
        Assert.assertEquals(false, newState.showSupportedSpokenLanguagesSelection)
        Assert.assertEquals(false, newState.showSupportedCaptionLanguagesSelections)
    }
}
