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
            participantGridViewModel.update(modifiedTimestamp, remoteParticipantsMap, listOf(), 0, pipStatus)

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
                getParticipantInfoModel("user1", "user1")
            remoteParticipantsMap["user2"] =
                getParticipantInfoModel("user2", "user2")
            remoteParticipantsMap["user3"] =
                getParticipantInfoModel("user3", "user3")
            remoteParticipantsMap["user4"] =
                getParticipantInfoModel("user4", "user4")
            remoteParticipantsMap["user5"] =
                getParticipantInfoModel("user5", "user5")
            remoteParticipantsMap["user6"] =
                getParticipantInfoModel("user6", "user6")
            remoteParticipantsMap["user7"] =
                getParticipantInfoModel("user7", "user7")
            remoteParticipantsMap["user8"] =
                getParticipantInfoModel("user8", "user8")

            val flowJob = launch {
                participantGridViewModel.getRemoteParticipantsUpdateStateFlow()
                    .toList(emitResultFromRemoteParticipantsSharedFlow)
            }
            val pipStatus = PictureInPictureStatus.NONE

            val dominantSpeakersInfo = listOf("user8", "user7", "user6", "user5", "user4", "user3", "user2", "user1")

            // act
            participantGridViewModel.update(modifiedTimestamp, remoteParticipantsMap, dominantSpeakersInfo, System.currentTimeMillis(), pipStatus)

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
            remoteParticipantsMap["user1"] = getParticipantInfoModel("user1", "user1")
            remoteParticipantsMap["user2"] = getParticipantInfoModel("user2", "user2")
            remoteParticipantsMap["user3"] = getParticipantInfoModel("user3", "user3")
            remoteParticipantsMap["user4"] = getParticipantInfoModel("user4", "user4")
            remoteParticipantsMap["user5"] = getParticipantInfoModel("user5", "user5")
            remoteParticipantsMap["user6"] = getParticipantInfoModel("user6", "user6")
            remoteParticipantsMap["user7"] = getParticipantInfoModel("user7", "user7")
            remoteParticipantsMap["user8"] = getParticipantInfoModel("user8", "user8")
            remoteParticipantsMap["user9"] = getParticipantInfoModel("user9", "user9")
            val expected = mutableListOf("user9", "user8", "user1", "user4", "user5", "user7")

            val flowJob = launch {
                participantGridViewModel.getRemoteParticipantsUpdateStateFlow()
                    .toList(emitResultFromRemoteParticipantsSharedFlow)
            }
            val pipStatus = PictureInPictureStatus.NONE
            val dominantSpeakersInfo = listOf("user9", "user8", "user1", "user4", "user5", "user7", "user6", "user3", "user2")

            // act
            participantGridViewModel.update(modifiedTimestamp, remoteParticipantsMap, dominantSpeakersInfo, System.currentTimeMillis(), pipStatus)

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
            remoteParticipantsMap["user1"] = getParticipantInfoModel("user1", "user1")
            remoteParticipantsMap["user2"] = getParticipantInfoModel("user2", "user2")

            val emitResultFromRemoteParticipantsSharedFlow =
                mutableListOf<List<ParticipantGridCellViewModel>>()

            val flowJob = launch {
                participantGridViewModel.getRemoteParticipantsUpdateStateFlow()
                    .toList(emitResultFromRemoteParticipantsSharedFlow)
            }
            val pipStatus = PictureInPictureStatus.NONE
            val dominantSpeakersInfo = listOf("user1", "user2")

            // act
            participantGridViewModel.update(modifiedTimestamp, remoteParticipantsMap, dominantSpeakersInfo, System.currentTimeMillis(), pipStatus)
            participantGridViewModel.update(100, remoteParticipantsMap.toMutableMap(), dominantSpeakersInfo, 100, pipStatus)

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
            remoteParticipantsMap["user1"] = getParticipantInfoModel("user1", "user1")
            remoteParticipantsMap["user2"] = getParticipantInfoModel("user2", "user2")
            val remoteParticipantsMapNew: MutableMap<String, ParticipantInfoModel> = mutableMapOf()
            remoteParticipantsMapNew["user8"] = getParticipantInfoModel("user8", "user8")
            remoteParticipantsMapNew["user2"] = getParticipantInfoModel("user2", "user2")

            val flowJob = launch {
                participantGridViewModel.getRemoteParticipantsUpdateStateFlow()
                    .toList(emitResultFromRemoteParticipantsSharedFlow)
            }
            val pipStatus = PictureInPictureStatus.NONE
            val dominantSpeakersInfo = listOf("user1", "user2")
            val dominantSpeakersInfoNew = listOf("user8", "user2")

            // act
            participantGridViewModel.update(modifiedTimestamp, remoteParticipantsMap.toMutableMap(), dominantSpeakersInfo, modifiedTimestamp, pipStatus)

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
                dominantSpeakersInfoNew,
                modifiedTimestamp,
                pipStatus
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
            remoteParticipantsMap["user1"] = getParticipantInfoModel("user1", "user1")
            remoteParticipantsMap["user2"] = getParticipantInfoModel("user2", "user2")
            val remoteParticipantsMapNew: MutableMap<String, ParticipantInfoModel> = mutableMapOf()
            remoteParticipantsMapNew["user8"] = getParticipantInfoModel("user8", "user8")
            remoteParticipantsMapNew["user2"] = getParticipantInfoModel("user2", "user2")

            val flowJob = launch {
                participantGridViewModel.getRemoteParticipantsUpdateStateFlow()
                    .toList(emitResultFromRemoteParticipantsSharedFlow)
            }
            val pipStatus = PictureInPictureStatus.NONE

            // act
            participantGridViewModel.update(modifiedTimestamp, remoteParticipantsMap.toMutableMap(), listOf(), 0, pipStatus)

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
            participantGridViewModel.update(100, remoteParticipantsMapNew.toMutableMap(), listOf(), 0, pipStatus)

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

            remoteParticipantsMap["user1"] = getParticipantInfoModel("user1", "user1")
            remoteParticipantsMap["user2"] = getParticipantInfoModel("user2", "user2")

            val remoteParticipantsMapNew: MutableMap<String, ParticipantInfoModel> = mutableMapOf()
            remoteParticipantsMapNew["user8"] = getParticipantInfoModel("user8", "user8")
            remoteParticipantsMapNew["user2"] = getParticipantInfoModel("user2", "user2")
            remoteParticipantsMapNew["user3"] = getParticipantInfoModel("user3", "user3")

            val emitResultFromRemoteParticipantsSharedFlow =
                mutableListOf<List<ParticipantGridCellViewModel>>()

            val flowJob = launch {
                participantGridViewModel.getRemoteParticipantsUpdateStateFlow()
                    .toList(emitResultFromRemoteParticipantsSharedFlow)
            }
            val pipStatus = PictureInPictureStatus.NONE

            val dominantSpeakersInfo = listOf("user2", "user1")
            val dominantSpeakersInfoNew = listOf("user8", "user3", "user2")

            // act
            participantGridViewModel.update(100, remoteParticipantsMap.toMutableMap(), dominantSpeakersInfo, 100, pipStatus)

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
            participantGridViewModel.update(300, remoteParticipantsMapNew.toMutableMap(), dominantSpeakersInfoNew, 300, pipStatus)

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
            remoteParticipantsMap["user1"] = getParticipantInfoModel("user1", "user1")
            remoteParticipantsMap["user2"] = getParticipantInfoModel("user2", "user2")
            remoteParticipantsMap["user11"] = getParticipantInfoModel("user11", "user11")
            remoteParticipantsMap["user12"] = getParticipantInfoModel("user12", "user12")

            val remoteParticipantsMapNew: MutableMap<String, ParticipantInfoModel> = mutableMapOf()
            remoteParticipantsMapNew["user1"] = getParticipantInfoModel("user1", "user1")
            remoteParticipantsMapNew["user2"] = getParticipantInfoModel("user2", "user2")
            remoteParticipantsMapNew["user3"] = getParticipantInfoModel("user3", "user3")

            val emitResultFromRemoteParticipantsSharedFlow =
                mutableListOf<List<ParticipantGridCellViewModel>>()
            val flowJob = launch {
                participantGridViewModel.getRemoteParticipantsUpdateStateFlow()
                    .toList(emitResultFromRemoteParticipantsSharedFlow)
            }
            val pipStatus = PictureInPictureStatus.NONE
            val dominantSpeakersInfo = listOf("user12", "user11", "user2", "user1")
            val dominantSpeakersInfoNew = listOf("user1", "user3", "user2")

            // act
            participantGridViewModel.update(89, remoteParticipantsMap.toMutableMap(), dominantSpeakersInfo, 89, pipStatus)

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
            participantGridViewModel.update(300, remoteParticipantsMapNew.toMutableMap(), dominantSpeakersInfoNew, 300, pipStatus)

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

            remoteParticipantsMap["user1"] = getParticipantInfoModel("user1", "user1")
            remoteParticipantsMap["user21"] = getParticipantInfoModel("user21", "user21")
            remoteParticipantsMap["user23"] = getParticipantInfoModel("user23", "user23")
            remoteParticipantsMap["user22"] = getParticipantInfoModel("user22", "user22")
            remoteParticipantsMap["user3"] = getParticipantInfoModel("user3", "user3")
            remoteParticipantsMap["user4"] = getParticipantInfoModel("user4", "user4")
            remoteParticipantsMap["user5"] = getParticipantInfoModel("user5", "user5")
            remoteParticipantsMap["user6"] = getParticipantInfoModel("user6", "user6")

            val emitResultFromRemoteParticipantsSharedFlow =
                mutableListOf<List<ParticipantGridCellViewModel>>()
            val flowJob = launch {
                participantGridViewModel.getRemoteParticipantsUpdateStateFlow()
                    .toList(emitResultFromRemoteParticipantsSharedFlow)
            }
            val pipStatus = PictureInPictureStatus.NONE
            val dominantSpeakersInfo = listOf("user22", "user23", "user21", "user6", "user5", "user4", "user3", "user1",)

            // act
            participantGridViewModel.update(5, remoteParticipantsMap.toMutableMap(), dominantSpeakersInfo, 5, pipStatus)

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

            remoteParticipantsMap["user1"] = getParticipantInfoModel("user1", "user1")
            remoteParticipantsMap["user21"] = getParticipantInfoModel("user21", "user21")
            remoteParticipantsMap["user23"] = getParticipantInfoModel("user23", "user23")
            remoteParticipantsMap["user22"] = getParticipantInfoModel("user22", "user22")
            remoteParticipantsMap["user3"] = getParticipantInfoModel("user3", "user3")
            remoteParticipantsMap["user4"] = getParticipantInfoModel("user4", "user4")
            remoteParticipantsMap["user5"] = getParticipantInfoModel("user5", "user5")
            remoteParticipantsMap["user6"] = getParticipantInfoModel("user6", "user6")

            val remoteParticipantsMapNew: MutableMap<String, ParticipantInfoModel> = mutableMapOf()

            remoteParticipantsMapNew["user1"] = getParticipantInfoModel("user1", "user1")
            remoteParticipantsMapNew["user21"] = getParticipantInfoModel("user21", "user21")
            remoteParticipantsMapNew["user23"] = getParticipantInfoModel("user23", "user23")
            remoteParticipantsMapNew["user22"] = getParticipantInfoModel("user22", "user22")
            remoteParticipantsMapNew["user3"] = getParticipantInfoModel("user3", "user3")
            remoteParticipantsMapNew["user4"] = getParticipantInfoModel("user4", "user4")
            remoteParticipantsMapNew["user5"] = getParticipantInfoModel("user5", "user5")
            remoteParticipantsMapNew["user6"] = getParticipantInfoModel("user6", "user6")

            val emitResultFromRemoteParticipantsSharedFlow =
                mutableListOf<List<ParticipantGridCellViewModel>>()
            val flowJob = launch {
                participantGridViewModel.getRemoteParticipantsUpdateStateFlow()
                    .toList(emitResultFromRemoteParticipantsSharedFlow)
            }
            val pipStatus = PictureInPictureStatus.NONE
            val dominantSpeakersInfo = listOf("user22", "user23", "user21", "user6", "user5", "user4", "user3", "user1")
            val dominantSpeakersInfoNew = listOf("user1", "user23", "user4", "user3", "user22", "user21", "user6", "user5")

            // act
            participantGridViewModel.update(5, remoteParticipantsMap.toMutableMap(), dominantSpeakersInfo, 5, pipStatus)

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
            participantGridViewModel.update(10, remoteParticipantsMapNew.toMutableMap(), dominantSpeakersInfoNew, 10, pipStatus)

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

            remoteParticipantsMap["user1"] = getParticipantInfoModel("user1", "user1")
            remoteParticipantsMap["user21"] = getParticipantInfoModel("user21", "user21")
            remoteParticipantsMap["user23"] = getParticipantInfoModel(
                "user23", "user23",
                null,
                VideoStreamModel("123", StreamType.VIDEO)

            )
            remoteParticipantsMap["user22"] = getParticipantInfoModel("user22", "user22")
            remoteParticipantsMap["user3"] = getParticipantInfoModel("user3", "user3")
            remoteParticipantsMap["user4"] = getParticipantInfoModel("user4", "user4")
            remoteParticipantsMap["user5"] = getParticipantInfoModel("user5", "user5")
            remoteParticipantsMap["user6"] = getParticipantInfoModel("user6", "user6")

            val remoteParticipantsMapNew: MutableMap<String, ParticipantInfoModel> = mutableMapOf()

            remoteParticipantsMapNew["user1"] = getParticipantInfoModel("user1", "user1")
            remoteParticipantsMapNew["user23"] = getParticipantInfoModel(
                "user23", "user23",
                screenShareVideoStreamModel = VideoStreamModel("123", StreamType.SCREEN_SHARING)
            )
            remoteParticipantsMapNew["user4"] = getParticipantInfoModel("user4", "user4")
            remoteParticipantsMapNew["user3"] = getParticipantInfoModel("user3", "user3")
            remoteParticipantsMapNew["user22"] = getParticipantInfoModel("user22", "user22")
            remoteParticipantsMapNew["user21"] = getParticipantInfoModel("user21", "user21")
            remoteParticipantsMapNew["user6"] = getParticipantInfoModel("user6", "user6")
            remoteParticipantsMapNew["user5"] = getParticipantInfoModel("user5", "user5")

            val emitResultFromRemoteParticipantsSharedFlow =
                mutableListOf<List<ParticipantGridCellViewModel>>()
            val flowJob = launch {
                participantGridViewModel.getRemoteParticipantsUpdateStateFlow()
                    .toList(emitResultFromRemoteParticipantsSharedFlow)
            }
            val pipStatus = PictureInPictureStatus.NONE

            val dominantSpeakersInfo = listOf("user22", "user23", "user21", "user6", "user5", "user4", "user3", "user1")
            val dominantSpeakersInfoNew = listOf("user1", "user23", "user4", "user3", "user22", "user21", "user6", "user5")

            // act
            participantGridViewModel.update(5, remoteParticipantsMap.toMutableMap(), dominantSpeakersInfo, 5, pipStatus)

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
            participantGridViewModel.update(10, remoteParticipantsMapNew.toMutableMap(), dominantSpeakersInfoNew, 10, pipStatus)

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

    @ExperimentalCoroutinesApi
    @Test
    fun participantGridViewModel_update_when_dominantSpeakersAreNotPresent_then_notifyRemoteParticipantsGrid_by_alphabet() =
        runScopedTest {
            // arrange
            val participantGridViewModel = getParticipantGridViewModel()
            val remoteParticipantsMap: MutableMap<String, ParticipantInfoModel> = mutableMapOf()

            remoteParticipantsMap["user1"] = getParticipantInfoModel("user1", "user1")
            remoteParticipantsMap["user2"] = getParticipantInfoModel("user2", "user2")
            remoteParticipantsMap["user3"] = getParticipantInfoModel("user3", "user3")
            remoteParticipantsMap["user4"] = getParticipantInfoModel("user4", "user4")
            remoteParticipantsMap["user5"] = getParticipantInfoModel("user5", "user5")
            remoteParticipantsMap["user6"] = getParticipantInfoModel("user6", "user6")
            remoteParticipantsMap["user7"] = getParticipantInfoModel("user7", "user7")
            remoteParticipantsMap["user8"] = getParticipantInfoModel("user8", "user8")

            val emitResultFromRemoteParticipantsSharedFlow =
                mutableListOf<List<ParticipantGridCellViewModel>>()
            val flowJob = launch {
                participantGridViewModel.getRemoteParticipantsUpdateStateFlow()
                    .toList(emitResultFromRemoteParticipantsSharedFlow)
            }

            val dominantSpeakersInfo = listOf<String>()
            val pipStatus = PictureInPictureStatus.NONE

            // act
            participantGridViewModel.update(5, remoteParticipantsMap.toMutableMap(), dominantSpeakersInfo, 5, pipStatus)

            // assert

            val emittedResult = emitResultFromRemoteParticipantsSharedFlow[1]

            assertEquals(
                6,
                emittedResult.size
            )

            val participantViewModelFirst = emittedResult[0]
            val participantViewModelSecond = emittedResult[1]
            val participantViewModelThird = emittedResult[2]
            val participantViewModelFourth = emittedResult[3]
            val participantViewModelFifth = emittedResult[4]
            val participantViewModelSixth = emittedResult[5]

            assertTrue(participantViewModelFirst.getParticipantUserIdentifier() == "user1" && participantViewModelFirst.getDisplayNameStateFlow().value == "user1")
            assertTrue(participantViewModelSecond.getParticipantUserIdentifier() == "user2" && participantViewModelSecond.getDisplayNameStateFlow().value == "user2")
            assertTrue(participantViewModelThird.getParticipantUserIdentifier() == "user3" && participantViewModelThird.getDisplayNameStateFlow().value == "user3")
            assertTrue(participantViewModelFourth.getParticipantUserIdentifier() == "user4" && participantViewModelFourth.getDisplayNameStateFlow().value == "user4")
            assertTrue(participantViewModelFifth.getParticipantUserIdentifier() == "user5" && participantViewModelFifth.getDisplayNameStateFlow().value == "user5")
            assertTrue(participantViewModelSixth.getParticipantUserIdentifier() == "user6" && participantViewModelSixth.getDisplayNameStateFlow().value == "user6")

            flowJob.cancel()
        }

    @ExperimentalCoroutinesApi
    @Test
    fun participantGridViewModel_update_when_dominantSpeakersArePartiallyPresent_then_notifyRemoteParticipantsGrid_by_dominantTheByAlphabet() =
        runScopedTest {
            // arrange
            val participantGridViewModel = getParticipantGridViewModel()
            val remoteParticipantsMap: MutableMap<String, ParticipantInfoModel> = mutableMapOf()

            remoteParticipantsMap["user1"] = getParticipantInfoModel("user1", "user1")
            remoteParticipantsMap["user2"] = getParticipantInfoModel("user2", "user2")
            remoteParticipantsMap["user3"] = getParticipantInfoModel("user3", "user3")
            remoteParticipantsMap["user4"] = getParticipantInfoModel("user4", "user4")
            remoteParticipantsMap["user5"] = getParticipantInfoModel("user5", "user5")
            remoteParticipantsMap["user6"] = getParticipantInfoModel("user6", "user6")
            remoteParticipantsMap["user7"] = getParticipantInfoModel("user7", "user7")
            remoteParticipantsMap["user8"] = getParticipantInfoModel("user8", "user8")

            val emitResultFromRemoteParticipantsSharedFlow =
                mutableListOf<List<ParticipantGridCellViewModel>>()
            val flowJob = launch {
                participantGridViewModel.getRemoteParticipantsUpdateStateFlow()
                    .toList(emitResultFromRemoteParticipantsSharedFlow)
            }

            val dominantSpeakersInfo = listOf("user6", "user7")
            val pipStatus = PictureInPictureStatus.NONE

            // act
            participantGridViewModel.update(5, remoteParticipantsMap.toMutableMap(), dominantSpeakersInfo, 5, pipStatus)

            // assert

            val emittedResult = emitResultFromRemoteParticipantsSharedFlow[1]

            assertEquals(
                6,
                emittedResult.size
            )

            val participantViewModelFirst = emittedResult[0]
            val participantViewModelSecond = emittedResult[1]
            val participantViewModelThird = emittedResult[2]
            val participantViewModelFourth = emittedResult[3]
            val participantViewModelFifth = emittedResult[4]
            val participantViewModelSixth = emittedResult[5]

            assertTrue(participantViewModelFirst.getParticipantUserIdentifier() == "user6" && participantViewModelFirst.getDisplayNameStateFlow().value == "user6")
            assertTrue(participantViewModelSecond.getParticipantUserIdentifier() == "user7" && participantViewModelSecond.getDisplayNameStateFlow().value == "user7")
            assertTrue(participantViewModelThird.getParticipantUserIdentifier() == "user1" && participantViewModelThird.getDisplayNameStateFlow().value == "user1")
            assertTrue(participantViewModelFourth.getParticipantUserIdentifier() == "user2" && participantViewModelFourth.getDisplayNameStateFlow().value == "user2")
            assertTrue(participantViewModelFifth.getParticipantUserIdentifier() == "user3" && participantViewModelFifth.getDisplayNameStateFlow().value == "user3")
            assertTrue(participantViewModelSixth.getParticipantUserIdentifier() == "user4" && participantViewModelSixth.getDisplayNameStateFlow().value == "user4")

            flowJob.cancel()
        }

    @ExperimentalCoroutinesApi
    @Test
    fun participantGridViewModel_update_when_dominantSpeakersArePartiallyPresent_then_notifyRemoteParticipantsGrid_by_dominantTheByIsVideoOn() =
        runScopedTest {
            // arrange
            val participantGridViewModel = getParticipantGridViewModel()
            val remoteParticipantsMap: MutableMap<String, ParticipantInfoModel> = mutableMapOf()

            val videoStreamModel = VideoStreamModel("videoStreamId", StreamType.VIDEO)

            remoteParticipantsMap["user1"] = getParticipantInfoModel("user1", "user1")
            remoteParticipantsMap["user2"] = getParticipantInfoModel("user2", "user2")
            remoteParticipantsMap["user3"] = getParticipantInfoModel("user3", "user3")
            remoteParticipantsMap["user4"] = getParticipantInfoModel("user4", "user4")
            remoteParticipantsMap["user5"] = getParticipantInfoModel("user5", "user5", cameraVideoStreamModel = videoStreamModel)
            remoteParticipantsMap["user6"] = getParticipantInfoModel("user6", "user6")
            remoteParticipantsMap["user7"] = getParticipantInfoModel("user7", "user7")
            remoteParticipantsMap["user8"] = getParticipantInfoModel("user8", "user8")
            remoteParticipantsMap["user9"] = getParticipantInfoModel("user9", "user9")

            val emitResultFromRemoteParticipantsSharedFlow =
                mutableListOf<List<ParticipantGridCellViewModel>>()
            val flowJob = launch {
                participantGridViewModel.getRemoteParticipantsUpdateStateFlow()
                    .toList(emitResultFromRemoteParticipantsSharedFlow)
            }

            val dominantSpeakersInfo = listOf("user7", "user8")
            val pipStatus = PictureInPictureStatus.NONE

            // act
            participantGridViewModel.update(5, remoteParticipantsMap.toMutableMap(), dominantSpeakersInfo, 5, pipStatus)

            // assert

            val emittedResult = emitResultFromRemoteParticipantsSharedFlow[1]

            assertEquals(
                6,
                emittedResult.size
            )

            val participantViewModelFirst = emittedResult[0]
            val participantViewModelSecond = emittedResult[1]
            val participantViewModelThird = emittedResult[2]
            val participantViewModelFourth = emittedResult[3]
            val participantViewModelFifth = emittedResult[4]
            val participantViewModelSixth = emittedResult[5]

            assertTrue(participantViewModelFirst.getParticipantUserIdentifier() == "user7" && participantViewModelFirst.getDisplayNameStateFlow().value == "user7")
            assertTrue(participantViewModelSecond.getParticipantUserIdentifier() == "user8" && participantViewModelSecond.getDisplayNameStateFlow().value == "user8")
            assertTrue(participantViewModelThird.getParticipantUserIdentifier() == "user5" && participantViewModelThird.getDisplayNameStateFlow().value == "user5")
            assertTrue(participantViewModelFourth.getParticipantUserIdentifier() == "user1" && participantViewModelFourth.getDisplayNameStateFlow().value == "user1")
            assertTrue(participantViewModelFifth.getParticipantUserIdentifier() == "user2" && participantViewModelFifth.getDisplayNameStateFlow().value == "user2")
            assertTrue(participantViewModelSixth.getParticipantUserIdentifier() == "user3" && participantViewModelSixth.getDisplayNameStateFlow().value == "user3")

            flowJob.cancel()
        }

    private fun getParticipantGridViewModel() = ParticipantGridViewModel(
        ParticipantGridCellViewModelFactory(),
        6
    )

    private fun getParticipantInfoModel(
        displayName: String,
        id: String,
        screenShareVideoStreamModel: VideoStreamModel? = null,
        cameraVideoStreamModel: VideoStreamModel? = null,
        isMuted: Boolean = true,
        isSpeaking: Boolean = false,
    ) = ParticipantInfoModel(
        displayName,
        id,
        isMuted = isMuted,
        isSpeaking = isSpeaking,
        ParticipantStatus.CONNECTED,
        screenShareVideoStreamModel,
        cameraVideoStreamModel,
        modifiedTimestamp = 0,
    )
}
