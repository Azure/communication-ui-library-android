// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.redux.reducer

import com.azure.android.communication.ui.redux.action.CallingAction
import com.azure.android.communication.ui.redux.action.NavigationAction
import com.azure.android.communication.ui.redux.state.CallingStatus
import com.azure.android.communication.ui.redux.state.NavigationState
import com.azure.android.communication.ui.redux.state.NavigationStatus
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
}
