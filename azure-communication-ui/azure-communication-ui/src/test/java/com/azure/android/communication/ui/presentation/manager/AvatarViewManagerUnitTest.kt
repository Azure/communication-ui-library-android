// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.presentation.manager

import com.azure.android.communication.common.CommunicationUserIdentifier
import com.azure.android.communication.ui.configuration.RemoteParticipantPersonaData
import com.azure.android.communication.ui.configuration.RemoteParticipantsConfiguration
import com.azure.android.communication.ui.helper.MainCoroutineRule
import com.azure.android.communication.ui.helper.StandardTestContextProvider
import com.azure.android.communication.ui.model.ParticipantInfoModel
import com.azure.android.communication.ui.model.StreamType
import com.azure.android.communication.ui.model.VideoStreamModel
import com.azure.android.communication.ui.persona.PersonaData
import com.azure.android.communication.ui.redux.AppStore
import com.azure.android.communication.ui.redux.state.AppReduxState
import com.azure.android.communication.ui.redux.state.ReduxState
import com.azure.android.communication.ui.redux.state.RemoteParticipantsState
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock

@RunWith(MockitoJUnitRunner::class)
internal class AvatarViewManagerUnitTest {

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Test
    fun avatarViewManager_update_then_remoteParticipantSharedFlow_notify_subscribers() {
        runTest {
            // arrange
            val reduxState = AppReduxState("")
            reduxState.remoteParticipantState =
                RemoteParticipantsState(
                    mapOf(
                        Pair(
                            "test",
                            ParticipantInfoModel(
                                displayName = "user one",
                                userIdentifier = "test",
                                isMuted = true,
                                isSpeaking = true,
                                cameraVideoStreamModel = VideoStreamModel(
                                    videoStreamID = "video",
                                    StreamType.VIDEO
                                ),
                                screenShareVideoStreamModel = VideoStreamModel(
                                    videoStreamID = "video",
                                    StreamType.SCREEN_SHARING
                                ),
                                modifiedTimestamp = 456,
                                speakingTimestamp = 567
                            )
                        )
                    ),
                    123
                )
            val mockAppStore = mock<AppStore<ReduxState>> {
                on { getCurrentState() } doReturn reduxState
            }
            val remoteParticipantsConfiguration = RemoteParticipantsConfiguration()
            val avatarViewManager = AvatarViewManager(
                StandardTestContextProvider(),
                mockAppStore,
                null,
                remoteParticipantsConfiguration
            )

            val resultList =
                mutableListOf<Map<String, PersonaData>>()

            val flowJob = launch {
                avatarViewManager.getRemoteParticipantsPersonaSharedFlow()
                    .toList(resultList)
            }

            val remoteParticipantPersonaData = RemoteParticipantPersonaData(
                CommunicationUserIdentifier("test"),
                PersonaData("test")
            )

            // act
            avatarViewManager.onSetRemoteParticipantPersonaData(remoteParticipantPersonaData)
            testScheduler.runCurrent()

            // assert
            Assert.assertEquals(
                remoteParticipantPersonaData.personaData,
                resultList[0]["test"]
            )

            flowJob.cancel()
        }
    }
}
