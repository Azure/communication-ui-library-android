// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.service

import com.azure.android.communication.ui.configuration.CallCompositeConfiguration
import com.azure.android.communication.ui.configuration.events.CallCompositeErrorCode
import com.azure.android.communication.ui.error.CallCompositeError
import com.azure.android.communication.ui.error.CallStateError
import com.azure.android.communication.ui.error.ErrorHandler
import com.azure.android.communication.ui.helper.MainCoroutineRule
import com.azure.android.communication.ui.redux.AppStore
import com.azure.android.communication.ui.redux.action.ErrorAction
import com.azure.android.communication.ui.redux.state.AppReduxState
import com.azure.android.communication.ui.redux.state.AudioDeviceSelectionStatus
import com.azure.android.communication.ui.redux.state.AudioOperationalStatus
import com.azure.android.communication.ui.redux.state.AudioState
import com.azure.android.communication.ui.redux.state.CameraDeviceSelectionStatus
import com.azure.android.communication.ui.redux.state.CameraOperationalStatus
import com.azure.android.communication.ui.redux.state.CameraState
import com.azure.android.communication.ui.redux.state.CameraTransmissionStatus
import com.azure.android.communication.ui.redux.state.ErrorState
import com.azure.android.communication.ui.redux.state.LocalUserState
import com.azure.android.communication.ui.redux.state.ReduxState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.argThat
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify

@RunWith(MockitoJUnitRunner::class)
internal class ErrorHandlerUnitTests {
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Test
    fun errorHandler_onStateChange_withNoError_callsNothing() =
        mainCoroutineRule.testDispatcher.runBlockingTest {
            // arrange
            val appState = AppReduxState("")
            appState.errorState = ErrorState(null, null)

            val stateFlow: MutableStateFlow<ReduxState> = MutableStateFlow(AppReduxState(""))
            val mockAppStore = mock<AppStore<ReduxState>> {
                on { getStateFlow() } doReturn stateFlow
            }

            val configuration = CallCompositeConfiguration()
            configuration.callCompositeEventsHandler.setOnErrorHandler(mock { })

            val errorHandler = ErrorHandler(configuration, mockAppStore)

            // act
            val job = launch {
                errorHandler.start()
            }

            stateFlow.value = appState

            // assert
            verify(mockAppStore, times(0)).dispatch(
                argThat { action ->
                    action is Any
                }
            )

            verify(configuration.callCompositeEventsHandler.getOnErrorHandler()!!, times(0)).handle(
                argThat { action ->
                    action is Any
                }
            )

            job.cancel()
        }

    @Test
    fun errorHandler_onStateChange_withCameraError_callsOnException() =
        mainCoroutineRule.testDispatcher.runBlockingTest {
            // arrange
            val appState = AppReduxState("")
            val error = Exception("Camera error")
            appState.errorState = ErrorState(null, null)
            appState.localParticipantState = LocalUserState(
                CameraState(
                    CameraOperationalStatus.OFF, CameraDeviceSelectionStatus.FRONT,
                    CameraTransmissionStatus.REMOTE,
                    CallCompositeError(CallCompositeErrorCode.TURN_CAMERA_OFF, error),
                ),
                AudioState(
                    AudioOperationalStatus.OFF,
                    AudioDeviceSelectionStatus.SPEAKER_SELECTED,
                ),
                videoStreamID = null,
                displayName = "name"
            )

            val stateFlow: MutableStateFlow<ReduxState> = MutableStateFlow(AppReduxState(""))
            val mockAppStore = mock<AppStore<ReduxState>> {
                on { getStateFlow() } doReturn stateFlow
            }

            val configuration = CallCompositeConfiguration()
            configuration.callCompositeEventsHandler.setOnErrorHandler(mock { on { handle(any()) } doAnswer { } })
            val errorHandler = ErrorHandler(configuration, mockAppStore)

            // act
            val job = launch {
                errorHandler.start()
            }
            stateFlow.value = appState

            // assert
            verify(mockAppStore, times(0)).dispatch(
                argThat { action ->
                    action is ErrorAction.EmergencyExit
                }
            )

            verify(configuration.callCompositeEventsHandler.getOnErrorHandler()!!, times(1)).handle(
                argThat { exception ->
                    exception.cause == error && exception.errorCode == CallCompositeErrorCode.TURN_CAMERA_OFF
                }
            )

            job.cancel()
        }

    @Test
    fun errorHandler_onStateChange_withMicError_callsOnException() =
        mainCoroutineRule.testDispatcher.runBlockingTest {
            // arrange
            val appState = AppReduxState("")
            val error = Exception("Mic error")
            appState.errorState = ErrorState(null, null)
            appState.localParticipantState = LocalUserState(
                CameraState(
                    CameraOperationalStatus.OFF,
                    CameraDeviceSelectionStatus.FRONT,
                    CameraTransmissionStatus.REMOTE
                ),
                AudioState(
                    AudioOperationalStatus.OFF, AudioDeviceSelectionStatus.SPEAKER_SELECTED,
                    CallCompositeError(CallCompositeErrorCode.TURN_MIC_OFF, error),
                ),
                videoStreamID = null,
                displayName = "name"
            )

            val stateFlow: MutableStateFlow<ReduxState> = MutableStateFlow(AppReduxState(""))
            val mockAppStore = mock<AppStore<ReduxState>> {
                on { getStateFlow() } doReturn stateFlow
            }

            val configuration = CallCompositeConfiguration()
            configuration.callCompositeEventsHandler.setOnErrorHandler(mock { on { handle(any()) } doAnswer { } })

            val errorHandler = ErrorHandler(configuration, mockAppStore)

            // act
            val job = launch {
                errorHandler.start()
            }
            stateFlow.value = appState

            // assert
            verify(mockAppStore, times(0)).dispatch(
                argThat { action ->
                    action is ErrorAction.EmergencyExit
                }
            )

            verify(configuration.callCompositeEventsHandler.getOnErrorHandler()!!, times(1)).handle(
                argThat { exception ->
                    exception.cause == error && exception.errorCode == CallCompositeErrorCode.TURN_MIC_OFF
                }
            )

            job.cancel()
        }

    @Test
    fun errorHandler_onStateChange_withCallStateErrorTokenExpired_callsOnException() =
        mainCoroutineRule.testDispatcher.runBlockingTest {
            // arrange
            val appState = AppReduxState("")
            appState.errorState = ErrorState(null, CallStateError(CallCompositeErrorCode.TOKEN_EXPIRED))

            val stateFlow: MutableStateFlow<ReduxState> = MutableStateFlow(AppReduxState(""))
            val mockAppStore = mock<AppStore<ReduxState>> {
                on { getStateFlow() } doReturn stateFlow
                on { dispatch(any()) } doAnswer { }
            }

            val configuration = CallCompositeConfiguration()
            configuration.callCompositeEventsHandler.setOnErrorHandler(mock { on { handle(any()) } doAnswer { } })

            val errorHandler = ErrorHandler(configuration, mockAppStore)

            // act
            val job = launch {
                errorHandler.start()
            }
            stateFlow.value = appState

            // assert
            verify(mockAppStore, times(1)).dispatch(
                argThat { action ->
                    action is ErrorAction.EmergencyExit
                }
            )

            verify(configuration.callCompositeEventsHandler.getOnErrorHandler()!!, times(1)).handle(
                argThat { exception ->
                    exception.errorCode == CallCompositeErrorCode.TOKEN_EXPIRED
                }
            )

            job.cancel()
        }
}
