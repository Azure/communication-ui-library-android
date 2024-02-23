// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.calling.participantlist

import com.azure.android.communication.ui.calling.ACSBaseTestCoroutine
import com.azure.android.communication.ui.calling.models.CallCompositeLobbyErrorCode
import com.azure.android.communication.ui.calling.presentation.fragment.calling.lobby.LobbyErrorHeaderViewModel
import com.azure.android.communication.ui.calling.redux.AppStore
import com.azure.android.communication.ui.calling.redux.action.ParticipantAction
import com.azure.android.communication.ui.calling.redux.state.CallingStatus
import com.azure.android.communication.ui.calling.redux.state.ReduxState
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.argThat
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify

@RunWith(MockitoJUnitRunner::class)
internal class LobbyErrorHeaderViewModelUnitTest : ACSBaseTestCoroutine() {

    @Test
    fun lobbyErrorHeaderViewModelUnitTest_update_then_showErrorIfStateIsConnected() {
        runScopedTest {

            // arrange
            val mockAppStore = mock<AppStore<ReduxState>> {}
            val lobbyErrorHeaderViewModel = LobbyErrorHeaderViewModel(mockAppStore::dispatch)
            val resultLobbyErrorListCellStateFlow =
                mutableListOf<CallCompositeLobbyErrorCode?>()

            val resultDisplayErrorHeaderStateFlow =
                mutableListOf<Boolean?>()

            lobbyErrorHeaderViewModel.init(
                CallingStatus.DISCONNECTED,
                CallCompositeLobbyErrorCode.LOBBY_MEETING_ROLE_NOT_ALLOWED,
                true
            )

            val errorJob = launch {
                lobbyErrorHeaderViewModel.getLobbyErrorFlow()
                    .toList(resultLobbyErrorListCellStateFlow)
            }

            val displayErrorJob = launch {
                lobbyErrorHeaderViewModel.getDisplayLobbyErrorHeaderFlow()
                    .toList(resultDisplayErrorHeaderStateFlow)
            }

            // act
            lobbyErrorHeaderViewModel.update(
                CallingStatus.CONNECTED,
                CallCompositeLobbyErrorCode.LOBBY_DISABLED_BY_CONFIGURATIONS,
                true
            )
            lobbyErrorHeaderViewModel.update(
                CallingStatus.CONNECTED,
                null,
                true
            )

            // assert
            Assert.assertEquals(
                CallCompositeLobbyErrorCode.LOBBY_MEETING_ROLE_NOT_ALLOWED,
                resultLobbyErrorListCellStateFlow[0]
            )

            Assert.assertEquals(
                CallCompositeLobbyErrorCode.LOBBY_DISABLED_BY_CONFIGURATIONS,
                resultLobbyErrorListCellStateFlow[1]
            )

            Assert.assertEquals(
                null,
                resultLobbyErrorListCellStateFlow[2]
            )

            Assert.assertEquals(
                false,
                resultDisplayErrorHeaderStateFlow[0]
            )

            Assert.assertEquals(
                true,
                resultDisplayErrorHeaderStateFlow[1]
            )

            Assert.assertEquals(
                false,
                resultDisplayErrorHeaderStateFlow[2]
            )

            errorJob.cancel()
            displayErrorJob.cancel()
        }
    }

