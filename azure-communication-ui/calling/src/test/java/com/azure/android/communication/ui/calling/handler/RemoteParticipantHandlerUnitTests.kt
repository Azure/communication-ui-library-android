// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.handler

import com.azure.android.communication.ui.calling.ACSBaseTestCoroutine
import com.azure.android.communication.ui.calling.CallCompositeEventHandler
import com.azure.android.communication.ui.calling.configuration.CallCompositeConfiguration
import com.azure.android.communication.ui.calling.configuration.RemoteParticipantsConfigurationHandler
import com.azure.android.communication.ui.calling.handlers.RemoteParticipantHandler
import com.azure.android.communication.ui.calling.models.CallCompositeRemoteParticipantJoinedEvent
import com.azure.android.communication.ui.calling.models.CallCompositeRemoteParticipantLeftEvent
import com.azure.android.communication.ui.calling.models.ParticipantInfoModel
import com.azure.android.communication.ui.calling.models.StreamType
import com.azure.android.communication.ui.calling.models.VideoStreamModel
import com.azure.android.communication.ui.calling.redux.AppStore
import com.azure.android.communication.ui.calling.redux.state.AppReduxState
import com.azure.android.communication.ui.calling.redux.state.ReduxState
import com.azure.android.communication.ui.calling.redux.state.RemoteParticipantsState
import com.azure.android.communication.ui.calling.service.sdk.CallingSDK
import com.azure.android.communication.ui.calling.service.sdk.CommunicationIdentifier
import com.azure.android.communication.ui.calling.service.sdk.RemoteParticipant
import com.azure.android.communication.ui.calling.service.sdk.into
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.argThat
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify

@RunWith(MockitoJUnitRunner::class)
internal class RemoteParticipantHandlerUnitTests : ACSBaseTestCoroutine() {

    @Test
    fun remoteParticipantHandler_start_onStateChangeWithNoRemoteParticipant_then_eventIsNotFiredToContoso() {
        runScopedTest {
            // arrange
            val storeStateFlow = MutableStateFlow<ReduxState>(
                AppReduxState(
                    "",
                    false,
                    false,
                )
            )
            val mockAppStore = mock<AppStore<ReduxState>> {
                on { getStateFlow() } doReturn storeStateFlow
            }
            val mockParticipantJoinedHandler =
                mock<CallCompositeEventHandler<CallCompositeRemoteParticipantJoinedEvent>>()

            val mockRemoteParticipantsCollection = mock<CallingSDK>()

            val configuration = CallCompositeConfiguration()
            configuration.callCompositeEventsHandler.addOnRemoteParticipantJoinedEventHandler(
                mockParticipantJoinedHandler
            )
            val handler = RemoteParticipantHandler(
                configuration,
                mockAppStore,
                mockRemoteParticipantsCollection
            )

            // act
            val job = launch {
                handler.start()
            }
            testScheduler.runCurrent()

            // assert
            verify(mockParticipantJoinedHandler, times(0)).handle(any())

            job.cancel()
        }
    }

