// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.handler

import com.azure.android.communication.ui.calling.ACSBaseTestCoroutine
import com.azure.android.communication.ui.calling.CallCompositeEventHandler
import com.azure.android.communication.ui.calling.configuration.CallCompositeConfiguration
import com.azure.android.communication.ui.calling.handlers.CallStateHandler
import com.azure.android.communication.ui.calling.models.CallCompositeCallStateCode
import com.azure.android.communication.ui.calling.models.CallCompositeCallStateChangedEvent
import com.azure.android.communication.ui.calling.redux.AppStore
import com.azure.android.communication.ui.calling.redux.state.AppReduxState
import com.azure.android.communication.ui.calling.redux.state.CallingState
import com.azure.android.communication.ui.calling.redux.state.CallingStatus
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
            val storeStateFlow = MutableStateFlow<ReduxState>(
                AppReduxState(
                    "",
                    false,
                    false,
                    false,
                    localOptions = localOptions
                )
            )
            storeStateFlow.value.callState = CallingState(
                callingStatus = CallingStatus.NONE,
                callId = "callId",
                callEndReasonCode = 123,
                callEndReasonSubCode = 456
            )
            val mockAppStore = mock<AppStore<ReduxState>> {
                on { getStateFlow() } doReturn storeStateFlow
            }
            val mockHandler =
                mock<CallCompositeEventHandler<CallCompositeCallStateChangedEvent>>()
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
                    event is CallCompositeCallStateChangedEvent &&
                        event.code == CallCompositeCallStateCode.NONE &&
                        event.callEndReasonCode == 123 &&
                        event.callEndReasonSubCode == 456
                }
            )
            job.cancel()
        }
    }

    @Test
    fun callStateEventHandler_onCompositeExit_eventIsFiredToContoso() {
        runScopedTest {
            // arrange
            val storeStateFlow = MutableStateFlow<ReduxState>(
                AppReduxState(
                    "",
                    false,
                    false,
                    false,
                    localOptions = localOptions
                )
            )
            storeStateFlow.value.callState = CallingState(
                callingStatus = CallingStatus.NONE,
                callId = "callId",
                callEndReasonCode = 123,
                callEndReasonSubCode = 456
            )
            val mockAppStore = mock<AppStore<ReduxState>> {
                on { getCurrentState() } doReturn storeStateFlow.value
            }
            val mockHandler =
                mock<CallCompositeEventHandler<CallCompositeCallStateChangedEvent>>()
            val configuration = CallCompositeConfiguration()
            configuration.callCompositeEventsHandler.addOnCallStateChangedEventHandler(
                mockHandler
            )
            val handler = CallStateHandler(
                configuration,
                mockAppStore
            )

            // act
            handler.onCompositeExit()

            // assert
            verify(mockHandler, times(1)).handle(
                argThat { event ->
                    event is CallCompositeCallStateChangedEvent &&
                        event.code == CallCompositeCallStateCode.NONE &&
                        event.callEndReasonCode == 123 &&
                        event.callEndReasonSubCode == 456
                }
            )
        }
    }

    @Test
    fun callStateEventHandler_start_onMultipleSameCallStateChange_then_eventIsFiredToContosoOnce() {
        runScopedTest {
            // arrange
            val appState = AppReduxState("", false, false, false, localOptions = localOptions)
            appState.callState = CallingState()

            val storeStateFlow = MutableStateFlow<ReduxState>(appState)
            val mockAppStore = mock<AppStore<ReduxState>> {
                on { getStateFlow() } doReturn storeStateFlow
            }
            val mockHandler =
                mock<CallCompositeEventHandler<CallCompositeCallStateChangedEvent>>()
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
            val appStateNew = AppReduxState("", false, false, false, localOptions = localOptions)
            appStateNew.callState = CallingState(callingStatus = CallingStatus.CONNECTED, callId = "callId")
            storeStateFlow.value = appStateNew

            // assert
            TestCase.assertEquals(
                CallCompositeCallStateCode.CONNECTED,
                handler.getCallCompositeCallState()
            )
            verify(mockHandler, times(1)).handle(
                argThat { event ->
                    event is CallCompositeCallStateChangedEvent && event.code == CallCompositeCallStateCode.CONNECTED
                }
            )
            job.cancel()
        }
    }

    @Test
    fun callStateEventHandler_start_onCallStateChangeWithNoHandler_then_eventIsNotFiredToContoso() {
        runScopedTest {
            // arrange
            val appState = AppReduxState("", false, false, false, localOptions = localOptions)
            appState.callState = CallingState(callingStatus = CallingStatus.CONNECTED)

            val storeStateFlow = MutableStateFlow<ReduxState>(appState)
            val mockAppStore = mock<AppStore<ReduxState>> {
                on { getStateFlow() } doReturn storeStateFlow
            }
            val mockHandler =
                mock<CallCompositeEventHandler<CallCompositeCallStateChangedEvent>>()
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
            val appState = AppReduxState("", false, false, false, localOptions = localOptions)
            appState.callState = CallingState(callingStatus = CallingStatus.CONNECTED, callId = "callId")

            val storeStateFlow = MutableStateFlow<ReduxState>(appState)
            val mockAppStore = mock<AppStore<ReduxState>> {
                on { getStateFlow() } doReturn storeStateFlow
            }
            val mockHandler =
                mock<CallCompositeEventHandler<CallCompositeCallStateChangedEvent>>()
            val mockHandler2 =
                mock<CallCompositeEventHandler<CallCompositeCallStateChangedEvent>>()
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
                    event is CallCompositeCallStateChangedEvent && event.code == CallCompositeCallStateCode.CONNECTED
                }
            )
            verify(mockHandler2, times(1)).handle(
                argThat { event ->
                    event is CallCompositeCallStateChangedEvent && event.code == CallCompositeCallStateCode.CONNECTED
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
        val appState = AppReduxState("", false, false, false, localOptions = localOptions)
        appState.callState = CallingState(callingStatus = CallingStatus.NONE, "abc")
        val storeStateFlow = MutableStateFlow<ReduxState>(appState)
        val mockAppStore = mock<AppStore<ReduxState>> {
            on { getStateFlow() } doReturn storeStateFlow
        }
        var mockHandler =
            mock<CallCompositeEventHandler<CallCompositeCallStateChangedEvent>>()
        var configuration = CallCompositeConfiguration()
        configuration.callCompositeEventsHandler.addOnCallStateChangedEventHandler(
            mockHandler
        )
        var handler = CallStateHandler(
            configuration,
            mockAppStore
        )

        // act
        appState.callState = CallingState(callingStatus = callingStatus, "12345")
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
                event is CallCompositeCallStateChangedEvent && event.code == callCompositeCallStateCode
            }
        )
        job.cancel()
    }

    private fun TestScope.testNotCallState(
        callingStatus: CallingStatus,
        callCompositeCallStateCode: CallCompositeCallStateCode,
    ) {
        // arrange
        val appState = AppReduxState("", false, false, false, localOptions = localOptions)
        appState.callState = CallingState(callingStatus = CallingStatus.NONE, "abc")
        val storeStateFlow = MutableStateFlow<ReduxState>(appState)
        val mockAppStore = mock<AppStore<ReduxState>> {
            on { getStateFlow() } doReturn storeStateFlow
        }
        var mockHandler =
            mock<CallCompositeEventHandler<CallCompositeCallStateChangedEvent>>()
        var configuration = CallCompositeConfiguration()
        configuration.callCompositeEventsHandler.addOnCallStateChangedEventHandler(
            mockHandler
        )
        var handler = CallStateHandler(
            configuration,
            mockAppStore
        )

        // act
        appState.callState = CallingState(callingStatus, callId = "12345")
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
                event is CallCompositeCallStateChangedEvent && event.code != callCompositeCallStateCode
            }
        )
        job.cancel()
    }
}
