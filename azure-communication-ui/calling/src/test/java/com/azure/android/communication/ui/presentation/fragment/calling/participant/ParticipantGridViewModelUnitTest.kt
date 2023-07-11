// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.presentation.fragment.calling.participant

import com.azure.android.communication.ui.calling.models.ParticipantInfoModel
import com.azure.android.communication.ui.calling.models.StreamType
import com.azure.android.communication.ui.calling.models.VideoStreamModel
import com.azure.android.communication.ui.calling.presentation.fragment.calling.participant.grid.ParticipantGridCellViewModel
import com.azure.android.communication.ui.calling.presentation.fragment.calling.participant.grid.ParticipantGridViewModel
import com.azure.android.communication.ui.calling.presentation.fragment.factories.ParticipantGridCellViewModelFactory
import com.azure.android.communication.ui.ACSBaseTestCoroutine
import com.azure.android.communication.ui.calling.models.ParticipantStatus
import com.azure.android.communication.ui.calling.redux.state.PictureInPictureStatus
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
internal class ParticipantGridViewModelUnitTest : ACSBaseTestCoroutine() {

    @ExperimentalCoroutinesApi
    @Test
    fun participantGridViewModel_update_then_notifyRemoteParticipantsSharedFlow() =
        runScopedTest {
            // arrange
            val participantGridViewModel = getParticipantGridViewModel()
            val remoteParticipantsMap: MutableMap<String, ParticipantInfoModel> = mutableMapOf()
            val modifiedTimestamp: Number = System.currentTimeMillis()
            val emitResultFromRemoteParticipantsSharedFlow =
                mutableListOf<List<ParticipantGridCellViewModel>>()
            val displayName = "user"
            val id = "user"
            remoteParticipantsMap[id] = getParticipantInfoModel(displayName, id)
            val flowJob = launch {
                participantGridViewModel.getRemoteParticipantsUpdateStateFlow()
                    .toList(emitResultFromRemoteParticipantsSharedFlow)
            }
            val pipStatus = PictureInPictureStatus.NONE

            // act
            participantGridViewModel.update(modifiedTimestamp, remoteParticipantsMap, pipStatus)

            // assert
            assertEquals(
                displayName,
                emitResultFromRemoteParticipantsSharedFlow[1][0].getDisplayNameStateFlow().value
            )
            assertEquals(
                id,
                emitResultFromRemoteParticipantsSharedFlow[1][0].getParticipantUserIdentifier()
            )
            flowJob.cancel()
        }

    @ExperimentalCoroutinesApi
    @Test
    fun participantGridViewModel_update_when_inputMoreThanSixUsers_then_notifyRemoteParticipantsSharedFlowWithSixUsers() =
        runScopedTest {
            // arrange
            val participantGridViewModel = getParticipantGridViewModel()
            val remoteParticipantsMap: MutableMap<String, ParticipantInfoModel> = mutableMapOf()
            val modifiedTimestamp: Number = System.currentTimeMillis()
            val emitResultFromRemoteParticipantsSharedFlow =
                mutableListOf<List<ParticipantGridCellViewModel>>()
            var i = 0
            remoteParticipantsMap["user1"] =
                getParticipantInfoModel("user1", "user1", System.currentTimeMillis() + i++)
            remoteParticipantsMap["user2"] =
                getParticipantInfoModel("user2", "user2", System.currentTimeMillis() + i++)
            remoteParticipantsMap["user3"] =
                getParticipantInfoModel("user3", "user3", System.currentTimeMillis() + i++)
            remoteParticipantsMap["user4"] =
                getParticipantInfoModel("user4", "user4", System.currentTimeMillis() + i++)
            remoteParticipantsMap["user5"] =
                getParticipantInfoModel("user5", "user5", System.currentTimeMillis() + i++)
            remoteParticipantsMap["user6"] =
                getParticipantInfoModel("user6", "user6", System.currentTimeMillis() + i++)
            remoteParticipantsMap["user7"] =
                getParticipantInfoModel("user7", "user7", System.currentTimeMillis() + i++)
            remoteParticipantsMap["user8"] =
                getParticipantInfoModel("user8", "user8", System.currentTimeMillis() + i)

            val flowJob = launch {
                participantGridViewModel.getRemoteParticipantsUpdateStateFlow()
                    .toList(emitResultFromRemoteParticipantsSharedFlow)
            }
            val pipStatus = PictureInPictureStatus.NONE

            // act
            participantGridViewModel.update(modifiedTimestamp, remoteParticipantsMap, pipStatus)

            // assert
            assertEquals(
                6,
                emitResultFromRemoteParticipantsSharedFlow[1].size
            )

            flowJob.cancel()
        }

