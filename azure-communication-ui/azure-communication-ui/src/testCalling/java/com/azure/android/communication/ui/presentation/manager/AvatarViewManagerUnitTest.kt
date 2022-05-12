// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.presentation.manager

import android.graphics.Bitmap
import android.widget.ImageView
import com.azure.android.communication.common.CommunicationUserIdentifier
import com.azure.android.communication.ui.calling.models.LocalSettings
import com.azure.android.communication.ui.calling.configuration.RemoteParticipantViewData
import com.azure.android.communication.ui.calling.configuration.RemoteParticipantsConfiguration
import com.azure.android.communication.ui.ACSBaseTestCoroutine
import com.azure.android.communication.ui.helper.StandardTestContextProvider
import com.azure.android.communication.ui.calling.models.ParticipantInfoModel
import com.azure.android.communication.ui.calling.models.StreamType
import com.azure.android.communication.ui.calling.models.VideoStreamModel
import com.azure.android.communication.ui.calling.models.ParticipantViewData
import com.azure.android.communication.ui.calling.models.SetParticipantViewDataResult
import com.azure.android.communication.ui.calling.presentation.manager.AvatarViewManager
import com.azure.android.communication.ui.calling.redux.AppStore
import com.azure.android.communication.ui.calling.redux.state.AppReduxState
import com.azure.android.communication.ui.calling.redux.state.ReduxState
import com.azure.android.communication.ui.calling.redux.state.RemoteParticipantsState
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock

@RunWith(MockitoJUnitRunner::class)
internal class AvatarViewManagerUnitTest : ACSBaseTestCoroutine() {

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
        val result = avatarViewManager.localSettings

