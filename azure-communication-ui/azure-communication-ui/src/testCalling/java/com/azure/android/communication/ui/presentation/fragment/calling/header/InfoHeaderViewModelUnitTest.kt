// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.presentation.fragment.calling.header

import com.azure.android.communication.ui.calling.presentation.fragment.calling.header.InfoHeaderViewModel
import com.azure.android.communication.ui.helper.MainCoroutineRule
import com.azure.android.communication.ui.calling.model.ParticipantInfoModel
import com.azure.android.communication.ui.calling.redux.state.AppReduxState
import com.azure.android.communication.ui.calling.redux.state.CallingState
import com.azure.android.communication.ui.calling.redux.state.CallingStatus
import com.azure.android.communication.ui.calling.redux.state.RemoteParticipantsState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.mock

@RunWith(MockitoJUnitRunner::class)
internal class InfoHeaderViewModelUnitTest {

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @ExperimentalCoroutinesApi
    @Test
    fun floatingHeaderViewModel_update_then_numberOfParticipantsFlowReflectsUpdate() {
        mainCoroutineRule.testDispatcher.runBlockingTest {

            val appState = AppReduxState("")

            val participantInfoModel1 = mock<ParticipantInfoModel> {}
            val participantInfoModel2 = mock<ParticipantInfoModel> {}
            val participantInfoModel3 = mock<ParticipantInfoModel> {}
            val expectedParticipantMap: Map<String, ParticipantInfoModel> = mapOf(
                "p1" to participantInfoModel1,
                "p2" to participantInfoModel2,
                "p3" to participantInfoModel3
            )
            val timestamp: Number = System.currentTimeMillis()

            appState.remoteParticipantState = RemoteParticipantsState(
                expectedParticipantMap,
                timestamp
            )
            appState.callState = CallingState(
                CallingStatus.CONNECTED,
                joinCallIsRequested = false,
                isRecording = false,
                isTranscribing = false
            )

            val floatingHeaderViewModel = InfoHeaderViewModel()
            floatingHeaderViewModel.init(appState.callState.callingStatus, expectedParticipantMap.count())

            val resultListFromNumberOfParticipantsFlow =
                mutableListOf<Int>()

            val flowJob = launch {
                floatingHeaderViewModel.getNumberOfParticipantsFlow()
                    .toList(resultListFromNumberOfParticipantsFlow)
            }

            // act
            floatingHeaderViewModel.update(expectedParticipantMap.count())

            // assert
            Assert.assertEquals(
                expectedParticipantMap.size,
                resultListFromNumberOfParticipantsFlow[0]
            )

            flowJob.cancel()
        }
    }
}