    @ExperimentalCoroutinesApi
    @Test
    fun participantGridViewModel_update_when_inputMoreThanSixUsers_then_notifyRemoteParticipantsSharedFlowWithSixUsersWithMaxTimeStamp() =
        runScopedTest {
            // arrange
            val participantGridViewModel = getParticipantGridViewModel()
            val remoteParticipantsMap: MutableMap<String, ParticipantInfoModel> = mutableMapOf()
            val modifiedTimestamp: Number = System.currentTimeMillis()
            val emitResultFromRemoteParticipantsSharedFlow =
                mutableListOf<List<ParticipantGridCellViewModel>>()
            remoteParticipantsMap["user1"] = getParticipantInfoModel("user1", "user1", 7)
            remoteParticipantsMap["user2"] = getParticipantInfoModel("user2", "user2", 1)
            remoteParticipantsMap["user3"] = getParticipantInfoModel("user3", "user3", 2)
            remoteParticipantsMap["user4"] = getParticipantInfoModel("user4", "user4", 6)
            remoteParticipantsMap["user5"] = getParticipantInfoModel("user5", "user5", 5)
            remoteParticipantsMap["user6"] = getParticipantInfoModel("user6", "user6", 3)
            remoteParticipantsMap["user7"] = getParticipantInfoModel("user7", "user7", 4)
            remoteParticipantsMap["user8"] = getParticipantInfoModel("user8", "user8", 8)
            remoteParticipantsMap["user9"] = getParticipantInfoModel("user9", "user9", 9)
            val expected = mutableListOf("user9", "user8", "user1", "user4", "user5", "user7")

            val flowJob = launch {
                participantGridViewModel.getRemoteParticipantsUpdateStateFlow()
                    .toList(emitResultFromRemoteParticipantsSharedFlow)
            }
            val pipStatus = PictureInPictureStatus.NONE

            // act
            participantGridViewModel.update(modifiedTimestamp, remoteParticipantsMap, pipStatus)

            // assert
            assertEquals(
                expected.size,
                emitResultFromRemoteParticipantsSharedFlow[1].size
            )

            val output = mutableListOf<String>()

            emitResultFromRemoteParticipantsSharedFlow[1].forEach {
                output.add(it.getParticipantUserIdentifier())
            }

            assertTrue(output.containsAll(expected))

            flowJob.cancel()
        }

    @ExperimentalCoroutinesApi
    @Test
    fun participantGridViewModel_update_when_calledMultipleWithSameList_then_notifyRemoteParticipantsSharedFlowOnlyOnce() =
        runScopedTest {
            // arrange
            val participantGridViewModel = getParticipantGridViewModel()
            val remoteParticipantsMap: MutableMap<String, ParticipantInfoModel> = mutableMapOf()
            val modifiedTimestamp: Number = System.currentTimeMillis()
            remoteParticipantsMap["user1"] = getParticipantInfoModel("user1", "user1", 7)
            remoteParticipantsMap["user2"] = getParticipantInfoModel("user2", "user2", 1)

            val emitResultFromRemoteParticipantsSharedFlow =
                mutableListOf<List<ParticipantGridCellViewModel>>()

            val flowJob = launch {
                participantGridViewModel.getRemoteParticipantsUpdateStateFlow()
                    .toList(emitResultFromRemoteParticipantsSharedFlow)
            }
            val pipStatus = PictureInPictureStatus.NONE

            // act
            participantGridViewModel.update(modifiedTimestamp, remoteParticipantsMap, pipStatus)
            participantGridViewModel.update(100, remoteParticipantsMap.toMutableMap(), pipStatus)

            // assert
            assertEquals(
                remoteParticipantsMap.size,
                emitResultFromRemoteParticipantsSharedFlow[1].size
            )

            assertEquals(
                2,
                emitResultFromRemoteParticipantsSharedFlow.size
            )

            val participantViewModelFirst = emitResultFromRemoteParticipantsSharedFlow[1][0]
            val participantViewModelSecond = emitResultFromRemoteParticipantsSharedFlow[1][1]

            assertTrue(participantViewModelFirst.getParticipantUserIdentifier() == "user1" && participantViewModelFirst.getDisplayNameStateFlow().value == "user1")
            assertTrue(participantViewModelSecond.getParticipantUserIdentifier() == "user2" && participantViewModelSecond.getDisplayNameStateFlow().value == "user2")

            flowJob.cancel()
        }

