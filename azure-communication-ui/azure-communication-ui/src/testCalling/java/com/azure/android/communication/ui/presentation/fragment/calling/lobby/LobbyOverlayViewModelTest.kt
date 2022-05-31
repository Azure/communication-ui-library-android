// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.presentation.fragment.calling.lobby

import com.azure.android.communication.ui.calling.redux.state.CallingStatus
import com.azure.android.communication.ui.ACSBaseTestCoroutine
import com.azure.android.communication.ui.calling.presentation.fragment.calling.lobby.LobbyOverlayViewModel
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
internal class LobbyOverlayViewModelTest : ACSBaseTestCoroutine() {

    @Test
    fun lobbyOverlayViewModel_when_callingStateChange_then_notifyLobbyState() =
        runScopedTest {

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
