// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.presentation.manager

import android.graphics.Bitmap
import android.widget.ImageView
import com.azure.android.communication.common.CommunicationUserIdentifier
import com.azure.android.communication.ui.configuration.LocalDataOptions
import com.azure.android.communication.ui.configuration.RemoteParticipantPersonaData
import com.azure.android.communication.ui.configuration.RemoteParticipantsConfiguration
import com.azure.android.communication.ui.helper.MainCoroutineRule
import com.azure.android.communication.ui.helper.StandardTestContextProvider
import com.azure.android.communication.ui.model.ParticipantInfoModel
import com.azure.android.communication.ui.model.StreamType
import com.azure.android.communication.ui.model.VideoStreamModel
import com.azure.android.communication.ui.persona.PersonaData
import com.azure.android.communication.ui.persona.SetPersonaDataResult
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
    fun avatarViewManager_call_localDataOption_then_returnNullIfLocalDataOptionNotSet() {
        // arrange
        val mockAppStore = mock<AppStore<ReduxState>>()
        val remoteParticipantsConfiguration = RemoteParticipantsConfiguration()
        val avatarViewManager = AvatarViewManager(
            StandardTestContextProvider(),
            mockAppStore,
            null,
            remoteParticipantsConfiguration
        )

        // act
        val result = avatarViewManager.localDataOptions

        // assert
        Assert.assertEquals(
            null,
            result
        )
    }

    @Test
    fun avatarViewManager_call_localDataOptionSet_then_returnDisplayName_ifLocalDataOptionIsSetWithRenderedDisplayName() {
        // arrange
        val mockAppStore = mock<AppStore<ReduxState>>()
        val remoteParticipantsConfiguration = RemoteParticipantsConfiguration()
        val avatarViewManager = AvatarViewManager(
            StandardTestContextProvider(),
            mockAppStore,
            LocalDataOptions(PersonaData("test")),
            remoteParticipantsConfiguration
        )

        // act
        val result = avatarViewManager.localDataOptions

        // assert
        Assert.assertEquals(
            "test",
            result?.personaData?.renderedDisplayName,
        )
        Assert.assertEquals(
            null,
            result?.personaData?.avatarBitmap,
        )
    }

    @Test
    fun avatarViewManager_call_localDataOptionSet_then_returnBitMap_ifLocalDataOptionIsSetWithBitMap() {
        // arrange
        val mockAppStore = mock<AppStore<ReduxState>>()
        val mockBitMap = mock<Bitmap>()
        val remoteParticipantsConfiguration = RemoteParticipantsConfiguration()
        val avatarViewManager = AvatarViewManager(
            StandardTestContextProvider(),
            mockAppStore,
            LocalDataOptions(PersonaData(mockBitMap)),
            remoteParticipantsConfiguration
        )

        // act
        val result = avatarViewManager.localDataOptions

        // assert
        Assert.assertEquals(
            mockBitMap,
            result?.personaData?.avatarBitmap,
        )
        Assert.assertEquals(
            null,
            result?.personaData?.renderedDisplayName,
        )
    }

    @Test
    fun avatarViewManager_call_localDataOptionSet_then_returnScaleXY_ifScaleIsNotSet() {
        // arrange
        val mockAppStore = mock<AppStore<ReduxState>>()
        val mockBitMap = mock<Bitmap>()
        val remoteParticipantsConfiguration = RemoteParticipantsConfiguration()
        val avatarViewManager = AvatarViewManager(
            StandardTestContextProvider(),
            mockAppStore,
            LocalDataOptions(PersonaData(mockBitMap)),
            remoteParticipantsConfiguration
        )

        // act
        val result = avatarViewManager.localDataOptions

        // assert
        Assert.assertEquals(
            ImageView.ScaleType.FIT_XY,
            result?.personaData?.scaleType,
        )
    }

    @Test
    fun avatarViewManager_call_localDataOptionSet_then_returnScaleSet_ifScaleIsSet() {
        // arrange
        val mockAppStore = mock<AppStore<ReduxState>>()
        val mockBitMap = mock<Bitmap>()
        val remoteParticipantsConfiguration = RemoteParticipantsConfiguration()
        val avatarViewManager = AvatarViewManager(
            StandardTestContextProvider(),
            mockAppStore,
            LocalDataOptions(PersonaData(mockBitMap, ImageView.ScaleType.FIT_CENTER)),
            remoteParticipantsConfiguration
        )

        // act
        val result = avatarViewManager.localDataOptions

        // assert
        Assert.assertEquals(
            ImageView.ScaleType.FIT_CENTER,
            result?.personaData?.scaleType,
        )
    }

    @Test
    fun avatarViewManager_onSetRemoteParticipantPersonaData_returnSuccess_ifCalledWithValidParticipantID() {
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

            val remoteParticipantPersonaData = RemoteParticipantPersonaData(
                CommunicationUserIdentifier("test"),
                PersonaData("test")
            )

            // act
            val result =
                avatarViewManager.onSetRemoteParticipantPersonaData(remoteParticipantPersonaData)
            testScheduler.runCurrent()

            // assert
            Assert.assertEquals(
                SetPersonaDataResult.SUCCESS,
                result
            )
        }
    }

    @Test
    fun avatarViewManager_onSetRemoteParticipantPersonaData_returnFail_ifCalledWithInValidParticipantID() {
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

            val remoteParticipantPersonaData = RemoteParticipantPersonaData(
                CommunicationUserIdentifier("test1"),
                PersonaData("test")
            )

            // act
            val result =
                avatarViewManager.onSetRemoteParticipantPersonaData(remoteParticipantPersonaData)
            testScheduler.runCurrent()

            // assert
            Assert.assertEquals(
                SetPersonaDataResult.PARTICIPANT_NOT_IN_CALL,
                result
            )
        }
    }

    @Test
    fun avatarViewManager_onSetRemoteParticipantPersonaData_then_remoteParticipantSharedFlow_notify_subscribers_onPersonaInjected() {
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

    @Test
    fun avatarViewManager_onSetRemoteParticipantPersonaData_then_remoteParticipantSharedFlow_notify_subscribers_onPersonaUpdated() {
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

            val remoteParticipantPersonaDataUpdated = RemoteParticipantPersonaData(
                CommunicationUserIdentifier("test"),
                PersonaData("testUpdated")
            )

            // act
            avatarViewManager.onSetRemoteParticipantPersonaData(remoteParticipantPersonaData)
            testScheduler.runCurrent()

            // assert
            Assert.assertEquals(
                remoteParticipantPersonaData.personaData,
                resultList[0]["test"]
            )

            // act
            avatarViewManager.onSetRemoteParticipantPersonaData(remoteParticipantPersonaDataUpdated)
            testScheduler.runCurrent()

            // assert
            Assert.assertEquals(
                remoteParticipantPersonaDataUpdated.personaData,
                resultList[1]["test"]
            )

            flowJob.cancel()
        }
    }

    @Test
    fun avatarViewManager_onSetRemoteParticipantPersonaData_then_remoteParticipantSharedFlow_notify_subscribers_onPersonaWithBitmapInjected() {
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

            val mockBitmap = mock<Bitmap>()
            val remoteParticipantPersonaData = RemoteParticipantPersonaData(
                CommunicationUserIdentifier("test"),
                PersonaData(mockBitmap)
            )

            // act
            avatarViewManager.onSetRemoteParticipantPersonaData(remoteParticipantPersonaData)
            testScheduler.runCurrent()

            // assert
            Assert.assertEquals(
                remoteParticipantPersonaData.personaData.avatarBitmap,
                resultList[0]["test"]?.avatarBitmap
            )

            Assert.assertEquals(
                remoteParticipantPersonaData.personaData.scaleType,
                resultList[0]["test"]?.scaleType
            )

            flowJob.cancel()
        }
    }
}