    @ExperimentalCoroutinesApi
    @Test
    fun participantGridViewModel_update_when_calledMultipleWithSameStateModifiedTimeStamp_then_notifyRemoteParticipantsSharedFlowOnlyOnce() =
        runScopedTest {
            // arrange
            val participantGridViewModel = getParticipantGridViewModel()
            val remoteParticipantsMap: MutableMap<String, ParticipantInfoModel> = mutableMapOf()
            val modifiedTimestamp: Number = System.currentTimeMillis()
            val emitResultFromRemoteParticipantsSharedFlow =
                mutableListOf<List<ParticipantGridCellViewModel>>()
            remoteParticipantsMap["user1"] = getParticipantInfoModel("user1", "user1", 7)
            remoteParticipantsMap["user2"] = getParticipantInfoModel("user2", "user2", 1)
            val remoteParticipantsMapNew: MutableMap<String, ParticipantInfoModel> = mutableMapOf()
            remoteParticipantsMapNew["user8"] = getParticipantInfoModel("user8", "user8", 7)
            remoteParticipantsMapNew["user2"] = getParticipantInfoModel("user2", "user2", 1)

            val flowJob = launch {
                participantGridViewModel.getRemoteParticipantsUpdateStateFlow()
                    .toList(emitResultFromRemoteParticipantsSharedFlow)
            }
            val pipStatus = PictureInPictureStatus.NONE

            // act
            participantGridViewModel.update(modifiedTimestamp, remoteParticipantsMap.toMutableMap(), pipStatus)

            // assert
            assertEquals(
                remoteParticipantsMap.size,
                emitResultFromRemoteParticipantsSharedFlow[1].size
            )

            var participantViewModelFirst = emitResultFromRemoteParticipantsSharedFlow[1][0]
            var participantViewModelSecond = emitResultFromRemoteParticipantsSharedFlow[1][1]

            assertTrue(participantViewModelFirst.getParticipantUserIdentifier() == "user1" && participantViewModelFirst.getDisplayNameStateFlow().value == "user1")
            assertTrue(participantViewModelSecond.getParticipantUserIdentifier() == "user2" && participantViewModelSecond.getDisplayNameStateFlow().value == "user2")

            // act
            participantGridViewModel.update(
                modifiedTimestamp,
                remoteParticipantsMapNew.toMutableMap(),
                pipStatus,
            )

            // assert state flow called only once
            assertEquals(
                2,
                emitResultFromRemoteParticipantsSharedFlow.size
            )

            participantViewModelFirst =
                participantGridViewModel.getRemoteParticipantsUpdateStateFlow().value[0]
            participantViewModelSecond =
                participantGridViewModel.getRemoteParticipantsUpdateStateFlow().value[1]

            assertTrue(participantViewModelFirst.getParticipantUserIdentifier() == "user1" && participantViewModelFirst.getDisplayNameStateFlow().value == "user1")
            assertTrue(participantViewModelSecond.getParticipantUserIdentifier() == "user2" && participantViewModelSecond.getDisplayNameStateFlow().value == "user2")

            flowJob.cancel()
        }

    @ExperimentalCoroutinesApi
    @Test
    fun participantGridViewModel_update_when_calledMultipleWithDifferentStateModifiedTimeStamp_then_notifyRemoteParticipantsSharedFlowTwice() =
        runScopedTest {
            // arrange
            val participantGridViewModel = getParticipantGridViewModel()
            val remoteParticipantsMap: MutableMap<String, ParticipantInfoModel> = mutableMapOf()
            val modifiedTimestamp: Number = System.currentTimeMillis()
            val emitResultFromRemoteParticipantsSharedFlow =
                mutableListOf<List<ParticipantGridCellViewModel>>()
            remoteParticipantsMap["user1"] = getParticipantInfoModel("user1", "user1", 7)
            remoteParticipantsMap["user2"] = getParticipantInfoModel("user2", "user2", 1)
            val remoteParticipantsMapNew: MutableMap<String, ParticipantInfoModel> = mutableMapOf()
            remoteParticipantsMapNew["user8"] = getParticipantInfoModel("user8", "user8", 7)
            remoteParticipantsMapNew["user2"] = getParticipantInfoModel("user2", "user2", 1)

            val flowJob = launch {
                participantGridViewModel.getRemoteParticipantsUpdateStateFlow()
                    .toList(emitResultFromRemoteParticipantsSharedFlow)
            }
            val pipStatus = PictureInPictureStatus.NONE

            // act
            participantGridViewModel.update(modifiedTimestamp, remoteParticipantsMap.toMutableMap(), pipStatus)

            // assert first update
            assertEquals(
                remoteParticipantsMap.size,
                emitResultFromRemoteParticipantsSharedFlow[1].size
            )

            var participantViewModelFirst = emitResultFromRemoteParticipantsSharedFlow[1][0]
            var participantViewModelSecond = emitResultFromRemoteParticipantsSharedFlow[1][1]

            assertTrue(participantViewModelFirst.getParticipantUserIdentifier() == "user1" && participantViewModelFirst.getDisplayNameStateFlow().value == "user1")
            assertTrue(participantViewModelSecond.getParticipantUserIdentifier() == "user2" && participantViewModelSecond.getDisplayNameStateFlow().value == "user2")

            // act again with modified timestamp
            participantGridViewModel.update(100, remoteParticipantsMapNew.toMutableMap(), pipStatus)

            // assert state flow called only once
            assertEquals(
                2,
                emitResultFromRemoteParticipantsSharedFlow.size
            )

            participantViewModelFirst =
                participantGridViewModel.getRemoteParticipantsUpdateStateFlow().value[0]
            participantViewModelSecond =
                participantGridViewModel.getRemoteParticipantsUpdateStateFlow().value[1]

            assertTrue(participantViewModelFirst.getParticipantUserIdentifier() == "user8" && participantViewModelFirst.getDisplayNameStateFlow().value == "user8")
            assertTrue(participantViewModelSecond.getParticipantUserIdentifier() == "user2" && participantViewModelSecond.getDisplayNameStateFlow().value == "user2")

            flowJob.cancel()
        }

