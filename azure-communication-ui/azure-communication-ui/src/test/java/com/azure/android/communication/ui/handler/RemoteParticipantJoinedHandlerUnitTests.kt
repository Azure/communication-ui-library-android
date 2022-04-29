// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.handler

import com.azure.android.communication.calling.RemoteParticipant
import com.azure.android.communication.common.CommunicationUserIdentifier
import com.azure.android.communication.ui.CallingEventHandler
import com.azure.android.communication.ui.configuration.CallCompositeConfiguration
import com.azure.android.communication.ui.configuration.events.CommunicationUIRemoteParticipantJoinedEvent
import com.azure.android.communication.ui.handlers.RemoteParticipantJoinedHandler
import com.azure.android.communication.ui.helper.MainCoroutineRule
import com.azure.android.communication.ui.model.ParticipantInfoModel
import com.azure.android.communication.ui.model.StreamType
import com.azure.android.communication.ui.model.VideoStreamModel
import com.azure.android.communication.ui.redux.AppStore
import com.azure.android.communication.ui.redux.state.AppReduxState
import com.azure.android.communication.ui.redux.state.ReduxState
import com.azure.android.communication.ui.redux.state.RemoteParticipantsState
import com.azure.android.communication.ui.service.calling.sdk.CallingSDKRemoteParticipantsCollection
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Rule
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
internal class RemoteParticipantJoinedHandlerUnitTests {
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Test
    fun remoteParticipantJoinedHandler_start_onStateChangeWithNoRemoteParticipant_then_eventIsNotFiredToContoso() {
        runTest {
            // arrange
            val storeStateFlow = MutableStateFlow<ReduxState>(AppReduxState(""))
            val mockAppStore = mock<AppStore<ReduxState>> {
                on { getStateFlow() } doReturn storeStateFlow
            }
            val mockParticipantJoinedHandler =
                mock<CallingEventHandler<CommunicationUIRemoteParticipantJoinedEvent>>()

            val mockRemoteParticipantsCollection = mock<CallingSDKRemoteParticipantsCollection>()

            val configuration = CallCompositeConfiguration()
            configuration.callCompositeEventsHandler.setOnRemoteParticipantJoinedHandler(
                mockParticipantJoinedHandler
            )
            val handler = RemoteParticipantJoinedHandler(
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
    fun remoteParticipantJoinedHandler_start_onStateChangeWithOneRemoteParticipant_then_eventIsFiredToOnce() {
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
            val storeStateFlow = MutableStateFlow<ReduxState>(reduxState)
            val mockAppStore = mock<AppStore<ReduxState>> {
                on { getStateFlow() } doReturn storeStateFlow
            }
            val mockParticipantJoinedHandler =
                mock<CallingEventHandler<CommunicationUIRemoteParticipantJoinedEvent>>()

            val communicationIdentifier = CommunicationUserIdentifier("test")

            val mockRemoteParticipant = mock<RemoteParticipant> {
                on { identifier } doReturn communicationIdentifier
            }
            val mockRemoteParticipantsCollection = mock<CallingSDKRemoteParticipantsCollection> {
                on { getRemoteParticipantsMap() } doReturn mapOf(
                    Pair(
                        "test",
                        mockRemoteParticipant
                    )
                )
            }

            val configuration = CallCompositeConfiguration()
            configuration.callCompositeEventsHandler.setOnRemoteParticipantJoinedHandler(
                mockParticipantJoinedHandler
            )
            val handler = RemoteParticipantJoinedHandler(
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
                    event.identifiers.size == 1 && event.identifiers[0] == communicationIdentifier
                }
            )

            job.cancel()
        }
    }

    @Test
    fun remoteParticipantJoinedHandler_start_onStateChangeWithTwoRemoteParticipant_then_eventIsFiredToOnce() {
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
                        ),
                        Pair(
                            "test2",
                            ParticipantInfoModel(
                                displayName = "user two",
                                userIdentifier = "test2",
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
            val storeStateFlow = MutableStateFlow<ReduxState>(reduxState)
            val mockAppStore = mock<AppStore<ReduxState>> {
                on { getStateFlow() } doReturn storeStateFlow
            }
            val mockParticipantJoinedHandler =
                mock<CallingEventHandler<CommunicationUIRemoteParticipantJoinedEvent>>()

            val communicationIdentifierFirst = CommunicationUserIdentifier("test")
            val communicationIdentifierSecond = CommunicationUserIdentifier("test2")

            val mockRemoteParticipantFirst = mock<RemoteParticipant> {
                on { identifier } doReturn communicationIdentifierFirst
            }
            val mockRemoteParticipantSecond = mock<RemoteParticipant> {
                on { identifier } doReturn communicationIdentifierSecond
            }
            val mockRemoteParticipantsCollection = mock<CallingSDKRemoteParticipantsCollection> {
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
            configuration.callCompositeEventsHandler.setOnRemoteParticipantJoinedHandler(
                mockParticipantJoinedHandler
            )
            val handler = RemoteParticipantJoinedHandler(
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
                    event.identifiers.size == 2
                        && event.identifiers[0] == communicationIdentifierFirst
                        && event.identifiers[1] == communicationIdentifierSecond
                }
            )

            job.cancel()
        }
    }
}
