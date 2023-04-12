// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.handler

import com.azure.android.communication.ui.ACSBaseTestCoroutine
import com.azure.android.communication.ui.calling.CallCompositeEventHandler
import com.azure.android.communication.ui.calling.configuration.CallCompositeConfiguration
import com.azure.android.communication.ui.calling.handlers.CallStateHandler
import com.azure.android.communication.ui.calling.models.CallCompositeCallState
import com.azure.android.communication.ui.calling.redux.AppStore
import com.azure.android.communication.ui.calling.redux.state.AppReduxState
import com.azure.android.communication.ui.calling.redux.state.CallingState
import com.azure.android.communication.ui.calling.redux.state.CallingStatus
import com.azure.android.communication.ui.calling.redux.state.OperationStatus
import com.azure.android.communication.ui.calling.redux.state.ReduxState
import junit.framework.TestCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.argThat
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify

@RunWith(MockitoJUnitRunner::class)
internal class CallStateHandlerUnitTests : ACSBaseTestCoroutine() {

    @Test
    fun callStateEventHandler_start_onCallStateChange_then_eventIsFiredToContoso() {
        runScopedTest {
            // arrange
            val storeStateFlow = MutableStateFlow<ReduxState>(AppReduxState("", false, false))
            val mockAppStore = mock<AppStore<ReduxState>> {
                on { getStateFlow() } doReturn storeStateFlow
            }
            val mockHandler =
                mock<CallCompositeEventHandler<CallCompositeCallState>>()
            val configuration = CallCompositeConfiguration()
            configuration.callCompositeEventsHandler.addOnCallStateEventHandler(
                mockHandler
            )
            val handler = CallStateHandler(
                configuration,
                mockAppStore
            )

            // act
            val job = launch {
                handler.start()
            }

            testScheduler.runCurrent()

            // assert
            verify(mockHandler, times(1)).handle(
                argThat { event ->
                    event == CallCompositeCallState.NONE
                }
            )
            job.cancel()
        }
    }

    @Test
    fun callStateEventHandler_start_onMultipleSameCallStateChange_then_eventIsFiredToContosoOnce() {
        runScopedTest {
            // arrange
            val appState = AppReduxState("", false, false)
            appState.callState = CallingState(CallingStatus.CONNECTED, OperationStatus.NONE)

            val storeStateFlow = MutableStateFlow<ReduxState>(appState)
            val mockAppStore = mock<AppStore<ReduxState>> {
                on { getStateFlow() } doReturn storeStateFlow
            }
            val mockHandler =
                mock<CallCompositeEventHandler<CallCompositeCallState>>()
            val configuration = CallCompositeConfiguration()
            configuration.callCompositeEventsHandler.addOnCallStateEventHandler(
                mockHandler
            )
            val handler = CallStateHandler(
                configuration,
                mockAppStore
            )

            // act
            val job = launch {
                handler.start()
                handler.start()
            }
            testScheduler.runCurrent()

            // assert
            TestCase.assertEquals(CallCompositeCallState.CONNECTED,
                handler.getCallCompositeCallState())
            verify(mockHandler, times(1)).handle(
                argThat { event ->
                    event == CallCompositeCallState.CONNECTED
                }
            )
            job.cancel()
        }
    }

    @Test
    fun callStateEventHandler_start_onCallStateChangeWithNoHandler_then_eventIsNotFiredToContoso() {
        runScopedTest {
            // arrange
            val appState = AppReduxState("", false, false)
            appState.callState = CallingState(CallingStatus.CONNECTED, OperationStatus.NONE)

            val storeStateFlow = MutableStateFlow<ReduxState>(appState)
            val mockAppStore = mock<AppStore<ReduxState>> {
                on { getStateFlow() } doReturn storeStateFlow
            }
            val mockHandler =
                mock<CallCompositeEventHandler<CallCompositeCallState>>()
            val configuration = CallCompositeConfiguration()
            configuration.callCompositeEventsHandler.addOnCallStateEventHandler(
                mockHandler
            )
            val handler = CallStateHandler(
                configuration,
                mockAppStore
            )

            // act
            configuration.callCompositeEventsHandler.removeOnCallStateEventHandler(mockHandler)
            val job = launch {
                handler.start()
            }
            testScheduler.runCurrent()

            // assert
            TestCase.assertEquals(CallCompositeCallState.CONNECTED,
                handler.getCallCompositeCallState())
            verify(mockHandler, times(0)).handle(any())
            job.cancel()
        }
    }

