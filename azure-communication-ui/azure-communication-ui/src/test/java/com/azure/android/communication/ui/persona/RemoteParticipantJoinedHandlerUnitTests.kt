// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.persona

import com.azure.android.communication.ui.configuration.CallCompositeConfiguration
import com.azure.android.communication.ui.helper.MainCoroutineRule
import com.azure.android.communication.ui.redux.AppStore
import com.azure.android.communication.ui.redux.state.AppReduxState
import com.azure.android.communication.ui.redux.state.ReduxState
import com.azure.android.communication.ui.redux.state.RemoteParticipantsState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.argThat
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify

@RunWith(MockitoJUnitRunner::class)
internal class RemoteParticipantJoinedHandlerUnitTests {

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Test
    fun remoteParticipantJoinedHandler_onStateChange_withNoRemoteParticipant_callsNothing() =
        runTest {
            // arrange
            val appState = AppReduxState("")
            appState.remoteParticipantState = RemoteParticipantsState(mutableMapOf(), 0)

            val stateFlow: MutableStateFlow<ReduxState> = MutableStateFlow(AppReduxState(""))
            val mockAppStore = mock<AppStore<ReduxState>> {}

            val configuration = CallCompositeConfiguration()
            configuration.callCompositeEventsHandler.setOnRemoteParticipantJoinedHandler(mock { })

            val remoteParticipantJoinedHandler =
                RemoteParticipantJoinedHandler(configuration, mockAppStore, mock { })

            // act
            val job = launch {
                remoteParticipantJoinedHandler.start()
            }

            stateFlow.value = appState

            // assert
            verify(mockAppStore, times(0)).dispatch(
                argThat { action ->
                    action is Any
                }
            )

            verify(
                configuration.callCompositeEventsHandler.getOnRemoteParticipantJoinedHandler()!!,
                times(0)
            ).handle(
                argThat { action ->
                    action is Any
                }
            )

            job.cancel()
        }
}
