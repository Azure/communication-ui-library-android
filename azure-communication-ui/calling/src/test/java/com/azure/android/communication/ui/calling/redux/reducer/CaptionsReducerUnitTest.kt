// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.redux.reducer

import com.azure.android.communication.ui.calling.models.CallCompositeCaptionsType
import com.azure.android.communication.ui.calling.redux.action.CaptionsAction
import com.azure.android.communication.ui.calling.redux.state.CaptionsState
import com.azure.android.communication.ui.calling.redux.state.CaptionsStatus
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
        val oldState = CaptionsState(isCaptionsUIEnabled = false, status = CaptionsStatus.NONE)
        val action = CaptionsAction.UpdateStatus(CaptionsStatus.STARTED)

        // act
        val newState = reducer.reduce(oldState, action)

        // assert
        Assert.assertEquals(CaptionsStatus.STARTED, newState.status)
    }

    @Test
    fun captionsReducer_reduce_when_actionStopped_then_changeStateToStopped() {
        // arrange
        val reducer = CaptionsReducerImpl()
        val oldState = CaptionsState(isCaptionsUIEnabled = true, status = CaptionsStatus.NONE)
        val action = CaptionsAction.UpdateStatus(CaptionsStatus.STOPPED)

        // act
        val newState = reducer.reduce(oldState, action)

        // assert
        Assert.assertEquals(CaptionsStatus.STOPPED, newState.status)
    }

    @Test
    fun captionsReducer_reduce_when_actionSpokenLanguageChanged_then_changeStateToNewLanguage() {
        // arrange
        val reducer = CaptionsReducerImpl()
        val oldState = CaptionsState(spokenLanguage = "en")
        val action = CaptionsAction.SpokenLanguageChanged(language = "fr")

        // act
        val newState = reducer.reduce(oldState, action)

        // assert
        Assert.assertEquals("fr", newState.spokenLanguage)
    }

    @Test
    fun captionsReducer_reduce_when_actionCaptionLanguageChanged_then_changeStateToNewLanguage() {
        // arrange
        val reducer = CaptionsReducerImpl()
        val oldState = CaptionsState(captionLanguage = "en")
        val action = CaptionsAction.CaptionLanguageChanged(language = "es")

        // act
        val newState = reducer.reduce(oldState, action)

        // assert
        Assert.assertEquals("es", newState.captionLanguage)
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
        val oldState = CaptionsState(captionsType = CallCompositeCaptionsType.TEAMS)
        val action = CaptionsAction.TypeChanged(type = CallCompositeCaptionsType.COMMUNICATION)

        // act
        val newState = reducer.reduce(oldState, action)

        // assert
        Assert.assertEquals(CallCompositeCaptionsType.COMMUNICATION, newState.captionsType)
    }
}
