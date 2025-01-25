// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.redux

import com.azure.android.communication.ui.calling.ACSBaseTestCoroutine
import com.azure.android.communication.ui.calling.models.ParticipantInfoModel
import com.azure.android.communication.ui.calling.models.ParticipantStatus
import com.azure.android.communication.ui.calling.redux.action.Action
import com.azure.android.communication.ui.calling.redux.action.CallingAction
import com.azure.android.communication.ui.calling.redux.reducer.AppStateReducer
import com.azure.android.communication.ui.calling.redux.state.AppReduxState
import com.azure.android.communication.ui.calling.redux.state.ReduxState
import com.azure.android.communication.ui.calling.redux.state.RemoteParticipantsState
import kotlinx.coroutines.flow.first
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
internal class AppStoreUnitTest : ACSBaseTestCoroutine() {

    @Mock
    private lateinit var mockAppStateReducer: AppStateReducer

    @Mock
    private lateinit var mockAppState: AppReduxState

    @Test
    fun appStore_dispatch_when_invoked_then_updateStoreState() =
        runScopedTest {
            // arrange
            val action = CallingAction.CallStartRequested()
            val stateTest = AppReduxState("", false, false)
            val participantMap: MutableMap<String, ParticipantInfoModel> = HashMap()
            participantMap["user"] =
                ParticipantInfoModel(
                    displayName = "user",
                    userIdentifier = "id",
                    isMuted = false,
                    isCameraDisabled = false,
                    isSpeaking = false,
                    isTypingRtt = false,
                    participantStatus = ParticipantStatus.HOLD,
                    screenShareVideoStreamModel = null,
                    cameraVideoStreamModel = null,
                    modifiedTimestamp = 0,
                )
            stateTest.remoteParticipantState = RemoteParticipantsState(participantMap, 0, listOf(), 0, null, participantMap.size)

            val store = AppStore(
                mockAppState,
                mockAppStateReducer,
                mutableListOf(TestMiddlewareImplementation() as Middleware<AppReduxState>),
                this.coroutineContext
            )

            Mockito.`when`(mockAppStateReducer.reduce(mockAppState, action)).thenReturn(stateTest)

            // act
            store.dispatch(action)

            // assert
            assertEquals(stateTest, store.getCurrentState())
        }

    @Test
    fun appStore_dispatch_when_invoked_then_callAllMiddlewares() =
        runScopedTest {
            // arrange
            val action = CallingAction.CallStartRequested()
            val middleware1 = TestMiddlewareImplementation() as Middleware<AppReduxState>
            val middleware2 = TestMiddlewareImplementation() as Middleware<AppReduxState>
            val middleware1Spy = Mockito.spy(middleware1)
            val middleware2Spy = Mockito.spy(middleware2)

            val store = AppStore(
                mockAppState,
                mockAppStateReducer,
                mutableListOf(middleware1Spy, middleware2Spy),
                this.coroutineContext
            )

            // act
            store.dispatch(action)

            // assert
            Mockito.verify(middleware1Spy, Mockito.times(1)).invoke(store)
            Mockito.verify(middleware2Spy, Mockito.times(1)).invoke(store)
        }

    @Test
    fun appStore_dispatch_when_invoked_then_callAllReducers() =
        runScopedTest {
            // arrange
            val action = CallingAction.CallStartRequested()

            val store = AppStore(
                mockAppState,
                mockAppStateReducer,
                mutableListOf(TestMiddlewareImplementation() as Middleware<AppReduxState>),
                this.coroutineContext
            )

            Mockito.`when`(mockAppStateReducer.reduce(mockAppState, action))
                .thenReturn(AppReduxState("", false, false))

            // act
            store.dispatch(action)

            // assert
            Mockito.verify(mockAppStateReducer, Mockito.times(1)).reduce(mockAppState, action)
        }

    @Test
    fun appStore_stateFlow_when_invoked_emit_latestState() =
        runScopedTest {
            // arrange
            val action = CallingAction.CallStartRequested()
            val testState = AppReduxState("", false, false)

            val store = AppStore(
                mockAppState,
                mockAppStateReducer,
                mutableListOf(TestMiddlewareImplementation() as Middleware<AppReduxState>),
                this.coroutineContext
            )
            Mockito.`when`(mockAppStateReducer.reduce(mockAppState, action)).thenReturn(testState)
            Mockito.`when`(mockAppStateReducer.reduce(testState, action)).thenReturn(mockAppState)

            // act
            store.dispatch(action)
            val firstState = store.getStateFlow().first()

            store.dispatch(action)
            val secondState = store.getStateFlow().first()

            // assert
            assertEquals(testState, firstState)
            assertEquals(mockAppState, secondState)
        }

    internal class TestMiddlewareImplementation : Middleware<ReduxState> {
        override fun invoke(store: Store<ReduxState>) = { next: Dispatch ->
            { action: Action ->
                next(action)
            }
        }
    }
}