    @ExperimentalCoroutinesApi
    @Test
    fun participantGridViewModel_update_when_calledMultipleWith_addedUserList_then_notifyRemoteParticipantsStateFlow() =
        runScopedTest {
            // arrange
            val participantGridViewModel = getParticipantGridViewModel()
            val remoteParticipantsMap: MutableMap<String, ParticipantInfoModel> = mutableMapOf()

            remoteParticipantsMap["user1"] = getParticipantInfoModel("user1", "user1", 9)
            remoteParticipantsMap["user2"] = getParticipantInfoModel("user2", "user2", 10)

            val remoteParticipantsMapNew: MutableMap<String, ParticipantInfoModel> = mutableMapOf()
            remoteParticipantsMapNew["user8"] = getParticipantInfoModel("user8", "user8", 7)
            remoteParticipantsMapNew["user2"] = getParticipantInfoModel("user2", "user2", 1)
            remoteParticipantsMapNew["user3"] = getParticipantInfoModel("user3", "user3", 4)

            val emitResultFromRemoteParticipantsSharedFlow =
                mutableListOf<List<ParticipantGridCellViewModel>>()

            val flowJob = launch {
                participantGridViewModel.getRemoteParticipantsUpdateStateFlow()
                    .toList(emitResultFromRemoteParticipantsSharedFlow)
            }
            val pipStatus = PictureInPictureStatus.NONE

            // act
            participantGridViewModel.update(100, remoteParticipantsMap.toMutableMap(), pipStatus)

            // assert first update
            assertEquals(
                remoteParticipantsMap.size,
                emitResultFromRemoteParticipantsSharedFlow[1].size
            )

            var participantViewModelFirst = emitResultFromRemoteParticipantsSharedFlow[1][0]
            var participantViewModelSecond = emitResultFromRemoteParticipantsSharedFlow[1][1]

            assertTrue(participantViewModelFirst.getParticipantUserIdentifier() == "user1" && participantViewModelFirst.getDisplayNameStateFlow().value == "user1")
            assertTrue(participantViewModelSecond.getParticipantUserIdentifier() == "user2" && participantViewModelSecond.getDisplayNameStateFlow().value == "user2")

            // act with new list
            participantGridViewModel.update(300, remoteParticipantsMapNew.toMutableMap(), pipStatus)

            // assert state updated count
            assertEquals(
                3,
                emitResultFromRemoteParticipantsSharedFlow.size
            )

            participantViewModelFirst = emitResultFromRemoteParticipantsSharedFlow[2][0]
            participantViewModelSecond = emitResultFromRemoteParticipantsSharedFlow[2][1]
            val participantViewModelThird = emitResultFromRemoteParticipantsSharedFlow[2][2]

            assertTrue(participantViewModelFirst.getParticipantUserIdentifier() == "user8" && participantViewModelFirst.getDisplayNameStateFlow().value == "user8")
            assertTrue(participantViewModelSecond.getParticipantUserIdentifier() == "user2" && participantViewModelSecond.getDisplayNameStateFlow().value == "user2")
            assertTrue(participantViewModelThird.getParticipantUserIdentifier() == "user3" && participantViewModelThird.getDisplayNameStateFlow().value == "user3")

            flowJob.cancel()
        }

