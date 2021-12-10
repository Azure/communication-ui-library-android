// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.redux

import android.os.Handler
import com.azure.android.communication.ui.helper.HandlerAnswerStub
import com.azure.android.communication.ui.helper.MainCoroutineRule
import com.azure.android.communication.ui.model.ParticipantInfoModel
import com.azure.android.communication.ui.redux.action.Action
import com.azure.android.communication.ui.redux.action.CallingAction
import com.azure.android.communication.ui.redux.reducer.AppStateReducer
import com.azure.android.communication.ui.redux.state.AppReduxState
import com.azure.android.communication.ui.redux.state.ReduxState
import com.azure.android.communication.ui.redux.state.RemoteParticipantsState
import com.azure.android.communication.ui.utilities.StoreHandlerThread
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any

@RunWith(MockitoJUnitRunner::class)
internal class AppStoreUnitTest {

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Mock
    private lateinit var mockAppStateReducer: AppStateReducer

    @Mock
    private lateinit var mockAppState: AppReduxState

    @Mock
    private lateinit var mockStoreHandlerThread: StoreHandlerThread

    @Mock
    private lateinit var mockHandler: Handler

    @Test
    fun appStore_dispatch_when_invoked_then_updateStoreState() =
        mainCoroutineRule.testDispatcher.runBlockingTest {
            // arrange
            val action = CallingAction.CallStartRequested()
            val stateTest = AppReduxState("")
            var participantMap: MutableMap<String, ParticipantInfoModel> = HashMap()
            participantMap["user"] =
                ParticipantInfoModel("user", "id", false, false, null, null, 0, 0)
            stateTest.remoteParticipantState = RemoteParticipantsState(participantMap, 0)

            Mockito.`when`(mockHandler.post(any())).thenAnswer(HandlerAnswerStub())
            Mockito.`when`(mockStoreHandlerThread.startHandlerThread()).thenReturn(mockHandler)
            Mockito.`when`(mockStoreHandlerThread.isHandlerThreadAlive()).thenReturn(true)

            val store = AppStore(
                mockAppState,
                mockAppStateReducer,
                mutableListOf(TestMiddlewareImplementation() as Middleware<AppReduxState>),
                mockStoreHandlerThread
            )

            Mockito.`when`(mockAppStateReducer.reduce(mockAppState, action)).thenReturn(stateTest)

            // act
            store.dispatch(action)

            // assert
            assertEquals(stateTest, store.getCurrentState())
        }

    @Test
    fun appStore_dispatch_when_invoked_then_callAllMiddlewares() =
        mainCoroutineRule.testDispatcher.runBlockingTest {
            // arrange
            val action = CallingAction.CallStartRequested()
            val middleware1 = TestMiddlewareImplementation() as Middleware<AppReduxState>
            val middleware2 = TestMiddlewareImplementation() as Middleware<AppReduxState>
            val middleware1Spy = Mockito.spy(middleware1)
            val middleware2Spy = Mockito.spy(middleware2)
            Mockito.`when`(mockHandler.post(any())).thenAnswer(HandlerAnswerStub())
            Mockito.`when`(mockStoreHandlerThread.startHandlerThread()).thenReturn(mockHandler)
            Mockito.`when`(mockStoreHandlerThread.isHandlerThreadAlive()).thenReturn(true)

            val store = AppStore(
                mockAppState,
                mockAppStateReducer,
                mutableListOf(middleware1Spy, middleware2Spy),
                mockStoreHandlerThread
            )

            // act
            store.dispatch(action)

            // assert
            Mockito.verify(middleware1Spy, Mockito.times(1)).invoke(store)
            Mockito.verify(middleware2Spy, Mockito.times(1)).invoke(store)
        }

    @Test
    fun appStore_dispatch_when_invoked_then_callAllReducers() =
        mainCoroutineRule.testDispatcher.runBlockingTest {
            // arrange
            val action = CallingAction.CallStartRequested()

            Mockito.`when`(mockHandler.post(any())).thenAnswer(HandlerAnswerStub())
            Mockito.`when`(mockStoreHandlerThread.startHandlerThread()).thenReturn(mockHandler)
            Mockito.`when`(mockStoreHandlerThread.isHandlerThreadAlive()).thenReturn(true)

            val store = AppStore(
                mockAppState,
                mockAppStateReducer,
                mutableListOf(TestMiddlewareImplementation() as Middleware<AppReduxState>),
                mockStoreHandlerThread
            )

            Mockito.`when`(mockAppStateReducer.reduce(mockAppState, action))
                .thenReturn(AppReduxState(""))

            // act
            store.dispatch(action)

            // assert
            Mockito.verify(mockAppStateReducer, Mockito.times(1)).reduce(mockAppState, action)
        }

    @Test
    fun appStore_stateFlow_when_invoked_emit_latestState() =
        mainCoroutineRule.testDispatcher.runBlockingTest {
            // arrange
            val action = CallingAction.CallStartRequested()
            val testState = AppReduxState("")
            Mockito.`when`(mockHandler.post(any())).thenAnswer(HandlerAnswerStub())
            Mockito.`when`(mockStoreHandlerThread.startHandlerThread()).thenReturn(mockHandler)

            val store = AppStore(
                mockAppState,
                mockAppStateReducer,
                mutableListOf(TestMiddlewareImplementation() as Middleware<AppReduxState>),
                mockStoreHandlerThread
            )
            Mockito.`when`(mockAppStateReducer.reduce(mockAppState, action)).thenReturn(testState)
            Mockito.`when`(mockAppStateReducer.reduce(testState, action)).thenReturn(mockAppState)
            Mockito.`when`(mockStoreHandlerThread.isHandlerThreadAlive()).thenReturn(true)

            // act
            store.dispatch(action)
            var firstState = store.getStateFlow().first()

            store.dispatch(action)
            var secondState = store.getStateFlow().first()

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
