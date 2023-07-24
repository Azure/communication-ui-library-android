// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.handler

import com.azure.android.communication.ui.ACSBaseTestCoroutine
import com.azure.android.communication.ui.calling.CallCompositeEventHandler
import com.azure.android.communication.ui.calling.configuration.CallCompositeConfiguration
import com.azure.android.communication.ui.calling.handlers.CallStateHandler
import com.azure.android.communication.ui.calling.models.CallCompositeCallStateCode
import com.azure.android.communication.ui.calling.models.CallCompositeCallStateEvent
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
                mock<CallCompositeEventHandler<CallCompositeCallStateEvent>>()
            val configuration = CallCompositeConfiguration()
            configuration.callCompositeEventsHandler.addOnCallStateChangedEventHandler(
                mockHandler
            )
            val handler = CallStateHandler(
                configuration,
                mockAppStore
            )

            // act
            val job = launch {
                handler.start(this)
            }

            testScheduler.runCurrent()

            // assert
            verify(mockHandler, times(1)).handle(
                argThat { event ->
                    event is CallCompositeCallStateEvent && event.code == CallCompositeCallStateCode.NONE
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
                mock<CallCompositeEventHandler<CallCompositeCallStateEvent>>()
            val configuration = CallCompositeConfiguration()
            configuration.callCompositeEventsHandler.addOnCallStateChangedEventHandler(
                mockHandler
            )
            val handler = CallStateHandler(
                configuration,
                mockAppStore
            )

            // act
            val job = launch {
                handler.start(this)
            }
            testScheduler.runCurrent()
            val appStateNew = AppReduxState("", false, false)
            appStateNew.callState = CallingState(CallingStatus.CONNECTED, OperationStatus.NONE)
            storeStateFlow.value = appStateNew

            // assert
            TestCase.assertEquals(
                CallCompositeCallStateCode.CONNECTED,
                handler.getCallCompositeCallState()
            )
            verify(mockHandler, times(1)).handle(
                argThat { event ->
                    event is CallCompositeCallStateEvent && event.code == CallCompositeCallStateCode.CONNECTED
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
                mock<CallCompositeEventHandler<CallCompositeCallStateEvent>>()
            val configuration = CallCompositeConfiguration()
            configuration.callCompositeEventsHandler.addOnCallStateChangedEventHandler(
                mockHandler
            )
            val handler = CallStateHandler(
                configuration,
                mockAppStore
            )

            // act
            configuration.callCompositeEventsHandler.removeOnCallStateEventHandler(mockHandler)
            val job = launch {
                handler.start(this)
            }
            testScheduler.runCurrent()

            // assert
            TestCase.assertEquals(
                CallCompositeCallStateCode.CONNECTED,
                handler.getCallCompositeCallState()
            )
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
                mock<CallCompositeEventHandler<CallCompositeCallStateEvent>>()
            val mockHandler2 =
                mock<CallCompositeEventHandler<CallCompositeCallStateEvent>>()
            val configuration = CallCompositeConfiguration()
            configuration.callCompositeEventsHandler.addOnCallStateChangedEventHandler(
                mockHandler
            )
            configuration.callCompositeEventsHandler.addOnCallStateChangedEventHandler(
                mockHandler2
            )
            val handler = CallStateHandler(
                configuration,
                mockAppStore
            )

            // act
            val job = launch {
                handler.start(this)
            }
            testScheduler.runCurrent()

            // assert
            TestCase.assertEquals(
                CallCompositeCallStateCode.CONNECTED,
                handler.getCallCompositeCallState()
            )
            verify(mockHandler, times(1)).handle(
                argThat { event ->
                    event is CallCompositeCallStateEvent && event.code == CallCompositeCallStateCode.CONNECTED
                }
            )
            verify(mockHandler2, times(1)).handle(
                argThat { event ->
                    event is CallCompositeCallStateEvent && event.code == CallCompositeCallStateCode.CONNECTED
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
                CallCompositeCallStateCode.CONNECTING
            )
            testCallState(
                CallingStatus.DISCONNECTED,
                CallCompositeCallStateCode.DISCONNECTED
            )
            testCallState(
                CallingStatus.CONNECTED,
                CallCompositeCallStateCode.CONNECTED
            )
            testCallState(
                CallingStatus.DISCONNECTING,
                CallCompositeCallStateCode.DISCONNECTING
            )
            testCallState(
                CallingStatus.EARLY_MEDIA,
                CallCompositeCallStateCode.EARLY_MEDIA
            )
            testCallState(
                CallingStatus.IN_LOBBY,
                CallCompositeCallStateCode.IN_LOBBY
            )
            testCallState(
                CallingStatus.LOCAL_HOLD,
                CallCompositeCallStateCode.LOCAL_HOLD
            )
            testCallState(
                CallingStatus.NONE,
                CallCompositeCallStateCode.NONE
            )
            testCallState(
                CallingStatus.REMOTE_HOLD,
                CallCompositeCallStateCode.REMOTE_HOLD
            )
            testCallState(
                CallingStatus.RINGING,
                CallCompositeCallStateCode.RINGING
            )
            testNotCallState(
                CallingStatus.RINGING,
                CallCompositeCallStateCode.CONNECTING
            )
        }
    }

    private fun TestScope.testCallState(
        callingStatus: CallingStatus,
        callCompositeCallStateCode: CallCompositeCallStateCode,
    ) {
        // arrange
        val appState = AppReduxState("", false, false)
        val storeStateFlow = MutableStateFlow<ReduxState>(appState)
        val mockAppStore = mock<AppStore<ReduxState>> {
            on { getStateFlow() } doReturn storeStateFlow
        }
        var mockHandler =
            mock<CallCompositeEventHandler<CallCompositeCallStateEvent>>()
        var configuration = CallCompositeConfiguration()
        configuration.callCompositeEventsHandler.addOnCallStateChangedEventHandler(
            mockHandler
        )
        var handler = CallStateHandler(
            configuration,
            mockAppStore
        )

        // act
        appState.callState = CallingState(callingStatus, OperationStatus.NONE)
        var job = launch {
            handler.start(this)
        }
        testScheduler.runCurrent()

        // assert
        TestCase.assertEquals(
            callCompositeCallStateCode,
            handler.getCallCompositeCallState()
        )
        verify(mockHandler, times(1)).handle(
            argThat { event ->
                event is CallCompositeCallStateEvent && event.code == callCompositeCallStateCode
            }
        )
        job.cancel()
    }

    private fun TestScope.testNotCallState(
        callingStatus: CallingStatus,
        callCompositeCallStateCode: CallCompositeCallStateCode,
    ) {
        // arrange
        val appState = AppReduxState("", false, false)
        val storeStateFlow = MutableStateFlow<ReduxState>(appState)
        val mockAppStore = mock<AppStore<ReduxState>> {
            on { getStateFlow() } doReturn storeStateFlow
        }
        var mockHandler =
            mock<CallCompositeEventHandler<CallCompositeCallStateEvent>>()
        var configuration = CallCompositeConfiguration()
        configuration.callCompositeEventsHandler.addOnCallStateChangedEventHandler(
            mockHandler
        )
        var handler = CallStateHandler(
            configuration,
            mockAppStore
        )

        // act
        appState.callState = CallingState(callingStatus, OperationStatus.NONE)
        var job = launch {
            handler.start(this)
        }
        testScheduler.runCurrent()

        // assert
        TestCase.assertNotSame(
            callCompositeCallStateCode,
            handler.getCallCompositeCallState()
        )
        verify(mockHandler, times(1)).handle(
            argThat { event ->
                event is CallCompositeCallStateEvent && event.code != callCompositeCallStateCode
            }
        )
        job.cancel()
    }
}