    @ExperimentalCoroutinesApi
    @Test
    fun participantGridViewModel_update_when_calledMultipleWith_removedUserList_then_notifyRemoteParticipantsStateFlow() =
        runScopedTest {
            // arrange
            val participantGridViewModel = getParticipantGridViewModel()
            val remoteParticipantsMap: MutableMap<String, ParticipantInfoModel> = mutableMapOf()
            remoteParticipantsMap["user1"] = getParticipantInfoModel("user1", "user1", 9)
            remoteParticipantsMap["user2"] = getParticipantInfoModel("user2", "user2", 10)
            remoteParticipantsMap["user11"] = getParticipantInfoModel("user11", "user11", 91)
            remoteParticipantsMap["user12"] = getParticipantInfoModel("user12", "user12", 101)

            val remoteParticipantsMapNew: MutableMap<String, ParticipantInfoModel> = mutableMapOf()
            remoteParticipantsMapNew["user1"] = getParticipantInfoModel("user1", "user1", 7)
            remoteParticipantsMapNew["user2"] = getParticipantInfoModel("user2", "user2", 1)
            remoteParticipantsMapNew["user3"] = getParticipantInfoModel("user3", "user3", 4)

            val emitResultFromRemoteParticipantsSharedFlow =
                mutableListOf<List<ParticipantGridCellViewModel>>()
            val flowJob = launch {
                participantGridViewModel.getRemoteParticipantsUpdateStateFlow()
                    .toList(emitResultFromRemoteParticipantsSharedFlow)
            }
            val pipStatus = PictureInPictureStatus.NONE

            // act
            participantGridViewModel.update(89, remoteParticipantsMap.toMutableMap(), pipStatus)

            // assert
            assertEquals(
                remoteParticipantsMap.size,
                emitResultFromRemoteParticipantsSharedFlow[1].size
            )

            var participantViewModelFirst = emitResultFromRemoteParticipantsSharedFlow[1][0]
            var participantViewModelSecond = emitResultFromRemoteParticipantsSharedFlow[1][1]
            var participantViewModelThird = emitResultFromRemoteParticipantsSharedFlow[1][2]
            val participantViewModelFourth = emitResultFromRemoteParticipantsSharedFlow[1][3]

            assertTrue(participantViewModelFirst.getParticipantUserIdentifier() == "user1" && participantViewModelFirst.getDisplayNameStateFlow().value == "user1")
            assertTrue(participantViewModelSecond.getParticipantUserIdentifier() == "user2" && participantViewModelSecond.getDisplayNameStateFlow().value == "user2")
            assertTrue(participantViewModelThird.getParticipantUserIdentifier() == "user11" && participantViewModelThird.getDisplayNameStateFlow().value == "user11")
            assertTrue(participantViewModelFourth.getParticipantUserIdentifier() == "user12" && participantViewModelFourth.getDisplayNameStateFlow().value == "user12")

            // act for new list
            participantGridViewModel.update(300, remoteParticipantsMapNew.toMutableMap(), pipStatus)

            // assert new list
            assertEquals(
                remoteParticipantsMapNew.size,
                emitResultFromRemoteParticipantsSharedFlow[2].size
            )

            assertEquals(
                3,
                emitResultFromRemoteParticipantsSharedFlow.size
            )

            participantViewModelFirst = emitResultFromRemoteParticipantsSharedFlow[2][0]
            participantViewModelSecond = emitResultFromRemoteParticipantsSharedFlow[2][1]
            participantViewModelThird = emitResultFromRemoteParticipantsSharedFlow[2][2]

            assertTrue(participantViewModelFirst.getParticipantUserIdentifier() == "user1" && participantViewModelFirst.getDisplayNameStateFlow().value == "user1")
            assertTrue(participantViewModelSecond.getParticipantUserIdentifier() == "user2" && participantViewModelSecond.getDisplayNameStateFlow().value == "user2")
            assertTrue(participantViewModelThird.getParticipantUserIdentifier() == "user3" && participantViewModelThird.getDisplayNameStateFlow().value == "user3")

            flowJob.cancel()
        }