        // assert
        Assert.assertEquals(
            null,
            result
        )
    }

    @Test
    fun avatarViewManager_call_localSettingsSet_then_returnDisplayName_ifLocalDataOptionIsSetWithRenderedDisplayName() {
        // arrange
        val mockAppStore = mock<AppStore<ReduxState>>()
        val remoteParticipantsConfiguration = RemoteParticipantsConfiguration()
        val avatarViewManager = AvatarViewManager(
            StandardTestContextProvider(),
            mockAppStore,
            LocalSettings(ParticipantViewData("test")),
            remoteParticipantsConfiguration
        )

        // act
        val result = avatarViewManager.localSettings

        // assert
        Assert.assertEquals(
            "test",
            result?.participantViewData?.renderedDisplayName,
        )
        Assert.assertEquals(
            null,
            result?.participantViewData?.avatarBitmap,
        )
    }

    @Test
    fun avatarViewManager_call_localSettingsSet_then_returnBitMap_ifLocalDataOptionIsSetWithBitMap() {
        // arrange
        val mockAppStore = mock<AppStore<ReduxState>>()
        val mockBitMap = mock<Bitmap>()
        val remoteParticipantsConfiguration = RemoteParticipantsConfiguration()
        val avatarViewManager = AvatarViewManager(
            StandardTestContextProvider(),
            mockAppStore,
            LocalSettings(ParticipantViewData(mockBitMap)),
            remoteParticipantsConfiguration
        )

        // act
        val result = avatarViewManager.localSettings

        // assert
        Assert.assertEquals(
            mockBitMap,
            result?.participantViewData?.avatarBitmap,
        )
        Assert.assertEquals(
            null,
            result?.participantViewData?.renderedDisplayName,
        )
    }

    @Test
    fun avatarViewManager_call_localSettingsSet_then_returnScaleXY_ifScaleIsNotSet() {
        // arrange
        val mockAppStore = mock<AppStore<ReduxState>>()
        val mockBitMap = mock<Bitmap>()
        val remoteParticipantsConfiguration = RemoteParticipantsConfiguration()
        val avatarViewManager = AvatarViewManager(
            StandardTestContextProvider(),
            mockAppStore,
            LocalSettings(ParticipantViewData(mockBitMap)),
            remoteParticipantsConfiguration
        )

        // act
        val result = avatarViewManager.localSettings

        // assert
        Assert.assertEquals(
            ImageView.ScaleType.FIT_XY,
            result?.participantViewData?.scaleType,
        )
    }

    @Test
    fun avatarViewManager_call_localSettingsSet_then_returnScaleSet_ifScaleIsSet() {
        // arrange
        val mockAppStore = mock<AppStore<ReduxState>>()
        val mockBitMap = mock<Bitmap>()
        val remoteParticipantsConfiguration = RemoteParticipantsConfiguration()
        val avatarViewManager = AvatarViewManager(
            StandardTestContextProvider(),
            mockAppStore,
            LocalSettings(ParticipantViewData(mockBitMap, ImageView.ScaleType.FIT_CENTER)),
            remoteParticipantsConfiguration
        )

        // act
        val result = avatarViewManager.localSettings

        // assert
        Assert.assertEquals(
            ImageView.ScaleType.FIT_CENTER,
            result?.participantViewData?.scaleType,
        )
    }

    @Test
    fun avatarViewManager_onSetRemoteParticipantPersonaData_returnSuccess_ifCalledWithValidParticipantID() {
        runScopedTest {
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

            val remoteParticipantPersonaData = RemoteParticipantViewData(
                CommunicationUserIdentifier("test"),
                ParticipantViewData().setRenderedDisplayName("test")
            )

            // act
            val result =
                avatarViewManager.onSetParticipantViewData(remoteParticipantPersonaData)
            testScheduler.runCurrent()

            // assert
            Assert.assertEquals(
                SetParticipantViewDataResult.SUCCESS,
                result
            )
        }
    }

    @Test
    fun avatarViewManager_onSetRemoteParticipantPersonaData_returnFail_ifCalledWithInValidParticipantID() {
        runScopedTest {
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

            val remoteParticipantPersonaData = RemoteParticipantViewData(
                CommunicationUserIdentifier("test1"),
                ParticipantViewData("test")
            )

            // act
            val result =
                avatarViewManager.onSetParticipantViewData(remoteParticipantPersonaData)
            testScheduler.runCurrent()

            // assert
            Assert.assertEquals(
                SetParticipantViewDataResult.PARTICIPANT_NOT_IN_CALL,
                result
            )
        }
    }

    @Test
    fun avatarViewManager_onSetRemoteParticipantPersonaData_then_remoteParticipantSharedFlow_notify_subscribers_onPersonaInjected() {
        runScopedTest {
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
                mutableListOf<Map<String, ParticipantViewData>>()

            val flowJob = launch {
                avatarViewManager.getRemoteParticipantsPersonaSharedFlow()
                    .toList(resultList)
            }

            val remoteParticipantPersonaData = RemoteParticipantViewData(
                CommunicationUserIdentifier("test"),
                ParticipantViewData("test")
            )

            // act
            avatarViewManager.onSetParticipantViewData(remoteParticipantPersonaData)
            testScheduler.runCurrent()

            // assert
            Assert.assertEquals(
                remoteParticipantPersonaData.participantViewData,
                resultList[0]["test"]
            )

            flowJob.cancel()
        }
    }

    @Test
    fun avatarViewManager_onSetRemoteParticipantPersonaData_then_remoteParticipantSharedFlow_notify_subscribers_onPersonaUpdated() {
        runScopedTest {
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
                mutableListOf<Map<String, ParticipantViewData>>()

            val flowJob = launch {
                avatarViewManager.getRemoteParticipantsPersonaSharedFlow()
                    .toList(resultList)
            }

            val remoteParticipantPersonaData = RemoteParticipantViewData(
                CommunicationUserIdentifier("test"),
                ParticipantViewData("test")
            )

            val remoteParticipantPersonaDataUpdated = RemoteParticipantViewData(
                CommunicationUserIdentifier("test"),
                ParticipantViewData("testUpdated")
            )

            // act
            avatarViewManager.onSetParticipantViewData(remoteParticipantPersonaData)
            testScheduler.runCurrent()

            // assert
            Assert.assertEquals(
                remoteParticipantPersonaData.participantViewData,
                resultList[0]["test"]
            )

            // act
            avatarViewManager.onSetParticipantViewData(remoteParticipantPersonaDataUpdated)
            testScheduler.runCurrent()

            // assert
            Assert.assertEquals(
                remoteParticipantPersonaDataUpdated.participantViewData,
                resultList[1]["test"]
            )

            flowJob.cancel()
        }
    }

    @Test
    fun avatarViewManager_onSetRemoteParticipantPersonaData_then_remoteParticipantSharedFlow_notify_subscribers_onPersonaWithBitmapInjected() {
        runScopedTest {
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
                mutableListOf<Map<String, ParticipantViewData>>()

            val flowJob = launch {
                avatarViewManager.getRemoteParticipantsPersonaSharedFlow()
                    .toList(resultList)
            }

            val mockBitmap = mock<Bitmap>()
            val remoteParticipantPersonaData = RemoteParticipantViewData(
                CommunicationUserIdentifier("test"),
                ParticipantViewData(mockBitmap)
            )

            // act
            avatarViewManager.onSetParticipantViewData(remoteParticipantPersonaData)
            testScheduler.runCurrent()

            // assert
            Assert.assertEquals(
                remoteParticipantPersonaData.participantViewData.avatarBitmap,
                resultList[0]["test"]?.avatarBitmap
            )

            Assert.assertEquals(
                remoteParticipantPersonaData.participantViewData.scaleType,
                resultList[0]["test"]?.scaleType
            )

            flowJob.cancel()
        }
    }

    @Test
    fun avatarViewManager_onRemoveParticipantPersonaData_then_remoteParticipantSharedFlow_notify_subscribers_ifIdentifierIsValid() {
        runScopedTest {
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
                mutableListOf<Map<String, ParticipantViewData>>()

            val flowJob = launch {
                avatarViewManager.getRemoteParticipantsPersonaSharedFlow()
                    .toList(resultList)
            }

            val mockBitmap = mock<Bitmap>()
            val remoteParticipantPersonaData = RemoteParticipantViewData(
                CommunicationUserIdentifier("test"),
                ParticipantViewData(mockBitmap)
            )

            // act
            avatarViewManager.onSetParticipantViewData(remoteParticipantPersonaData)
            testScheduler.runCurrent()

            // assert
            Assert.assertEquals(
                remoteParticipantPersonaData.participantViewData.avatarBitmap,
                resultList[0]["test"]?.avatarBitmap
            )

            Assert.assertEquals(
                remoteParticipantPersonaData.participantViewData.scaleType,
                resultList[0]["test"]?.scaleType
            )

            Assert.assertEquals(
                1,
                resultList.size
            )

            // act
            avatarViewManager.onRemoveParticipantViewData("test")
            testScheduler.runCurrent()

            // assert
            Assert.assertEquals(
                0,
                resultList[1].size
            )

            Assert.assertEquals(
                2,
                resultList.size
            )

            flowJob.cancel()
        }
    }

    @Test
    fun avatarViewManager_onRemoveParticipantPersonaData_then_remoteParticipantSharedFlow_doesNot_subscribers_ifIdentifierIsNotValid() {
        runScopedTest {
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
                mutableListOf<Map<String, ParticipantViewData>>()

            val flowJob = launch {
                avatarViewManager.getRemoteParticipantsPersonaSharedFlow()
                    .toList(resultList)
            }

            val mockBitmap = mock<Bitmap>()
            val remoteParticipantPersonaData = RemoteParticipantViewData(
                CommunicationUserIdentifier("test"),
                ParticipantViewData(mockBitmap)
            )

            // act
            avatarViewManager.onSetParticipantViewData(remoteParticipantPersonaData)
            testScheduler.runCurrent()

            // assert
            Assert.assertEquals(
                remoteParticipantPersonaData.participantViewData.avatarBitmap,
                resultList[0]["test"]?.avatarBitmap
            )

            Assert.assertEquals(
                remoteParticipantPersonaData.participantViewData.scaleType,
                resultList[0]["test"]?.scaleType
            )

            Assert.assertEquals(
                1,
                resultList.size
            )

            // act
            avatarViewManager.onRemoveParticipantViewData("test1")
            testScheduler.runCurrent()

            // assert
            Assert.assertEquals(
                1,
                resultList.size
            )

            flowJob.cancel()
        }
    }
}
