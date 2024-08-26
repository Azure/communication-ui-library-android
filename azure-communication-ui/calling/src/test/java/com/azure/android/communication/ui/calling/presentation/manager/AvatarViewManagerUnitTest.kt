// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.manager

import android.graphics.Bitmap
import android.widget.ImageView
import com.azure.android.communication.ui.calling.ACSBaseTestCoroutine
import com.azure.android.communication.ui.calling.configuration.RemoteParticipantViewData
import com.azure.android.communication.ui.calling.configuration.RemoteParticipantsConfiguration
import com.azure.android.communication.ui.calling.models.CallCompositeLocalOptions
import com.azure.android.communication.ui.calling.models.CallCompositeParticipantViewData
import com.azure.android.communication.ui.calling.models.CallCompositeSetParticipantViewDataResult
import com.azure.android.communication.ui.calling.models.ParticipantInfoModel
import com.azure.android.communication.ui.calling.models.ParticipantStatus
import com.azure.android.communication.ui.calling.models.StreamType
import com.azure.android.communication.ui.calling.models.VideoStreamModel
import com.azure.android.communication.ui.calling.redux.AppStore
import com.azure.android.communication.ui.calling.redux.state.AppReduxState
import com.azure.android.communication.ui.calling.redux.state.ReduxState
import com.azure.android.communication.ui.calling.redux.state.RemoteParticipantsState
import com.azure.android.communication.ui.calling.service.sdk.CommunicationIdentifier
import com.azure.android.communication.ui.calling.helper.StandardTestContextProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
        val result = avatarViewManager.callCompositeLocalOptions

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
            CallCompositeLocalOptions(CallCompositeParticipantViewData().setDisplayName("test")),
            remoteParticipantsConfiguration
        )

        // act
        val result = avatarViewManager.callCompositeLocalOptions

        // assert
        Assert.assertEquals(
            "test",
            result?.participantViewData?.displayName,
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
            CallCompositeLocalOptions(CallCompositeParticipantViewData().setAvatarBitmap(mockBitMap)),
            remoteParticipantsConfiguration
        )

        // act
        val result = avatarViewManager.callCompositeLocalOptions

        // assert
        Assert.assertEquals(
            mockBitMap,
            result?.participantViewData?.avatarBitmap,
        )
        Assert.assertEquals(
            null,
            result?.participantViewData?.displayName,
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
            CallCompositeLocalOptions(CallCompositeParticipantViewData().setAvatarBitmap(mockBitMap)),
            remoteParticipantsConfiguration
        )

        // act
        val result = avatarViewManager.callCompositeLocalOptions

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
            CallCompositeLocalOptions(
                CallCompositeParticipantViewData()
                    .setAvatarBitmap(mockBitMap).setScaleType(ImageView.ScaleType.FIT_CENTER)
            ),
            remoteParticipantsConfiguration
        )

        // act
        val result = avatarViewManager.callCompositeLocalOptions

        // assert
        Assert.assertEquals(
            ImageView.ScaleType.FIT_CENTER,
            result?.participantViewData?.scaleType,
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun avatarViewManager_onSetRemoteParticipantPersonaData_returnSuccess_ifCalledWithValidParticipantID() {
        runScopedTest {
            // arrange
            val reduxState = AppReduxState("", false, false, localOptions = localOptions)
            reduxState.remoteParticipantState =
                RemoteParticipantsState(
                    mapOf(
                        Pair(
                            "test",
                            ParticipantInfoModel(
                                displayName = "user one",
                                userIdentifier = "test",
                                isMuted = true,
                                isCameraDisabled = false,

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
                                participantStatus = ParticipantStatus.HOLD,
                            )
                        )
                    ),
                    123,
                    listOf(),
                    0,
                    lobbyErrorCode = null,
                    totalParticipantCount = 1,
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
                CommunicationIdentifier.CommunicationUserIdentifier("test"),
                CallCompositeParticipantViewData().setDisplayName("test")
            )

            // act
            val result =
                avatarViewManager.onSetParticipantViewData(remoteParticipantPersonaData)
            testScheduler.runCurrent()

            // assert
            Assert.assertEquals(
                CallCompositeSetParticipantViewDataResult.SUCCESS,
                result
            )
        }
    }

    @Test
    fun avatarViewManager_onSetRemoteParticipantPersonaData_returnFail_ifCalledWithInValidParticipantID() {
        runScopedTest {
            // arrange
            val reduxState = AppReduxState("", false, false, localOptions = localOptions)
            reduxState.remoteParticipantState =
                RemoteParticipantsState(
                    mapOf(
                        Pair(
                            "test",
                            ParticipantInfoModel(
                                displayName = "user one",
                                userIdentifier = "test",
                                isMuted = true,
                                isCameraDisabled = false,
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
                                participantStatus = ParticipantStatus.HOLD,
                            )
                        )
                    ),
                    123,
                    listOf(),
                    0,
                    lobbyErrorCode = null,
                    totalParticipantCount = 1,
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
                CommunicationIdentifier.CommunicationUserIdentifier("test1"),
                CallCompositeParticipantViewData().setDisplayName("test")
            )

            // act
            val result =
                avatarViewManager.onSetParticipantViewData(remoteParticipantPersonaData)
            testScheduler.runCurrent()

            // assert
            Assert.assertEquals(
                CallCompositeSetParticipantViewDataResult.PARTICIPANT_NOT_IN_CALL,
                result
            )
        }
    }

    @Test
    fun avatarViewManager_onSetRemoteParticipantPersonaData_then_remoteParticipantSharedFlow_notify_subscribers_onPersonaInjected() {
        runScopedTest {
            // arrange
            val reduxState = AppReduxState("", false, false, localOptions = localOptions)
            reduxState.remoteParticipantState =
                RemoteParticipantsState(
                    mapOf(
                        Pair(
                            "test",
                            ParticipantInfoModel(
                                displayName = "user one",
                                userIdentifier = "test",
                                isMuted = true,
                                isCameraDisabled = false,
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
                                participantStatus = ParticipantStatus.HOLD,
                            )
                        )
                    ),
                    123,
                    listOf(),
                    0,
                    lobbyErrorCode = null,
                    totalParticipantCount = 1,
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
                mutableListOf<Map<String, CallCompositeParticipantViewData>>()

            val flowJob = launch {
                avatarViewManager.getRemoteParticipantsPersonaSharedFlow()
                    .toList(resultList)
            }

            val remoteParticipantPersonaData = RemoteParticipantViewData(
                CommunicationIdentifier.CommunicationUserIdentifier("test"),
                CallCompositeParticipantViewData().setDisplayName("test")
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
            val reduxState = AppReduxState("", false, false, localOptions = localOptions)
            reduxState.remoteParticipantState =
                RemoteParticipantsState(
                    mapOf(
                        Pair(
                            "test",
                            ParticipantInfoModel(
                                displayName = "user one",
                                userIdentifier = "test",
                                isMuted = true,
                                isCameraDisabled = false,
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
                                participantStatus = ParticipantStatus.HOLD,
                            )
                        )
                    ),
                    123,
                    listOf(),
                    0,
                    lobbyErrorCode = null,
                    totalParticipantCount = 1,
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
                mutableListOf<Map<String, CallCompositeParticipantViewData>>()

            val flowJob = launch {
                avatarViewManager.getRemoteParticipantsPersonaSharedFlow()
                    .toList(resultList)
            }

            val remoteParticipantPersonaData = RemoteParticipantViewData(
                CommunicationIdentifier.CommunicationUserIdentifier("test"),
                CallCompositeParticipantViewData().setDisplayName("test")
            )

            val remoteParticipantPersonaDataUpdated = RemoteParticipantViewData(
                CommunicationIdentifier.CommunicationUserIdentifier("test"),
                CallCompositeParticipantViewData().setDisplayName("testUpdated")
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
            val reduxState = AppReduxState("", false, false, localOptions = localOptions)
            reduxState.remoteParticipantState =
                RemoteParticipantsState(
                    mapOf(
                        Pair(
                            "test",
                            ParticipantInfoModel(
                                displayName = "user one",
                                userIdentifier = "test",
                                isMuted = true,
                                isCameraDisabled = false,
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
                                participantStatus = ParticipantStatus.HOLD,
                            )
                        )
                    ),
                    123,
                    listOf(),
                    0,
                    lobbyErrorCode = null,
                    totalParticipantCount = 1,
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
                mutableListOf<Map<String, CallCompositeParticipantViewData>>()

            val flowJob = launch {
                avatarViewManager.getRemoteParticipantsPersonaSharedFlow()
                    .toList(resultList)
            }

            val mockBitmap = mock<Bitmap>()
            val remoteParticipantPersonaData = RemoteParticipantViewData(
                CommunicationIdentifier.CommunicationUserIdentifier("test"),
                CallCompositeParticipantViewData().setAvatarBitmap(mockBitmap)
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
            val reduxState = AppReduxState("", false, false, localOptions = localOptions)
            reduxState.remoteParticipantState =
                RemoteParticipantsState(
                    mapOf(
                        Pair(
                            "test",
                            ParticipantInfoModel(
                                displayName = "user one",
                                userIdentifier = "test",
                                isMuted = true,
                                isCameraDisabled = false,
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
                                participantStatus = ParticipantStatus.HOLD,
                            )
                        )
                    ),
                    123,
                    listOf(),
                    0,
                    lobbyErrorCode = null,
                    totalParticipantCount = 1,
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
                mutableListOf<Map<String, CallCompositeParticipantViewData>>()

            val flowJob = launch {
                avatarViewManager.getRemoteParticipantsPersonaSharedFlow()
                    .toList(resultList)
            }

            val mockBitmap = mock<Bitmap>()
            val remoteParticipantPersonaData = RemoteParticipantViewData(
                CommunicationIdentifier.CommunicationUserIdentifier("test"),
                CallCompositeParticipantViewData().setAvatarBitmap(mockBitmap)
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
            val reduxState = AppReduxState("", false, false, localOptions = localOptions)
            reduxState.remoteParticipantState =
                RemoteParticipantsState(
                    mapOf(
                        Pair(
                            "test",
                            ParticipantInfoModel(
                                displayName = "user one",
                                userIdentifier = "test",
                                isMuted = true,
                                isCameraDisabled = false,
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
                                participantStatus = ParticipantStatus.HOLD,
                            )
                        )
                    ),
                    123,
                    listOf(),
                    0,
                    lobbyErrorCode = null,
                    totalParticipantCount = 1,
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
                mutableListOf<Map<String, CallCompositeParticipantViewData>>()

            val flowJob = launch {
                avatarViewManager.getRemoteParticipantsPersonaSharedFlow()
                    .toList(resultList)
            }

            val mockBitmap = mock<Bitmap>()
            val remoteParticipantPersonaData = RemoteParticipantViewData(
                CommunicationIdentifier.CommunicationUserIdentifier("test"),
                CallCompositeParticipantViewData().setAvatarBitmap(mockBitmap)
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