    @ExperimentalCoroutinesApi
    @Test
    fun participantGridViewModel_update_when_calledWith_moreThanSix_then_notifyRemoteParticipantsShared_with_sortedModifiedList() =
        runScopedTest {
            // arrange
            val participantGridViewModel = getParticipantGridViewModel()
            val remoteParticipantsMap: MutableMap<String, ParticipantInfoModel> = mutableMapOf()

            remoteParticipantsMap["user1"] = getParticipantInfoModel("user1", "user1", 9)
            remoteParticipantsMap["user21"] = getParticipantInfoModel("user21", "user21", 91)
            remoteParticipantsMap["user23"] = getParticipantInfoModel("user23", "user23", 92)
            remoteParticipantsMap["user22"] = getParticipantInfoModel("user22", "user22", 93)
            remoteParticipantsMap["user3"] = getParticipantInfoModel("user3", "user3", 11)
            remoteParticipantsMap["user4"] = getParticipantInfoModel("user4", "user4", 12)
            remoteParticipantsMap["user5"] = getParticipantInfoModel("user5", "user5", 13)
            remoteParticipantsMap["user6"] = getParticipantInfoModel("user6", "user6", 14)

            val emitResultFromRemoteParticipantsSharedFlow =
                mutableListOf<List<ParticipantGridCellViewModel>>()
            val flowJob = launch {
                participantGridViewModel.getRemoteParticipantsUpdateStateFlow()
                    .toList(emitResultFromRemoteParticipantsSharedFlow)
            }
            val pipStatus = PictureInPictureStatus.NONE

            // act
            participantGridViewModel.update(5, remoteParticipantsMap.toMutableMap(), pipStatus)

            // assert
            assertEquals(
                remoteParticipantsMap.size - 2,
                emitResultFromRemoteParticipantsSharedFlow[1].size
            )

            val participantViewModelFirst = emitResultFromRemoteParticipantsSharedFlow[1][0]
            val participantViewModelSecond = emitResultFromRemoteParticipantsSharedFlow[1][1]
            val participantViewModelThird = emitResultFromRemoteParticipantsSharedFlow[1][2]
            val participantViewModelFourth = emitResultFromRemoteParticipantsSharedFlow[1][3]
            val participantViewModelFifth = emitResultFromRemoteParticipantsSharedFlow[1][4]
            val participantViewModelSixth = emitResultFromRemoteParticipantsSharedFlow[1][5]

            assertTrue(participantViewModelFirst.getParticipantUserIdentifier() == "user22" && participantViewModelFirst.getDisplayNameStateFlow().value == "user22")
            assertTrue(participantViewModelSecond.getParticipantUserIdentifier() == "user23" && participantViewModelSecond.getDisplayNameStateFlow().value == "user23")
            assertTrue(participantViewModelThird.getParticipantUserIdentifier() == "user21" && participantViewModelThird.getDisplayNameStateFlow().value == "user21")
            assertTrue(participantViewModelFourth.getParticipantUserIdentifier() == "user6" && participantViewModelFourth.getDisplayNameStateFlow().value == "user6")
            assertTrue(participantViewModelFifth.getParticipantUserIdentifier() == "user5" && participantViewModelFifth.getDisplayNameStateFlow().value == "user5")
            assertTrue(participantViewModelSixth.getParticipantUserIdentifier() == "user4" && participantViewModelSixth.getDisplayNameStateFlow().value == "user4")

            flowJob.cancel()
        }