    @Test
    fun callStateEventHandler_start_onCallStateChange_then_allHandlersAreNotified() {
        runScopedTest {
            // arrange
            val appState = AppReduxState("", false, false)
            appState.callState = CallingState(CallingStatus.CONNECTED, OperationStatus.NONE)

            val storeStateFlow = MutableStateFlow<ReduxState>(appState)
            val mockAppStore = mock<AppStore<ReduxState>> {
                on { getStateFlow() } doReturn storeStateFlow
            }
            val mockHandler =
                mock<CallCompositeEventHandler<CallCompositeCallState>>()
            val mockHandler2 =
                mock<CallCompositeEventHandler<CallCompositeCallState>>()
            val configuration = CallCompositeConfiguration()
            configuration.callCompositeEventsHandler.addOnCallStateEventHandler(
                mockHandler
            )
            configuration.callCompositeEventsHandler.addOnCallStateEventHandler(
                mockHandler2
            )
            val handler = CallStateHandler(
                configuration,
                mockAppStore
            )

            // act
            val job = launch {
                handler.start()
                handler.start()
            }
            testScheduler.runCurrent()

            // assert
            TestCase.assertEquals(CallCompositeCallState.CONNECTED,
                handler.getCallCompositeCallState())
            verify(mockHandler, times(1)).handle(
                argThat { event ->
                    event == CallCompositeCallState.CONNECTED
                }
            )
            verify(mockHandler2, times(1)).handle(
                argThat { event ->
                    event == CallCompositeCallState.CONNECTED
                }
            )
            job.cancel()
        }
    }

    @Test
    fun callStateEventHandler_start_onCallStateChange_then_eventIsFiredToContosoOnce_allStatesTest() {
        runScopedTest {
            // test all
            testCallState(
                CallingStatus.CONNECTING,
                CallCompositeCallState.CONNECTING)
            testCallState(
                CallingStatus.DISCONNECTED,
                CallCompositeCallState.DISCONNECTED)
            testCallState(
                CallingStatus.CONNECTED,
                CallCompositeCallState.CONNECTED)
            testCallState(
                CallingStatus.DISCONNECTING,
                CallCompositeCallState.DISCONNECTING)
            testCallState(
                CallingStatus.EARLY_MEDIA,
                CallCompositeCallState.EARLY_MEDIA)
            testCallState(
                CallingStatus.IN_LOBBY,
                CallCompositeCallState.IN_LOBBY)
            testCallState(
                CallingStatus.LOCAL_HOLD,
                CallCompositeCallState.LOCAL_HOLD)
            testCallState(
                CallingStatus.NONE,
                CallCompositeCallState.NONE)
            testCallState(
                CallingStatus.REMOTE_HOLD,
                CallCompositeCallState.REMOTE_HOLD)
            testCallState(
                CallingStatus.RINGING,
                CallCompositeCallState.RINGING)
            testNotCallState(
                CallingStatus.RINGING,
                CallCompositeCallState.CONNECTING)
        }
    }

    private fun TestScope.testCallState(
        callingStatus: CallingStatus,
        callCompositeCallState: CallCompositeCallState,
    ) {
        // arrange
        val appState = AppReduxState("", false, false)
        val storeStateFlow = MutableStateFlow<ReduxState>(appState)
        val mockAppStore = mock<AppStore<ReduxState>> {
            on { getStateFlow() } doReturn storeStateFlow
        }
        var mockHandler =
            mock<CallCompositeEventHandler<CallCompositeCallState>>()
        var configuration = CallCompositeConfiguration()
        configuration.callCompositeEventsHandler.addOnCallStateEventHandler(
            mockHandler
        )
        var handler = CallStateHandler(
            configuration,
            mockAppStore
        )

        // act
        appState.callState = CallingState(callingStatus, OperationStatus.NONE)
        var job = launch {
            handler.start()
        }
        testScheduler.runCurrent()

        // assert
        TestCase.assertEquals(callCompositeCallState,
            handler.getCallCompositeCallState())
        verify(mockHandler, times(1)).handle(
            argThat { event ->
                event == callCompositeCallState
            }
        )
        job.cancel()
    }

    private fun TestScope.testNotCallState(
        callingStatus: CallingStatus,
        callCompositeCallState: CallCompositeCallState,
    ) {
        // arrange
        val appState = AppReduxState("", false, false)
        val storeStateFlow = MutableStateFlow<ReduxState>(appState)
        val mockAppStore = mock<AppStore<ReduxState>> {
            on { getStateFlow() } doReturn storeStateFlow
        }
        var mockHandler =
            mock<CallCompositeEventHandler<CallCompositeCallState>>()
        var configuration = CallCompositeConfiguration()
        configuration.callCompositeEventsHandler.addOnCallStateEventHandler(
            mockHandler
        )
        var handler = CallStateHandler(
            configuration,
            mockAppStore
        )

        // act
        appState.callState = CallingState(callingStatus, OperationStatus.NONE)
        var job = launch {
            handler.start()
        }
        testScheduler.runCurrent()

        // assert
        TestCase.assertNotSame(callCompositeCallState,
            handler.getCallCompositeCallState())
        verify(mockHandler, times(1)).handle(
            argThat { event ->
                event != callCompositeCallState
            }
        )
        job.cancel()
    }
}
