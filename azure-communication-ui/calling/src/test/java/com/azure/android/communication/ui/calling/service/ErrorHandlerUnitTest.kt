// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.service

import com.azure.android.communication.ui.calling.ACSBaseTestCoroutine
import com.azure.android.communication.ui.calling.configuration.CallCompositeConfiguration
import com.azure.android.communication.ui.calling.error.CallCompositeError
import com.azure.android.communication.ui.calling.error.CallStateError
import com.azure.android.communication.ui.calling.error.ErrorCode
import com.azure.android.communication.ui.calling.error.ErrorCode.Companion.CALL_END_FAILED
import com.azure.android.communication.ui.calling.error.ErrorHandler
import com.azure.android.communication.ui.calling.models.CallCompositeErrorCode
import com.azure.android.communication.ui.calling.models.CallCompositeEventCode.Companion.CALL_DECLINED
import com.azure.android.communication.ui.calling.models.CallCompositeEventCode.Companion.CALL_EVICTED
import com.azure.android.communication.ui.calling.redux.AppStore
import com.azure.android.communication.ui.calling.redux.action.ErrorAction
import com.azure.android.communication.ui.calling.redux.state.AppReduxState
import com.azure.android.communication.ui.calling.redux.state.AudioDeviceSelectionStatus
import com.azure.android.communication.ui.calling.redux.state.AudioOperationalStatus
import com.azure.android.communication.ui.calling.redux.state.AudioState
import com.azure.android.communication.ui.calling.redux.state.BluetoothState
import com.azure.android.communication.ui.calling.redux.state.CameraDeviceSelectionStatus
import com.azure.android.communication.ui.calling.redux.state.CameraOperationalStatus
import com.azure.android.communication.ui.calling.redux.state.CameraState
import com.azure.android.communication.ui.calling.redux.state.CameraTransmissionStatus
import com.azure.android.communication.ui.calling.redux.state.ErrorState
import com.azure.android.communication.ui.calling.redux.state.LocalUserState
import com.azure.android.communication.ui.calling.redux.state.ReduxState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
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

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
internal class ErrorHandlerUnitTest : ACSBaseTestCoroutine() {

    @Test
    fun errorHandler_onStateChange_withNoError_callsNothing() =
        runScopedTest {
            // arrange
            val appState = AppReduxState("", false, false)
            appState.errorState = ErrorState(null, null)

            val stateFlow: MutableStateFlow<ReduxState> = MutableStateFlow(
                AppReduxState(
                    "",
                    false,
                    false,
                )
            )
            val mockAppStore = mock<AppStore<ReduxState>> {
                on { getStateFlow() } doReturn stateFlow
            }

            val configuration = CallCompositeConfiguration()
            configuration.callCompositeEventsHandler.addOnErrorEventHandler(mock { })

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

            verify(configuration.callCompositeEventsHandler.getOnErrorHandlers().single(), times(0)).handle(
                argThat { action ->
                    action is Any
                }
            )

            job.cancel()
        }

    @Test
    fun errorHandler_onStateChange_withCameraError_doNotCallException() =
        runScopedTest {
            // arrange
            val appState = AppReduxState("", false, false)
            val error = Exception("Camera error")
            appState.errorState = ErrorState(null, null)
            appState.localParticipantState = LocalUserState(
                CameraState(
                    CameraOperationalStatus.OFF, CameraDeviceSelectionStatus.FRONT,
                    CameraTransmissionStatus.REMOTE,
                    2,
                    CallCompositeError(ErrorCode.TURN_CAMERA_OFF_FAILED, error),
                ),
                AudioState(
                    AudioOperationalStatus.OFF,
                    AudioDeviceSelectionStatus.SPEAKER_SELECTED,
                    BluetoothState(available = false, deviceName = "bluetooth")
                ),
                videoStreamID = null,
                displayName = "name",
                localParticipantRole = null
            )

            val stateFlow: MutableStateFlow<ReduxState> = MutableStateFlow(
                AppReduxState(
                    "",
                    false,
                    false,
                )
            )
            val mockAppStore = mock<AppStore<ReduxState>> {
                on { getStateFlow() } doReturn stateFlow
            }

            val configuration = CallCompositeConfiguration()
            configuration.callCompositeEventsHandler.addOnErrorEventHandler(mock { })
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

            verify(configuration.callCompositeEventsHandler.getOnErrorHandlers().single(), times(0)).handle(
                argThat { exception ->
                    exception is Any
                }
            )

            job.cancel()
        }