    @ExperimentalCoroutinesApi
    @Test
    fun participantGridViewModel_update_when_calledMultipleTimesWith_moreThanSix_then_notifyRemoteParticipantsShared_with_sortedModifiedList() =
        runScopedTest {
            // arrange
            val participantGridViewModel = getParticipantGridViewModel()
            val remoteParticipantsMap: MutableMap<String, ParticipantInfoModel> = mutableMapOf()

            remoteParticipantsMap["user1"] = getParticipantInfoModel("user1", "user1", 9)
            remoteParticipantsMap["user21"] = getParticipantInfoModel("user21", "user21", 91)
            remoteParticipantsMap["user23"] = getParticipantInfoModel("user23", "user23", 92)
            remoteParticipantsMap["user22"] = getParticipantInfoModel("user22", "user22", 93)
            remoteParticipantsMap["user3"] = getParticipantInfoModel("user3", "user3", 11)
            remoteParticipantsMap["user4"] = getParticipantInfoModel("user4", "user4", 12)
            remoteParticipantsMap["user5"] = getParticipantInfoModel("user5", "user5", 13)
            remoteParticipantsMap["user6"] = getParticipantInfoModel("user6", "user6", 14)

            val remoteParticipantsMapNew: MutableMap<String, ParticipantInfoModel> = mutableMapOf()

            remoteParticipantsMapNew["user1"] = getParticipantInfoModel("user1", "user1", 900)
            remoteParticipantsMapNew["user21"] = getParticipantInfoModel("user21", "user21", 91)
            remoteParticipantsMapNew["user23"] = getParticipantInfoModel("user23", "user23", 192)
            remoteParticipantsMapNew["user22"] = getParticipantInfoModel("user22", "user22", 93)
            remoteParticipantsMapNew["user3"] = getParticipantInfoModel("user3", "user3", 111)
            remoteParticipantsMapNew["user4"] = getParticipantInfoModel("user4", "user4", 112)
            remoteParticipantsMapNew["user5"] = getParticipantInfoModel("user5", "user5", 13)
            remoteParticipantsMapNew["user6"] = getParticipantInfoModel("user6", "user6", 14)

            val emitResultFromRemoteParticipantsSharedFlow =
                mutableListOf<List<ParticipantGridCellViewModel>>()
            val flowJob = launch {
                participantGridViewModel.getRemoteParticipantsUpdateStateFlow()
                    .toList(emitResultFromRemoteParticipantsSharedFlow)
            }
            val pipStatus = PictureInPictureStatus.NONE

            // act
            participantGridViewModel.update(5, remoteParticipantsMap.toMutableMap(), pipStatus)

            // assert
            assertEquals(
                remoteParticipantsMap.size - 2,
                emitResultFromRemoteParticipantsSharedFlow[1].size
            )

            var participantViewModelFirst = emitResultFromRemoteParticipantsSharedFlow[1][0]
            var participantViewModelSecond = emitResultFromRemoteParticipantsSharedFlow[1][1]
            var participantViewModelThird = emitResultFromRemoteParticipantsSharedFlow[1][2]
            var participantViewModelFourth = emitResultFromRemoteParticipantsSharedFlow[1][3]
            var participantViewModelFifth = emitResultFromRemoteParticipantsSharedFlow[1][4]
            var participantViewModelSixth = emitResultFromRemoteParticipantsSharedFlow[1][5]

            assertTrue(participantViewModelFirst.getParticipantUserIdentifier() == "user22" && participantViewModelFirst.getDisplayNameStateFlow().value == "user22")
            assertTrue(participantViewModelSecond.getParticipantUserIdentifier() == "user23" && participantViewModelSecond.getDisplayNameStateFlow().value == "user23")
            assertTrue(participantViewModelThird.getParticipantUserIdentifier() == "user21" && participantViewModelThird.getDisplayNameStateFlow().value == "user21")
            assertTrue(participantViewModelFourth.getParticipantUserIdentifier() == "user6" && participantViewModelFourth.getDisplayNameStateFlow().value == "user6")
            assertTrue(participantViewModelFifth.getParticipantUserIdentifier() == "user5" && participantViewModelFifth.getDisplayNameStateFlow().value == "user5")
            assertTrue(participantViewModelSixth.getParticipantUserIdentifier() == "user4" && participantViewModelSixth.getDisplayNameStateFlow().value == "user4")

            // act with new list
            participantGridViewModel.update(2, remoteParticipantsMapNew.toMutableMap(), pipStatus)

            // assert new list
            participantViewModelFirst = emitResultFromRemoteParticipantsSharedFlow[1][0]
            participantViewModelSecond = emitResultFromRemoteParticipantsSharedFlow[1][1]
            participantViewModelThird = emitResultFromRemoteParticipantsSharedFlow[1][2]
            participantViewModelFourth = emitResultFromRemoteParticipantsSharedFlow[1][3]
            participantViewModelFifth = emitResultFromRemoteParticipantsSharedFlow[1][4]
            participantViewModelSixth = emitResultFromRemoteParticipantsSharedFlow[1][5]

            assertTrue(participantViewModelFirst.getParticipantUserIdentifier() == "user22" && participantViewModelFirst.getDisplayNameStateFlow().value == "user22")
            assertTrue(participantViewModelSecond.getParticipantUserIdentifier() == "user23" && participantViewModelSecond.getDisplayNameStateFlow().value == "user23")
            assertTrue(participantViewModelThird.getParticipantUserIdentifier() == "user21" && participantViewModelThird.getDisplayNameStateFlow().value == "user21")
            assertTrue(participantViewModelFourth.getParticipantUserIdentifier() == "user1" && participantViewModelFourth.getDisplayNameStateFlow().value == "user1")
            assertTrue(participantViewModelFifth.getParticipantUserIdentifier() == "user3" && participantViewModelFifth.getDisplayNameStateFlow().value == "user3")
            assertTrue(participantViewModelSixth.getParticipantUserIdentifier() == "user4" && participantViewModelSixth.getDisplayNameStateFlow().value == "user4")

            // assert state flow not triggered
            assertEquals(
                2,
                emitResultFromRemoteParticipantsSharedFlow.size
            )

            flowJob.cancel()
        }

