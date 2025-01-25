// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.redux.reducer

import com.azure.android.communication.ui.calling.redux.action.CallingAction
import com.azure.android.communication.ui.calling.redux.action.NavigationAction
import com.azure.android.communication.ui.calling.redux.state.CallingStatus
import com.azure.android.communication.ui.calling.redux.state.NavigationState
import com.azure.android.communication.ui.calling.redux.state.NavigationStatus
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
internal class NavigationReducerUnitTest {

    @Test
    fun navigationReducer_reduce_when_actionCallLaunched_then_changeNavigationState() {
        // arrange
        val reducer = NavigationReducerImpl()
        val previousState = NavigationState(NavigationStatus.SETUP)
        val action = NavigationAction.CallLaunched()

        // act
        val newState = reducer.reduce(previousState, action)

        // assert
        Assert.assertEquals(NavigationStatus.IN_CALL, newState.navigationState)
    }

    @Test
    fun navigationReducer_reduce_when_actionSetupLaunched_then_changeNavigationState() {
        // arrange
        val reducer = NavigationReducerImpl()
        val previousState = NavigationState(NavigationStatus.IN_CALL)
        val action = NavigationAction.SetupLaunched()

        // act
        val newState = reducer.reduce(previousState, action)

        // assert
        Assert.assertEquals(NavigationStatus.SETUP, newState.navigationState)
    }

    @Test
    fun navigationReducer_reduce_when_actionExit_then_changeNavigationState() {
        // arrange
        val reducer = NavigationReducerImpl()
        val previousState = NavigationState(NavigationStatus.IN_CALL)
        val action = NavigationAction.Exit()

        // act
        val newState = reducer.reduce(previousState, action)

        // assert
        Assert.assertEquals(NavigationStatus.EXIT, newState.navigationState)
    }

    @Test
    fun navigationReducer_reduce_when_actionUpdateCallState_when_callingStateNotDisconnected_then_doNotChangeNavigationState() {
        // arrange
        val reducer = NavigationReducerImpl()
        val previousState = NavigationState(NavigationStatus.IN_CALL)
        val action = CallingAction.StateUpdated(CallingStatus.CONNECTED)

        // act
        val newState = reducer.reduce(previousState, action)

        // assert
        Assert.assertEquals(NavigationStatus.IN_CALL, newState.navigationState)
    }

    @Test
    fun navigationReducer_reduce_when_actionStartCall_then_doNotChangeNavigationState() {
        // arrange
        val reducer = NavigationReducerImpl()
        val previousState = NavigationState(NavigationStatus.SETUP)
        val action = CallingAction.CallStartRequested()

        // act
        val newState = reducer.reduce(previousState, action)

        // assert
        Assert.assertEquals(NavigationStatus.SETUP, newState.navigationState)
    }

    @Test
    fun navigationReducer_reduce_when_actionShowSupportForm_state_updated() {
        // arrange
        val reducer = NavigationReducerImpl()
        val previousState = NavigationState(NavigationStatus.IN_CALL, false)
        val action = NavigationAction.ShowSupportForm()

        // act
        val newState = reducer.reduce(previousState, action)

        // assert
        Assert.assertTrue(newState.supportVisible)
    }

    @Test
    fun navigationReducer_reduce_when_actionHideSupportForm_state_updated() {
        // arrange
        val reducer = NavigationReducerImpl()
        val previousState = NavigationState(NavigationStatus.IN_CALL, true)
        val action = NavigationAction.HideSupportForm()

        // act
        val newState = reducer.reduce(previousState, action)

        // assert
        Assert.assertFalse(newState.supportVisible)
    }

    @Test
    fun navigationReducer_reduce_when_actionShowCaptionsOptions_then_changeStateToShowCaptionsToggleUI() {
        // arrange
        val reducer = NavigationReducerImpl()
        val oldState = NavigationState(navigationState = NavigationStatus.IN_CALL, showCaptionsToggleUI = false)
        val action = NavigationAction.ShowCaptionsOptions()

        // act
        val newState = reducer.reduce(oldState, action)

        // assert
        Assert.assertEquals(true, newState.showCaptionsToggleUI)
    }

    @Test
    fun navigationReducer_reduce_when_actionCloseCaptionsOptions_then_changeStateToCloseCaptionsToggleUI() {
        // arrange
        val reducer = NavigationReducerImpl()
        val oldState = NavigationState(navigationState = NavigationStatus.IN_CALL, showCaptionsToggleUI = true)
        val action = NavigationAction.CloseCaptionsOptions()

        // act
        val newState = reducer.reduce(oldState, action)

        // assert
        Assert.assertEquals(false, newState.showCaptionsToggleUI)
    }

    @Test
    fun navigationReducer_reduce_when_actionShowSupportedSpokenLanguagesOptions_then_changeStateToShowSupportedSpokenLanguagesSelection() {
        // arrange
        val reducer = NavigationReducerImpl()
        val oldState = NavigationState(navigationState = NavigationStatus.IN_CALL, showSupportedSpokenLanguagesSelection = false)
        val action = NavigationAction.ShowSupportedSpokenLanguagesOptions()

        // act
        val newState = reducer.reduce(oldState, action)

        // assert
        Assert.assertEquals(true, newState.showSupportedSpokenLanguagesSelection)
    }

    @Test
    fun navigationReducer_reduce_when_actionShowSupportedCaptionLanguagesOptions_then_changeStateToShowSupportedCaptionLanguagesSelections() {
        // arrange
        val reducer = NavigationReducerImpl()
        val oldState = NavigationState(navigationState = NavigationStatus.IN_CALL, showSupportedCaptionLanguagesSelections = false)
        val action = NavigationAction.ShowSupportedCaptionLanguagesOptions()

        // act
        val newState = reducer.reduce(oldState, action)

        // assert
        Assert.assertEquals(true, newState.showSupportedCaptionLanguagesSelections)
    }

    @Test
    fun navigationReducer_reduce_when_actionHideSupportedLanguagesOptions_then_changeStateToHideSupportedLanguagesSelections() {
        // arrange
        val reducer = NavigationReducerImpl()
        val oldState = NavigationState(navigationState = NavigationStatus.IN_CALL, showSupportedSpokenLanguagesSelection = true, showSupportedCaptionLanguagesSelections = true)
        val action = NavigationAction.HideSupportedLanguagesOptions()

        // act
        val newState = reducer.reduce(oldState, action)

        // assert
        Assert.assertEquals(false, newState.showSupportedSpokenLanguagesSelection)
        Assert.assertEquals(false, newState.showSupportedCaptionLanguagesSelections)
    }
}