    @Test
    fun errorHandler_onStateChange_withMicError_doNotCallException() =
        runScopedTest {
            // arrange
            val appState = AppReduxState("", false, false)
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
                    BluetoothState(available = false, deviceName = "bluetooth"),
                    CallCompositeError(ErrorCode.TURN_MIC_OFF_FAILED, error),
                ),
                videoStreamID = null,
                displayName = "name",
                localParticipantRole = null
            )

            val stateFlow: MutableStateFlow<ReduxState> = MutableStateFlow(
                AppReduxState(
                    "",
                    false,
                    false,
                )
            )
            val mockAppStore = mock<AppStore<ReduxState>> {
                on { getStateFlow() } doReturn stateFlow
            }

            val configuration = CallCompositeConfiguration()
            configuration.callCompositeEventsHandler.addOnErrorEventHandler(mock {})

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

            verify(configuration.callCompositeEventsHandler.getOnErrorHandlers().single(), times(0)).handle(
                argThat { exception ->
                    exception is Any
                }
            )
            job.cancel()
        }

    @Test
    fun errorHandler_onStateChange_withCallStateErrorTokenExpired_callsOnException() =
        runScopedTest {
            // arrange
            val appState = AppReduxState("", false, false)
            appState.errorState =
                ErrorState(null, CallStateError(ErrorCode.TOKEN_EXPIRED, null))

            val stateFlow: MutableStateFlow<ReduxState> = MutableStateFlow(
                AppReduxState(
                    "",
                    false,
                    false,
                )
            )
            val mockAppStore = mock<AppStore<ReduxState>> {
                on { getStateFlow() } doReturn stateFlow
                on { dispatch(any()) } doAnswer { }
            }

            val configuration = CallCompositeConfiguration()
            configuration.callCompositeEventsHandler.addOnErrorEventHandler(mock { on { handle(any()) } doAnswer { } })
            configuration.callCompositeEventsHandler.addOnErrorEventHandler(mock { on { handle(any()) } doAnswer { } })

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

            verify(configuration.callCompositeEventsHandler.getOnErrorHandlers().elementAt(0), times(1)).handle(
                argThat { exception ->
                    exception.errorCode == CallCompositeErrorCode.TOKEN_EXPIRED
                }
            )

            verify(configuration.callCompositeEventsHandler.getOnErrorHandlers().elementAt(1), times(1)).handle(
                argThat { exception ->
                    exception.errorCode == CallCompositeErrorCode.TOKEN_EXPIRED
                }
            )

            job.cancel()
        }

    @Test
    fun errorHandler_onStateChange_withCallStateErrorCallEvicted_callsNothing() =
        runScopedTest {
            // arrange
            val appState = AppReduxState("", false, false)
            appState.errorState =
                ErrorState(
                    null,
                    CallStateError(CALL_END_FAILED, CALL_EVICTED)
                )

            val stateFlow: MutableStateFlow<ReduxState> = MutableStateFlow(
                AppReduxState(
                    "",
                    false,
                    false,
                )
            )
            val mockAppStore = mock<AppStore<ReduxState>> {
                on { getStateFlow() } doReturn stateFlow
            }

            val configuration = CallCompositeConfiguration()
            configuration.callCompositeEventsHandler.addOnErrorEventHandler(mock())

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

            verify(configuration.callCompositeEventsHandler.getOnErrorHandlers().single(), times(0)).handle(
                argThat { exception ->
                    exception is Any
                }
            )

            job.cancel()
        }

    @Test
    fun errorHandler_onStateChange_withCallStateErrorCallDeclined_callsNothing() =
        runScopedTest {
            // arrange
            val appState = AppReduxState("", false, false)
            appState.errorState =
                ErrorState(
                    null,
                    CallStateError(CALL_END_FAILED, CALL_DECLINED)
                )

            val stateFlow: MutableStateFlow<ReduxState> = MutableStateFlow(
                AppReduxState(
                    "",
                    false,
                    false,
                )
            )
            val mockAppStore = mock<AppStore<ReduxState>> {
                on { getStateFlow() } doReturn stateFlow
            }

            val configuration = CallCompositeConfiguration()
            configuration.callCompositeEventsHandler.addOnErrorEventHandler(mock())

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

            verify(configuration.callCompositeEventsHandler.getOnErrorHandlers().single(), times(0)).handle(
                argThat { exception ->
                    exception is Any
                }
            )

            job.cancel()
        }

    @Test
    fun errorHandler_onStateChange_withNetworkError_notifyCallJoinFailed() =
        runScopedTest {
            // arrange
            val appState = AppReduxState("", false, false)
            appState.errorState =
                ErrorState(null, CallStateError(ErrorCode.NETWORK_NOT_AVAILABLE, null))

            val stateFlow: MutableStateFlow<ReduxState> = MutableStateFlow(
                AppReduxState(
                    "",
                    false,
                    false,
                )
            )
            val mockAppStore = mock<AppStore<ReduxState>> {
                on { getStateFlow() } doReturn stateFlow
            }

            val configuration = CallCompositeConfiguration()
            configuration.callCompositeEventsHandler.addOnErrorEventHandler(mock { on { handle(any()) } doAnswer { } })

            val errorHandler = ErrorHandler(configuration, mockAppStore)

            // act
            val job = launch {
                errorHandler.start()
            }
            stateFlow.value = appState

            // assert
            verify(configuration.callCompositeEventsHandler.getOnErrorHandlers().elementAt(0), times(1)).handle(
                argThat { exception ->
                    exception.errorCode == CallCompositeErrorCode.CALL_JOIN_FAILED
                }
            )

            job.cancel()
        }
}