    @Test
    fun lobbyErrorHeaderViewModelUnitTest_update_then_doNotShowErrorIfShowLobbyIsFalse() {
        runScopedTest {

            // arrange
            val mockAppStore = mock<AppStore<ReduxState>> {}
            val lobbyErrorHeaderViewModel = LobbyErrorHeaderViewModel(mockAppStore::dispatch)
            val resultLobbyErrorListCellStateFlow =
                mutableListOf<CallCompositeLobbyErrorCode?>()

            val resultDisplayErrorHeaderStateFlow =
                mutableListOf<Boolean?>()

            lobbyErrorHeaderViewModel.init(
                CallingStatus.DISCONNECTED,
                CallCompositeLobbyErrorCode.LOBBY_MEETING_ROLE_NOT_ALLOWED,
                false
            )

            val errorJob = launch {
                lobbyErrorHeaderViewModel.getLobbyErrorFlow()
                    .toList(resultLobbyErrorListCellStateFlow)
            }

            val displayErrorJob = launch {
                lobbyErrorHeaderViewModel.getDisplayLobbyErrorHeaderFlow()
                    .toList(resultDisplayErrorHeaderStateFlow)
            }

            // act
            lobbyErrorHeaderViewModel.update(
                CallingStatus.CONNECTED,
                CallCompositeLobbyErrorCode.LOBBY_DISABLED_BY_CONFIGURATIONS,
                false
            )
            lobbyErrorHeaderViewModel.update(
                CallingStatus.CONNECTED,
                null,
                true
            )

            // assert
            Assert.assertEquals(
                CallCompositeLobbyErrorCode.LOBBY_MEETING_ROLE_NOT_ALLOWED,
                resultLobbyErrorListCellStateFlow[0]
            )

            Assert.assertEquals(
                CallCompositeLobbyErrorCode.LOBBY_DISABLED_BY_CONFIGURATIONS,
                resultLobbyErrorListCellStateFlow[1]
            )

            Assert.assertEquals(
                null,
                resultLobbyErrorListCellStateFlow[2]
            )

            Assert.assertEquals(
                false,
                resultDisplayErrorHeaderStateFlow[0]
            )

            Assert.assertEquals(
                1,
                resultDisplayErrorHeaderStateFlow.size
            )

            errorJob.cancel()
            displayErrorJob.cancel()
        }
    }

    @Test
    fun lobbyErrorHeaderViewModelUnitTest_dismiss_then_hideError() {
        runScopedTest {

            // arrange
            val mockAppStore = mock<AppStore<ReduxState>> {}
            val lobbyErrorHeaderViewModel = LobbyErrorHeaderViewModel(mockAppStore::dispatch)
            val resultLobbyErrorListCellStateFlow =
                mutableListOf<CallCompositeLobbyErrorCode?>()

            val resultDisplayErrorHeaderStateFlow =
                mutableListOf<Boolean?>()

            lobbyErrorHeaderViewModel.init(
                CallingStatus.CONNECTED,
                CallCompositeLobbyErrorCode.LOBBY_MEETING_ROLE_NOT_ALLOWED,
                true
            )

            val errorJob = launch {
                lobbyErrorHeaderViewModel.getLobbyErrorFlow()
                    .toList(resultLobbyErrorListCellStateFlow)
            }

            val displayErrorJob = launch {
                lobbyErrorHeaderViewModel.getDisplayLobbyErrorHeaderFlow()
                    .toList(resultDisplayErrorHeaderStateFlow)
            }

            // act
            lobbyErrorHeaderViewModel.dismiss()
            // assert
            Assert.assertEquals(
                CallCompositeLobbyErrorCode.LOBBY_MEETING_ROLE_NOT_ALLOWED,
                resultLobbyErrorListCellStateFlow[0]
            )

            Assert.assertEquals(
                1,
                resultLobbyErrorListCellStateFlow.size
            )

            Assert.assertEquals(
                true,
                resultDisplayErrorHeaderStateFlow[0]
            )

            Assert.assertEquals(
                false,
                resultDisplayErrorHeaderStateFlow[1]
            )

            Assert.assertEquals(
                2,
                resultDisplayErrorHeaderStateFlow.size
            )

            errorJob.cancel()
            displayErrorJob.cancel()
        }
    }

    @Test
    fun lobbyErrorHeaderViewModelUnitTest_close_then_fireClearLobby() {
        runScopedTest {

            // arrange
            val mockAppStore = mock<AppStore<ReduxState>> {}
            val lobbyErrorHeaderViewModel = LobbyErrorHeaderViewModel(mockAppStore::dispatch)

            // act
            lobbyErrorHeaderViewModel.close()

            // assert
            verify(mockAppStore, times(1)).dispatch(argThat { this is ParticipantAction.ClearLobbyError })
        }
    }
}