    @ExperimentalCoroutinesApi
    @Test
    fun participantGridViewModel_update_when_calledWithParticipantsSharingScreen_then_notifyRemoteParticipantsGrid_with_singleParticipantList() =
        runScopedTest {
            // arrange
            val participantGridViewModel = getParticipantGridViewModel()
            val remoteParticipantsMap: MutableMap<String, ParticipantInfoModel> = mutableMapOf()

            remoteParticipantsMap["user1"] = getParticipantInfoModel("user1", "user1", 9)
            remoteParticipantsMap["user21"] = getParticipantInfoModel("user21", "user21", 91)
            remoteParticipantsMap["user23"] = getParticipantInfoModel(
                "user23", "user23", 92,
                null,
                VideoStreamModel("123", StreamType.VIDEO)

            )
            remoteParticipantsMap["user22"] = getParticipantInfoModel("user22", "user22", 93)
            remoteParticipantsMap["user3"] = getParticipantInfoModel("user3", "user3", 11)
            remoteParticipantsMap["user4"] = getParticipantInfoModel("user4", "user4", 12)
            remoteParticipantsMap["user5"] = getParticipantInfoModel("user5", "user5", 13)
            remoteParticipantsMap["user6"] = getParticipantInfoModel("user6", "user6", 14)

            val remoteParticipantsMapNew: MutableMap<String, ParticipantInfoModel> = mutableMapOf()

            remoteParticipantsMapNew["user1"] = getParticipantInfoModel("user1", "user1", 900)
            remoteParticipantsMapNew["user21"] = getParticipantInfoModel("user21", "user21", 91)
            remoteParticipantsMapNew["user23"] = getParticipantInfoModel(
                "user23", "user23", 192,
                screenShareVideoStreamModel = VideoStreamModel("123", StreamType.SCREEN_SHARING)
            )
            remoteParticipantsMapNew["user22"] = getParticipantInfoModel("user22", "user22", 93)
            remoteParticipantsMapNew["user3"] = getParticipantInfoModel("user3", "user3", 111)
            remoteParticipantsMapNew["user4"] = getParticipantInfoModel("user4", "user4", 112)
            remoteParticipantsMapNew["user5"] = getParticipantInfoModel("user5", "user5", 13)
            remoteParticipantsMapNew["user6"] = getParticipantInfoModel("user6", "user6", 14)

            val emitResultFromRemoteParticipantsSharedFlow =
                mutableListOf<List<ParticipantGridCellViewModel>>()
            val flowJob = launch {
                participantGridViewModel.getRemoteParticipantsUpdateStateFlow()
                    .toList(emitResultFromRemoteParticipantsSharedFlow)
            }
            val pipStatus = PictureInPictureStatus.NONE

            // act
            participantGridViewModel.update(5, remoteParticipantsMap.toMutableMap(), pipStatus)

            // assert
            assertEquals(
                remoteParticipantsMap.size - 2,
                emitResultFromRemoteParticipantsSharedFlow[1].size
            )

            var participantViewModelFirst = emitResultFromRemoteParticipantsSharedFlow[1][0]
            val participantViewModelSecond = emitResultFromRemoteParticipantsSharedFlow[1][1]
            val participantViewModelThird = emitResultFromRemoteParticipantsSharedFlow[1][2]
            val participantViewModelFourth = emitResultFromRemoteParticipantsSharedFlow[1][3]
            val participantViewModelFifth = emitResultFromRemoteParticipantsSharedFlow[1][4]
            val participantViewModelSixth = emitResultFromRemoteParticipantsSharedFlow[1][5]

            assertTrue(participantViewModelFirst.getParticipantUserIdentifier() == "user22" && participantViewModelFirst.getDisplayNameStateFlow().value == "user22")
            assertTrue(participantViewModelSecond.getParticipantUserIdentifier() == "user23" && participantViewModelSecond.getDisplayNameStateFlow().value == "user23")
            assertTrue(participantViewModelThird.getParticipantUserIdentifier() == "user21" && participantViewModelThird.getDisplayNameStateFlow().value == "user21")
            assertTrue(participantViewModelFourth.getParticipantUserIdentifier() == "user6" && participantViewModelFourth.getDisplayNameStateFlow().value == "user6")
            assertTrue(participantViewModelFifth.getParticipantUserIdentifier() == "user5" && participantViewModelFifth.getDisplayNameStateFlow().value == "user5")
            assertTrue(participantViewModelSixth.getParticipantUserIdentifier() == "user4" && participantViewModelSixth.getDisplayNameStateFlow().value == "user4")

            // act with new list
            participantGridViewModel.update(2, remoteParticipantsMapNew.toMutableMap(), pipStatus)

            // assert new list

            assertEquals(
                1,
                emitResultFromRemoteParticipantsSharedFlow[2].size
            )

            participantViewModelFirst = emitResultFromRemoteParticipantsSharedFlow[2][0]

            assertTrue(
                participantViewModelFirst.getParticipantUserIdentifier() == "user23" &&
                    participantViewModelFirst.getDisplayNameStateFlow().value == "user23"
            )

            assertEquals(
                3,
                emitResultFromRemoteParticipantsSharedFlow.size
            )

            flowJob.cancel()
        }

    private fun getParticipantGridViewModel() = ParticipantGridViewModel(
        ParticipantGridCellViewModelFactory(),
        6
    )

    private fun getParticipantInfoModel(
        displayName: String,
        id: String,
        timestamp: Number = 0,
        screenShareVideoStreamModel: VideoStreamModel? = null,
        cameraVideoStreamModel: VideoStreamModel? = null,
    ) = ParticipantInfoModel(
        displayName,
        id,
        isMuted = true,
        isSpeaking = true,
        ParticipantStatus.CONNECTED,
        screenShareVideoStreamModel,
        cameraVideoStreamModel,
        modifiedTimestamp = timestamp,
        speakingTimestamp = timestamp
    )
}