    @Test
    fun remoteParticipantHandler_start_onStateChangeWithOneRemoteParticipant_then_eventIsFiredToOnce() {
        runScopedTest {
            // arrange
            val reduxState = AppReduxState("", false, false)
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
                                participantStatus = null,
                            )
                        )
                    ),
                    123,
                    listOf(),
                    0,
                    lobbyErrorCode = null,
                    totalParticipantCount = 1
                )
            val storeStateFlow = MutableStateFlow<ReduxState>(reduxState)
            val mockAppStore = mock<AppStore<ReduxState>> {
                on { getStateFlow() } doReturn storeStateFlow
            }
            val mockParticipantJoinedHandler =
                mock<CallCompositeEventHandler<CallCompositeRemoteParticipantJoinedEvent>>()

            val communicationIdentifier = CommunicationIdentifier.CommunicationUserIdentifier("test")

            val mockRemoteParticipant = mock<RemoteParticipant> {
                on { identifier } doReturn communicationIdentifier
            }
            val mockRemoteParticipantsCollection = mock<CallingSDK> {
                on { getRemoteParticipantsMap() } doReturn mapOf(
                    Pair(
                        "test",
                        mockRemoteParticipant
                    )
                )
            }

            val configuration = CallCompositeConfiguration()
            configuration.callCompositeEventsHandler.addOnRemoteParticipantJoinedEventHandler(
                mockParticipantJoinedHandler
            )
            val handler = RemoteParticipantHandler(
                configuration,
                mockAppStore,
                mockRemoteParticipantsCollection
            )

            // act
            val job = launch {
                handler.start()
            }
            testScheduler.runCurrent()

            // assert
            verify(mockParticipantJoinedHandler, times(1)).handle(
                argThat { event ->
                    event.identifiers.size == 1 && event.identifiers.toList()[0] == communicationIdentifier.into()
                }
            )

            job.cancel()
        }
    }

    @Test
    fun remoteParticipantHandler_start_add_remove_handler_then_eventIsNotFired() {
        runScopedTest {
            // arrange
            val reduxState = AppReduxState("", false, false)
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
                                participantStatus = null,
                            )
                        )
                    ),
                    123,
                    listOf(),
                    0,
                    lobbyErrorCode = null,
                    totalParticipantCount = 1
                )
            val storeStateFlow = MutableStateFlow<ReduxState>(reduxState)
            val mockAppStore = mock<AppStore<ReduxState>> {
                on { getStateFlow() } doReturn storeStateFlow
            }
            val mockParticipantJoinedHandler =
                mock<CallCompositeEventHandler<CallCompositeRemoteParticipantJoinedEvent>>()

            val mockRemoteParticipantsCollection = mock<CallingSDK> { }

            val configuration = CallCompositeConfiguration()
            configuration.callCompositeEventsHandler.addOnRemoteParticipantJoinedEventHandler(
                mockParticipantJoinedHandler
            )

            configuration.callCompositeEventsHandler.removeOnRemoteParticipantJoinedEventHandler(
                mockParticipantJoinedHandler
            )

            val handler = RemoteParticipantHandler(
                configuration,
                mockAppStore,
                mockRemoteParticipantsCollection
            )

            // act
            val job = launch {
                handler.start()
            }
            testScheduler.runCurrent()

            // assert
            verify(mockParticipantJoinedHandler, times(0)).handle(
                argThat { event ->
                    event is Any
                }
            )

            job.cancel()
        }
    }

    @Test
    fun remoteParticipantHandler_start_onStateChangeWithTwoRemoteParticipant_then_eventIsFiredToOnce() {
        runScopedTest {
            // arrange
            val reduxState = AppReduxState("", false, false)
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
                                participantStatus = null,
                            )
                        ),
                        Pair(
                            "test2",
                            ParticipantInfoModel(
                                displayName = "user two",
                                userIdentifier = "test2",
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
                                participantStatus = null,
                            )
                        )
                    ),
                    123,
                    listOf(),
                    0,
                    lobbyErrorCode = null,
                    totalParticipantCount = 1
                )
            val storeStateFlow = MutableStateFlow<ReduxState>(reduxState)
            val mockAppStore = mock<AppStore<ReduxState>> {
                on { getStateFlow() } doReturn storeStateFlow
            }
            val mockParticipantJoinedHandler =
                mock<CallCompositeEventHandler<CallCompositeRemoteParticipantJoinedEvent>>()

            val communicationIdentifierFirst = CommunicationIdentifier.CommunicationUserIdentifier("test")
            val communicationIdentifierSecond = CommunicationIdentifier.CommunicationUserIdentifier("test2")

            val mockRemoteParticipantFirst = mock<RemoteParticipant> {
                on { identifier } doReturn communicationIdentifierFirst
            }
            val mockRemoteParticipantSecond = mock<RemoteParticipant> {
                on { identifier } doReturn communicationIdentifierSecond
            }
            val mockRemoteParticipantsCollection = mock<CallingSDK> {
                on { getRemoteParticipantsMap() } doReturn mapOf(
                    Pair(
                        "test",
                        mockRemoteParticipantFirst
                    ),
                    Pair(
                        "test2",
                        mockRemoteParticipantSecond
                    )
                )
            }

            val configuration = CallCompositeConfiguration()
            configuration.callCompositeEventsHandler.addOnRemoteParticipantJoinedEventHandler(
                mockParticipantJoinedHandler
            )
            val handler = RemoteParticipantHandler(
                configuration,
                mockAppStore,
                mockRemoteParticipantsCollection
            )

            // act
            val job = launch {
                handler.start()
            }
            testScheduler.runCurrent()

            // assert
            verify(mockParticipantJoinedHandler, times(1)).handle(
                argThat { event ->
                    event.identifiers.size == 2 &&
                        event.identifiers.toList()[0] == communicationIdentifierFirst.into() &&
                        event.identifiers.toList()[1] == communicationIdentifierSecond.into()
                }
            )

            job.cancel()
        }
    }

    @Test
    fun remoteParticipantHandler_start_onStateChangeMultipleTimes_then_eventIsFiredForNewJoinedParticipants() {
        runScopedTest {
            // arrange
            val reduxState = AppReduxState("", false, false)
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
                                participantStatus = null,
                            )
                        ),
                        Pair(
                            "test2",
                            ParticipantInfoModel(
                                displayName = "user two",
                                userIdentifier = "test2",
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
                                participantStatus = null,
                            )
                        )
                    ),
                    123,
                    listOf(),
                    0,
                    lobbyErrorCode = null,
                    totalParticipantCount = 1
                )
            val storeStateFlow = MutableStateFlow<ReduxState>(reduxState)
            val mockAppStore = mock<AppStore<ReduxState>> {
                on { getStateFlow() } doReturn storeStateFlow
            }
            val mockParticipantJoinedHandler =
                mock<CallCompositeEventHandler<CallCompositeRemoteParticipantJoinedEvent>>()

            val communicationIdentifierFirst = CommunicationIdentifier.CommunicationUserIdentifier("test")
            val communicationIdentifierSecond = CommunicationIdentifier.CommunicationUserIdentifier("test2")
            val communicationIdentifierNew = CommunicationIdentifier.CommunicationUserIdentifier("testNew")

            val mockRemoteParticipantFirst = mock<RemoteParticipant> {
                on { identifier } doReturn communicationIdentifierFirst
            }
            val mockRemoteParticipantSecond = mock<RemoteParticipant> {
                on { identifier } doReturn communicationIdentifierSecond
            }
            val mockRemoteParticipantNew = mock<RemoteParticipant> {
                on { identifier } doReturn communicationIdentifierNew
            }
            val mockRemoteParticipantsCollection = mock<CallingSDK> {
                on { getRemoteParticipantsMap() } doReturn mapOf(
                    Pair(
                        "test",
                        mockRemoteParticipantFirst
                    ),
                    Pair(
                        "test2",
                        mockRemoteParticipantSecond
                    ),
                    Pair(
                        "testNew",
                        mockRemoteParticipantNew
                    )
                )
            }

            val configuration = CallCompositeConfiguration()
            configuration.callCompositeEventsHandler.addOnRemoteParticipantJoinedEventHandler(
                mockParticipantJoinedHandler
            )
            val handler = RemoteParticipantHandler(
                configuration,
                mockAppStore,
                mockRemoteParticipantsCollection
            )

            // act
            var job = launch {
                handler.start()
            }
            testScheduler.runCurrent()

            // assert
            verify(mockParticipantJoinedHandler, times(1)).handle(any())
            verify(mockParticipantJoinedHandler, times(1)).handle(
                argThat { event ->
                    event.identifiers.size == 2 &&
                        event.identifiers.toList()[0] == communicationIdentifierFirst.into() &&
                        event.identifiers.toList()[1] == communicationIdentifierSecond.into()
                }
            )

            job.cancel()

            // arrange
            val newReduxState = AppReduxState("", false, false)
            newReduxState.remoteParticipantState =
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
                                participantStatus = null,
                            )
                        ),
                        Pair(
                            "test2",
                            ParticipantInfoModel(
                                displayName = "user two",
                                userIdentifier = "test2",
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
                                participantStatus = null,
                            )
                        ),
                        Pair(
                            "testNew",
                            ParticipantInfoModel(
                                displayName = "user two",
                                userIdentifier = "testNew",
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
                                participantStatus = null,
                            )
                        )
                    ),
                    123456,
                    listOf(),
                    0,
                    lobbyErrorCode = null,
                    totalParticipantCount = 3
                )

            // act
            storeStateFlow.value = newReduxState
            job = launch {
                handler.start()
            }
            testScheduler.runCurrent()

            // assert
            verify(mockParticipantJoinedHandler, times(2)).handle(any())
            verify(mockParticipantJoinedHandler, times(1)).handle(
                argThat { event ->
                    event.identifiers.size == 1 &&
                        event.identifiers.toList()[0] == communicationIdentifierNew.into()
                }
            )

            job.cancel()
        }
    }

    @Test
    fun remoteParticipantHandler_start_onStateChangeMultipleTimes_then_eventIsNotFiredForRemovedParticipants() {
        runScopedTest {
            // arrange
            val reduxState = AppReduxState("", false, false)
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
                                participantStatus = null,
                            )
                        ),
                        Pair(
                            "test2",
                            ParticipantInfoModel(
                                displayName = "user two",
                                userIdentifier = "test2",
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
                                participantStatus = null,
                            )
                        )
                    ),
                    123,
                    listOf(),
                    0,
                    lobbyErrorCode = null,
                    totalParticipantCount = 2
                )
            val storeStateFlow = MutableStateFlow<ReduxState>(reduxState)
            val mockAppStore = mock<AppStore<ReduxState>> {
                on { getStateFlow() } doReturn storeStateFlow
            }
            val mockParticipantJoinedHandler =
                mock<CallCompositeEventHandler<CallCompositeRemoteParticipantJoinedEvent>>()

            val communicationIdentifierFirst = CommunicationIdentifier.CommunicationUserIdentifier("test")
            val communicationIdentifierSecond = CommunicationIdentifier.CommunicationUserIdentifier("test2")

            val mockRemoteParticipantFirst = mock<RemoteParticipant> {
                on { identifier } doReturn communicationIdentifierFirst
            }
            val mockRemoteParticipantSecond = mock<RemoteParticipant> {
                on { identifier } doReturn communicationIdentifierSecond
            }

            val mockRemoteParticipantsCollection = mock<CallingSDK> {
                on { getRemoteParticipantsMap() } doReturn mapOf(
                    Pair(
                        "test",
                        mockRemoteParticipantFirst
                    ),
                    Pair(
                        "test2",
                        mockRemoteParticipantSecond
                    )
                )
            }

            val mockRemoteParticipantsConfigurationHandler =
                mock<RemoteParticipantsConfigurationHandler>()
            val configuration = CallCompositeConfiguration()
            configuration.remoteParticipantsConfiguration.setHandler(
                mockRemoteParticipantsConfigurationHandler
            )
            configuration.callCompositeEventsHandler.addOnRemoteParticipantJoinedEventHandler(
                mockParticipantJoinedHandler
            )
            val handler = RemoteParticipantHandler(
                configuration,
                mockAppStore,
                mockRemoteParticipantsCollection
            )

            // act
            var job = launch {
                handler.start()
            }
            testScheduler.runCurrent()

            // assert
            verify(mockParticipantJoinedHandler, times(1)).handle(any())
            verify(mockParticipantJoinedHandler, times(1)).handle(
                argThat { event ->
                    event.identifiers.size == 2 &&
                        event.identifiers.toList()[0] == communicationIdentifierFirst.into() &&
                        event.identifiers.toList()[1] == communicationIdentifierSecond.into()
                }
            )

            job.cancel()

            // arrange
            val newReduxState = AppReduxState("", false, false)
            newReduxState.remoteParticipantState =
                RemoteParticipantsState(
                    mapOf(
                        Pair(
                            "test2",
                            ParticipantInfoModel(
                                displayName = "user two",
                                userIdentifier = "test2",
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
                                participantStatus = null,
                            )
                        ),
                    ),
                    123456,
                    listOf(),
                    0,
                    lobbyErrorCode = null,
                    totalParticipantCount = 1
                )

            // act
            storeStateFlow.value = newReduxState
            job = launch {
                handler.start()
            }
            testScheduler.runCurrent()

            // assert
            verify(mockParticipantJoinedHandler, times(1)).handle(any())
            verify(
                mockRemoteParticipantsConfigurationHandler,
                times(1)
            ).onRemoveParticipantViewData(
                argThat { identifier ->
                    identifier == "test"
                }
            )

            job.cancel()
        }
    }

    @Test
    fun remoteParticipantHandler_start_onStateChangeWithRemoteParticipantRemoved_then_eventIsFiredToOnce() {
        runScopedTest {
            // arrange
            val reduxState = AppReduxState("", false, false)
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
                                participantStatus = null,
                            )
                        ),
                        Pair(
                            "test2",
                            ParticipantInfoModel(
                                displayName = "user two",
                                userIdentifier = "test2",
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
                                participantStatus = null,
                            )
                        )
                    ),
                    123,
                    listOf(),
                    0,
                    lobbyErrorCode = null,
                    totalParticipantCount = 2
                )
            val storeStateFlow = MutableStateFlow<ReduxState>(reduxState)
            val mockAppStore = mock<AppStore<ReduxState>> {
                on { getStateFlow() } doReturn storeStateFlow
            }
            val mockParticipantJoinedHandler =
                mock<CallCompositeEventHandler<CallCompositeRemoteParticipantJoinedEvent>>()
            val mockParticipantLeftHandler =
                mock<CallCompositeEventHandler<CallCompositeRemoteParticipantLeftEvent>>()

            val communicationIdentifierFirst = CommunicationIdentifier.CommunicationUserIdentifier("test")
            val communicationIdentifierSecond = CommunicationIdentifier.CommunicationUserIdentifier("test2")

            val mockRemoteParticipantFirst = mock<RemoteParticipant> {
                on { identifier } doReturn communicationIdentifierFirst
            }
            val mockRemoteParticipantSecond = mock<RemoteParticipant> {
                on { identifier } doReturn communicationIdentifierSecond
            }

            val mockRemoteParticipantsCollection = mock<CallingSDK> {
                on { getRemoteParticipantsMap() } doReturn mapOf(
                    Pair(
                        "test",
                        mockRemoteParticipantFirst
                    ),
                    Pair(
                        "test2",
                        mockRemoteParticipantSecond
                    )
                )
            }

            val mockRemoteParticipantsConfigurationHandler =
                mock<RemoteParticipantsConfigurationHandler>()
            val configuration = CallCompositeConfiguration()
            configuration.remoteParticipantsConfiguration.setHandler(
                mockRemoteParticipantsConfigurationHandler
            )
            configuration.callCompositeEventsHandler.addOnRemoteParticipantJoinedEventHandler(
                mockParticipantJoinedHandler
            )
            configuration.callCompositeEventsHandler.addOnRemoteParticipantLeftEventHandler(
                mockParticipantLeftHandler
            )
            val handler = RemoteParticipantHandler(
                configuration,
                mockAppStore,
                mockRemoteParticipantsCollection
            )

            // act
            var job = launch {
                handler.start()
            }
            testScheduler.runCurrent()

            // assert
            verify(mockParticipantJoinedHandler, times(1)).handle(any())
            verify(mockParticipantJoinedHandler, times(1)).handle(
                argThat { event ->
                    event.identifiers.size == 2 &&
                        event.identifiers.toList()[0] == communicationIdentifierFirst.into() &&
                        event.identifiers.toList()[1] == communicationIdentifierSecond.into()
                }
            )

            job.cancel()

            // arrange
            val newReduxState = AppReduxState("", false, false)
            newReduxState.remoteParticipantState =
                RemoteParticipantsState(
                    mapOf(
                        Pair(
                            "test2",
                            ParticipantInfoModel(
                                displayName = "user two",
                                userIdentifier = "test2",
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
                                participantStatus = null,
                            )
                        ),
                    ),
                    123496,
                    listOf(),
                    0,
                    lobbyErrorCode = null,
                    totalParticipantCount = 1
                )

            // act
            storeStateFlow.value = newReduxState
            job = launch {
                handler.start()
            }
            testScheduler.runCurrent()

            // assert
            verify(mockParticipantLeftHandler, times(1)).handle(
                argThat { event ->
                    event.identifiers.size == 1 && event.identifiers.toList()[0].rawId == "test"
                }
            )

            job.cancel()
        }
    }
}
