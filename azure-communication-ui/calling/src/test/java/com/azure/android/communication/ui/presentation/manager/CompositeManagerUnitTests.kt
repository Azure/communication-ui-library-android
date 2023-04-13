// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.presentation.manager

import com.azure.android.communication.ui.ACSBaseTestCoroutine
import com.azure.android.communication.ui.calling.configuration.CallCompositeConfiguration
import com.azure.android.communication.ui.calling.presentation.manager.CompositeManager
import com.azure.android.communication.ui.calling.redux.AppStore
import com.azure.android.communication.ui.calling.redux.action.CallingAction
import com.azure.android.communication.ui.calling.redux.action.NavigationAction
import com.azure.android.communication.ui.calling.redux.state.AppReduxState
import com.azure.android.communication.ui.calling.redux.state.CallingState
import com.azure.android.communication.ui.calling.redux.state.CallingStatus
import com.azure.android.communication.ui.calling.redux.state.OperationStatus
import com.azure.android.communication.ui.calling.redux.state.ReduxState
import com.azure.android.communication.ui.helper.StandardTestContextProvider
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
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
internal class CompositeManagerUnitTests : ACSBaseTestCoroutine() {
    @Test
    fun lifecycleManager_callStateConnected_then_callEndRequestedTriggered() {
        runScopedTest {
            // Arrange
            val state = AppReduxState(displayName = "", cameraOnByDefault = false, microphoneOnByDefault = false)
            state.callState = CallingState(CallingStatus.CONNECTED, OperationStatus.NONE)
            val mockAppStore = mock<AppStore<ReduxState>> {
                on { getCurrentState() } doReturn state
                on { dispatch(any()) } doAnswer { }
            }

            val configuration = CallCompositeConfiguration()
            val compositeManager =
                CompositeManager(mockAppStore, configuration, StandardTestContextProvider())

            // Act
            compositeManager.exit()

            // Assert
            verify(mockAppStore, times(1)).dispatch(
                argThat { action ->
                    action is CallingAction.CallEndRequested
                }
            )
        }
    }

    @Test
    fun lifecycleManager_callStateNotChanged_then_navigationActionTriggered() {
        runScopedTest {
            // Arrange
            val state = AppReduxState(displayName = "", cameraOnByDefault = false, microphoneOnByDefault = false)
            state.callState = CallingState(CallingStatus.CONNECTED, OperationStatus.NONE)
            val mockAppStore = mock<AppStore<ReduxState>> {
                on { getCurrentState() } doReturn state
                on { dispatch(any()) } doAnswer { }
            }

            val configuration = CallCompositeConfiguration()
            val compositeManager =
                CompositeManager(mockAppStore, configuration, StandardTestContextProvider())

            // Act
            compositeManager.exit()
            delay(CompositeManager.MAX_WAIT_FOR_EXIT + 1)

            // Assert
            verify(mockAppStore, times(1)).dispatch(
                argThat { action ->
                    action is NavigationAction.Exit
                }
            )
        }
    }

    @Test
    fun lifecycleManager_callStateChanged_then_callEndActionTriggered() {
        runScopedTest {
            // Arrange
            val state = AppReduxState(displayName = "", cameraOnByDefault = false, microphoneOnByDefault = false)
            state.callState = CallingState(CallingStatus.CONNECTED, OperationStatus.NONE)
            val stateFlow: MutableStateFlow<ReduxState> = MutableStateFlow(state)
            val mockAppStore = mock<AppStore<ReduxState>> {
                on { getCurrentState() } doReturn state
                on { dispatch(any()) } doAnswer { }
            }

            val configuration = CallCompositeConfiguration()
            val compositeManager =
                CompositeManager(mockAppStore, configuration, StandardTestContextProvider())

            // Act
            compositeManager.exit()
            val stateNew = AppReduxState(displayName = "", cameraOnByDefault = false, microphoneOnByDefault = false)
            stateNew.callState = CallingState(CallingStatus.DISCONNECTED, OperationStatus.NONE)
            stateFlow.value = stateNew
            delay(CompositeManager.MAX_WAIT_FOR_EXIT + 1)

            // Assert
            verify(mockAppStore, times(1)).dispatch(
                argThat { action ->
                    action is CallingAction.CallEndRequested
                }
            )
        }
    }

    @Test
    fun lifecycleManager_callStateNone_then_navigationActionTriggered() {
        runScopedTest {
            // Arrange
            val state = AppReduxState(displayName = "", cameraOnByDefault = false, microphoneOnByDefault = false)
            state.callState = CallingState(CallingStatus.NONE, OperationStatus.NONE)
            val mockAppStore = mock<AppStore<ReduxState>> {
                on { getCurrentState() } doReturn state
                on { dispatch(any()) } doAnswer { }
            }

            val configuration = CallCompositeConfiguration()
            val compositeManager =
                CompositeManager(mockAppStore, configuration, StandardTestContextProvider())

            // Act
            compositeManager.exit()

            // Assert
            verify(mockAppStore, times(1)).dispatch(
                argThat { action ->
                    action is NavigationAction.Exit
                }
            )
        }
    }

    @Test
    fun lifecycleManager_callStateNone_then_onlySubscribedHandlerIsNotified() {
        runScopedTest {
            // Arrange
            val state = AppReduxState(displayName = "", cameraOnByDefault = false, microphoneOnByDefault = false)
            state.callState = CallingState(CallingStatus.NONE, OperationStatus.NONE)
            val mockAppStore = mock<AppStore<ReduxState>> {
                on { getCurrentState() } doReturn state
                on { dispatch(any()) } doAnswer { }
            }

            val configuration = CallCompositeConfiguration()
            val compositeManager =
                CompositeManager(mockAppStore, configuration, StandardTestContextProvider())

            // Act
            compositeManager.exit()

            // Assert
            verify(mockAppStore, times(1)).dispatch(
                argThat { action ->
                    action is NavigationAction.Exit
                }
            )
        }
    }
}
