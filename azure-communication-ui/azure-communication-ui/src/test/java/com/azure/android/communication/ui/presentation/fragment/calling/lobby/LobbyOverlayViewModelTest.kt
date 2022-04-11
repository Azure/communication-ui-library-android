// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.presentation.fragment.calling.lobby

import com.azure.android.communication.ui.helper.MainCoroutineRule
import com.azure.android.communication.ui.redux.state.CallingStatus
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
internal class LobbyOverlayViewModelTest {

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Test
    fun lobbyOverlayViewModel_when_callingStateChange_then_notifyLobbyState() =
        mainCoroutineRule.testDispatcher.runBlockingTest {

            // arrange
            val viewModel = LobbyOverlayViewModel()
            viewModel.init(
                CallingStatus.CONNECTED,
            )

            val modelFlow = mutableListOf<Boolean>()
            val displayLobbyJob = launch {
                viewModel.getDisplayLobbyOverlayFlow().toList(modelFlow)
            }

            // act
            viewModel.update(
                CallingStatus.IN_LOBBY,
            )

            // assert
            Assert.assertEquals(2, modelFlow.count())
            Assert.assertEquals(false, modelFlow[0])
            Assert.assertEquals(true, modelFlow[1])

            displayLobbyJob.cancel()
        }
}
