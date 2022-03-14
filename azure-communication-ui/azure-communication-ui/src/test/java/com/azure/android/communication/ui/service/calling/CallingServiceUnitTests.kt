// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.service.calling

import com.azure.android.communication.calling.CallState
import com.azure.android.communication.calling.LocalVideoStream
import com.azure.android.communication.calling.VideoDeviceInfo
import com.azure.android.communication.ui.configuration.events.CallCompositeErrorCode
import com.azure.android.communication.ui.helper.MainCoroutineRule
import com.azure.android.communication.ui.helper.MockitoHelper.any
import com.azure.android.communication.ui.helper.TestContextProvider
import com.azure.android.communication.ui.model.CallInfoModel
import com.azure.android.communication.ui.model.ParticipantInfoModel
import com.azure.android.communication.ui.redux.state.CallingStatus
import com.azure.android.communication.ui.redux.state.CameraOperationalStatus
import com.azure.android.communication.ui.redux.state.CameraState
import com.azure.android.communication.ui.redux.state.CameraDeviceSelectionStatus
import com.azure.android.communication.ui.redux.state.CameraTransmissionStatus
import com.azure.android.communication.ui.redux.state.AudioState
import com.azure.android.communication.ui.redux.state.BluetoothState
import com.azure.android.communication.ui.redux.state.AudioDeviceSelectionStatus
import com.azure.android.communication.ui.redux.state.AudioOperationalStatus
import com.azure.android.communication.ui.service.calling.sdk.CallingSDKWrapper
import com.azure.android.communication.ui.service.calling.sdk.CallingStateWrapper
import java9.util.concurrent.CompletableFuture
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
internal class CallingServiceUnitTests {
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Mock
    private lateinit var mockCallingGateway: CallingSDKWrapper

    @Mock
    private lateinit var mockLocalVideoStream: LocalVideoStream

    @ExperimentalCoroutinesApi
    @Test
    fun callingService_getCallStateStateFlow_when_invokedByCallingGateway_returnCallingState() =
        mainCoroutineRule.testDispatcher.runBlockingTest {

            // arrange
            val remoteParticipantsInfoModelSharedFlow =
                MutableSharedFlow<Map<String, ParticipantInfoModel>>()

            val callingStateWrapperStateFlow =
                MutableStateFlow(CallingStateWrapper(CallState.NONE, 0))
            val isMutedSharedFlow = MutableSharedFlow<Boolean>()
            val isRecordingSharedFlow = MutableSharedFlow<Boolean>()
            val isTranscribingSharedFlow = MutableSharedFlow<Boolean>()

            Mockito.`when`(mockCallingGateway.getRemoteParticipantInfoModelSharedFlow())
                .thenReturn(remoteParticipantsInfoModelSharedFlow)
            Mockito.`when`(mockCallingGateway.getCallingStateWrapperSharedFlow())
                .thenReturn(callingStateWrapperStateFlow)
            Mockito.`when`(mockCallingGateway.getIsMutedSharedFlow())
                .thenReturn(isMutedSharedFlow)
            Mockito.`when`(mockCallingGateway.getIsRecordingSharedFlow())
                .thenReturn(isRecordingSharedFlow)
            Mockito.`when`(mockCallingGateway.getIsTranscribingSharedFlow())
                .thenReturn(isTranscribingSharedFlow)
            Mockito.doReturn(CompletableFuture<Void>()).`when`(mockCallingGateway).startCall(
                any(), any()
            )

            val emitResultFromFlow = mutableListOf<CallInfoModel>()

            val callingService = CallingService(mockCallingGateway, TestContextProvider())

            val job = launch {
                callingService.getCallInfoModelEventSharedFlow().toList(emitResultFromFlow)
            }

            // act
            callingService.startCall(
                CameraState(
                    CameraOperationalStatus.OFF,
                    CameraDeviceSelectionStatus.FRONT,
                    CameraTransmissionStatus.LOCAL,
                ),
                AudioState(
                    AudioOperationalStatus.OFF,
                    AudioDeviceSelectionStatus.RECEIVER_SELECTED,
                    BluetoothState(available = false, deviceName = "bluetooth")
                )
            )
            callingStateWrapperStateFlow.value = CallingStateWrapper(CallState.CONNECTED, 0)
            callingStateWrapperStateFlow.value = CallingStateWrapper(CallState.DISCONNECTED, 0)

            // assert
            Assert.assertEquals(
                CallingStatus.NONE,
                emitResultFromFlow[0].callingStatus
            )

            Assert.assertEquals(
                CallingStatus.CONNECTED,
                emitResultFromFlow[1].callingStatus
            )

            Assert.assertEquals(
                CallingStatus.DISCONNECTED,
                emitResultFromFlow[2].callingStatus
            )

            job.cancel()
        }

    @ExperimentalCoroutinesApi
    @Test
    fun callingService_getRemoteParticipantSharedFlow_when_invokedByCallingGateway_returnParticipantsInOrder() =
        mainCoroutineRule.testDispatcher.runBlockingTest {

            // arrange
            val remoteParticipantsInfoModelSharedFlow =
                MutableSharedFlow<Map<String, ParticipantInfoModel>>()

            val remoteParticipantsInfoModelMap = mutableMapOf<String, ParticipantInfoModel>()

            val callingStateWrapperStateFlow =
                MutableStateFlow(CallingStateWrapper(CallState.NONE, 0))
            val isMutedSharedFlow = MutableSharedFlow<Boolean>()
            val isRecordingSharedFlow = MutableSharedFlow<Boolean>()
            val isTranscribingSharedFlow = MutableSharedFlow<Boolean>()

            Mockito.`when`(mockCallingGateway.getRemoteParticipantInfoModelSharedFlow())
                .thenReturn(remoteParticipantsInfoModelSharedFlow)
            Mockito.`when`(mockCallingGateway.getRemoteParticipantInfoModelSharedFlow())
                .thenReturn(remoteParticipantsInfoModelSharedFlow)

            Mockito.`when`(mockCallingGateway.getCallingStateWrapperSharedFlow())
                .thenReturn(callingStateWrapperStateFlow)
            Mockito.`when`(mockCallingGateway.getIsMutedSharedFlow())
                .thenReturn(isMutedSharedFlow)
            Mockito.`when`(mockCallingGateway.getIsRecordingSharedFlow())
                .thenReturn(isRecordingSharedFlow)
            Mockito.`when`(mockCallingGateway.getIsTranscribingSharedFlow())
                .thenReturn(isTranscribingSharedFlow)

            Mockito.doReturn(CompletableFuture<Void>()).`when`(mockCallingGateway).startCall(
                any(), any()
            )

            remoteParticipantsInfoModelMap["id1"] = ParticipantInfoModel(
                "user1", "id1",
                isMuted = false,
                isSpeaking = false,
                screenShareVideoStreamModel = null,
                cameraVideoStreamModel = null,
                modifiedTimestamp = 0,
                speakingTimestamp = 0
            )

            remoteParticipantsInfoModelMap["id3"] = ParticipantInfoModel(
                "abc", "id3",
                isMuted = false,
                isSpeaking = false,
                screenShareVideoStreamModel = null,
                cameraVideoStreamModel = null,
                modifiedTimestamp = 0,
                speakingTimestamp = 0
            )

            remoteParticipantsInfoModelMap["0"] = ParticipantInfoModel(
                "xyz", "0",
                isMuted = false,
                isSpeaking = false,
                screenShareVideoStreamModel = null,
                cameraVideoStreamModel = null,
                modifiedTimestamp = 0,
                speakingTimestamp = 0
            )

            remoteParticipantsInfoModelMap["id2"] = ParticipantInfoModel(
                "user2", "id2",
                isMuted = false,
                isSpeaking = false,
                screenShareVideoStreamModel = null,
                cameraVideoStreamModel = null,
                modifiedTimestamp = 0,
                speakingTimestamp = 0
            )

            remoteParticipantsInfoModelMap["id9"] = ParticipantInfoModel(
                "user9", "id9",
                isMuted = false,
                isSpeaking = false,
                screenShareVideoStreamModel = null,
                cameraVideoStreamModel = null,
                modifiedTimestamp = 0,
                speakingTimestamp = 0
            )

            remoteParticipantsInfoModelMap["10"] = ParticipantInfoModel(
                "100", "10",
                isMuted = false,
                isSpeaking = false,
                screenShareVideoStreamModel = null,
                cameraVideoStreamModel = null,
                modifiedTimestamp = 0,
                speakingTimestamp = 0
            )

            val emitResultFromFlow = mutableListOf<Map<String, ParticipantInfoModel>>()

            val callingService = CallingService(mockCallingGateway, TestContextProvider())

            val job = launch {
                callingService.getParticipantsInfoModelSharedFlow().toList(emitResultFromFlow)
            }

            // act
            callingService.startCall(
                CameraState(
                    CameraOperationalStatus.OFF,
                    CameraDeviceSelectionStatus.FRONT,
                    CameraTransmissionStatus.LOCAL,
                ),
                AudioState(
                    AudioOperationalStatus.OFF,
                    AudioDeviceSelectionStatus.RECEIVER_SELECTED,
                    BluetoothState(available = false, deviceName = "bluetooth")
                )
            )
            remoteParticipantsInfoModelSharedFlow.emit(remoteParticipantsInfoModelMap)

            // assert
            Assert.assertEquals(
                remoteParticipantsInfoModelMap.size,
                emitResultFromFlow[0].size
            )

            Assert.assertEquals(
                remoteParticipantsInfoModelMap.keys.toList()[0],
                emitResultFromFlow[0].values.toList()[0].userIdentifier
            )

            Assert.assertEquals(
                remoteParticipantsInfoModelMap.values.toList()[0].userIdentifier,
                emitResultFromFlow[0].values.toList()[0].userIdentifier
            )

            Assert.assertEquals(
                remoteParticipantsInfoModelMap.values.toList()[1].userIdentifier,
                emitResultFromFlow[0].values.toList()[1].userIdentifier
            )

            Assert.assertEquals(
                remoteParticipantsInfoModelMap.values.toList()[2].userIdentifier,
                emitResultFromFlow[0].values.toList()[2].userIdentifier
            )

            Assert.assertEquals(
                remoteParticipantsInfoModelMap.values.toList()[3].userIdentifier,
                emitResultFromFlow[0].values.toList()[3].userIdentifier
            )

            Assert.assertEquals(
                remoteParticipantsInfoModelMap.values.toList()[4].userIdentifier,
                emitResultFromFlow[0].values.toList()[4].userIdentifier
            )

            Assert.assertEquals(
                remoteParticipantsInfoModelMap.values.toList()[5].userIdentifier,
                emitResultFromFlow[0].values.toList()[5].userIdentifier
            )

            job.cancel()
        }

    @Test
    fun callingService_turnCameraOn_when_invoked_then_returnStreamIdOnGatewayFutureComplete_ifStreamIsNotNull() {
        // arrange
        var videoStreamId: String? = null
        val cameraStateCompletableFuture: CompletableFuture<LocalVideoStream> = CompletableFuture()
        val callingService = CallingService(mockCallingGateway, TestContextProvider())
        val mockVideoDevice = mock(VideoDeviceInfo::class.java)

        Mockito.`when`(mockCallingGateway.turnOnVideoAsync())
            .thenReturn(cameraStateCompletableFuture)
        Mockito.`when`(mockLocalVideoStream.source).thenReturn(mockVideoDevice)
        Mockito.`when`(mockVideoDevice.id).thenReturn("123")

        // act
        val future = callingService.turnCameraOn()
        future.whenComplete { streamId, _ ->
            videoStreamId = streamId
        }
        cameraStateCompletableFuture.complete(mockLocalVideoStream)

        // assert
        Assert.assertEquals(
            "123",
            videoStreamId
        )
    }

    @Test
    fun callingService_turnCameraOn_when_invoked_then_returnEmptyStreamIdOnGatewayFutureComplete_ifStreamIsNull() {
        // arrange
        var videoStreamId: String? = null
        val cameraStateCompletableFuture: CompletableFuture<LocalVideoStream> = CompletableFuture()
        val callingService = CallingService(mockCallingGateway, TestContextProvider())

        Mockito.`when`(mockCallingGateway.turnOnVideoAsync())
            .thenReturn(cameraStateCompletableFuture)

        // act
        val future = callingService.turnCameraOn()
        future.whenComplete { streamId, _ ->
            videoStreamId = streamId
        }
        cameraStateCompletableFuture.complete(null)

        // assert
        Assert.assertEquals(
            null,
            videoStreamId
        )
    }

    @ExperimentalCoroutinesApi
    @Test
    fun callingService_getCallStateErrorFlow_when_invokedByCallingGatewayWithAnyErrorCodeTokenExpired_returnErrorTypeTokenExpired() =
        mainCoroutineRule.testDispatcher.runBlockingTest {

            // arrange
            val remoteParticipantsInfoModelSharedFlow =
                MutableSharedFlow<Map<String, ParticipantInfoModel>>()

            val callingStateWrapperStateFlow =
                MutableStateFlow(CallingStateWrapper(CallState.NONE, 0))
            val isMutedSharedFlow = MutableSharedFlow<Boolean>()
            val isRecordingSharedFlow = MutableSharedFlow<Boolean>()
            val isTranscribingSharedFlow = MutableSharedFlow<Boolean>()

            Mockito.`when`(mockCallingGateway.getRemoteParticipantInfoModelSharedFlow())
                .thenReturn(remoteParticipantsInfoModelSharedFlow)
            Mockito.`when`(mockCallingGateway.getCallingStateWrapperSharedFlow())
                .thenReturn(callingStateWrapperStateFlow)
            Mockito.`when`(mockCallingGateway.getIsMutedSharedFlow())
                .thenReturn(isMutedSharedFlow)
            Mockito.`when`(mockCallingGateway.getIsRecordingSharedFlow())
                .thenReturn(isRecordingSharedFlow)
            Mockito.`when`(mockCallingGateway.getIsTranscribingSharedFlow())
                .thenReturn(isTranscribingSharedFlow)
            Mockito.doReturn(CompletableFuture<Void>()).`when`(mockCallingGateway).startCall(
                any(), any()
            )

            val emitResultFromFlow = mutableListOf<CallInfoModel>()

            val callingService = CallingService(mockCallingGateway, TestContextProvider())

            val job = launch {
                callingService.getCallInfoModelEventSharedFlow().toList(emitResultFromFlow)
            }

            // act
            callingService.startCall(
                CameraState(
                    CameraOperationalStatus.OFF,
                    CameraDeviceSelectionStatus.FRONT,
                    CameraTransmissionStatus.LOCAL,
                ),
                AudioState(
                    AudioOperationalStatus.OFF,
                    AudioDeviceSelectionStatus.RECEIVER_SELECTED,
                    BluetoothState(available = false, deviceName = "bluetooth")
                )
            )

            callingStateWrapperStateFlow.emit(CallingStateWrapper(CallState.NONE, 401))

            // assert
            Assert.assertEquals(
                CallCompositeErrorCode.TOKEN_EXPIRED,
                emitResultFromFlow[1].callStateError!!.callCompositeErrorCode
            )

            job.cancel()
        }

    @ExperimentalCoroutinesApi
    @Test
    fun callingService_getCallStateErrorFlow_when_nonErrorErrorCode_doesNotRaiseError() =
        mainCoroutineRule.testDispatcher.runBlockingTest {

            // arrange
            val remoteParticipantsInfoModelSharedFlow =
                MutableSharedFlow<Map<String, ParticipantInfoModel>>()

            val callingStateWrapperStateFlow =
                MutableStateFlow(CallingStateWrapper(CallState.NONE, 0))
            val isMutedSharedFlow = MutableSharedFlow<Boolean>()
            val isRecordingSharedFlow = MutableSharedFlow<Boolean>()
            val isTranscribingSharedFlow = MutableSharedFlow<Boolean>()

            Mockito.`when`(mockCallingGateway.getRemoteParticipantInfoModelSharedFlow())
                .thenReturn(remoteParticipantsInfoModelSharedFlow)
            Mockito.`when`(mockCallingGateway.getCallingStateWrapperSharedFlow())
                .thenReturn(callingStateWrapperStateFlow)
            Mockito.`when`(mockCallingGateway.getIsMutedSharedFlow())
                .thenReturn(isMutedSharedFlow)
            Mockito.`when`(mockCallingGateway.getIsRecordingSharedFlow())
                .thenReturn(isRecordingSharedFlow)
            Mockito.`when`(mockCallingGateway.getIsTranscribingSharedFlow())
                .thenReturn(isTranscribingSharedFlow)
            Mockito.doReturn(CompletableFuture<Void>()).`when`(mockCallingGateway).startCall(
                any(), any()
            )

            val emitResultFromFlow = mutableListOf<CallInfoModel>()

            val callingService = CallingService(mockCallingGateway, TestContextProvider())

            val job = launch {
                callingService.getCallInfoModelEventSharedFlow().toList(emitResultFromFlow)
            }

            // act
            callingService.startCall(
                CameraState(
                    CameraOperationalStatus.OFF,
                    CameraDeviceSelectionStatus.FRONT,
                    CameraTransmissionStatus.LOCAL,
                ),
                AudioState(
                    AudioOperationalStatus.OFF,
                    AudioDeviceSelectionStatus.RECEIVER_SELECTED,
                    BluetoothState(available = false, deviceName = "bluetooth")
                )
            )

            callingStateWrapperStateFlow.emit(CallingStateWrapper(CallState.NONE, 487))
            callingStateWrapperStateFlow.emit(CallingStateWrapper(CallState.DISCONNECTED, 603))

            // assert
            Assert.assertNull(emitResultFromFlow[1].callStateError)
            Assert.assertNull(emitResultFromFlow[2].callStateError)

            job.cancel()
        }

    @ExperimentalCoroutinesApi
    @Test
    fun callingService_getCallStateErrorFlow_when_stateConnectedAndInvokedByCallingGatewayWithAnyErrorCodeNonZero_returnErrorTypeCallEnd() =
        mainCoroutineRule.testDispatcher.runBlockingTest {

            // arrange
            val remoteParticipantsInfoModelSharedFlow =
                MutableSharedFlow<Map<String, ParticipantInfoModel>>()

            val callingStateWrapperStateFlow =
                MutableStateFlow(CallingStateWrapper(CallState.CONNECTED, 0))
            val isMutedSharedFlow = MutableSharedFlow<Boolean>()
            val isRecordingSharedFlow = MutableSharedFlow<Boolean>()
            val isTranscribingSharedFlow = MutableSharedFlow<Boolean>()

            Mockito.`when`(mockCallingGateway.getRemoteParticipantInfoModelSharedFlow())
                .thenReturn(remoteParticipantsInfoModelSharedFlow)
            Mockito.`when`(mockCallingGateway.getCallingStateWrapperSharedFlow())
                .thenReturn(callingStateWrapperStateFlow)
            Mockito.`when`(mockCallingGateway.getIsMutedSharedFlow())
                .thenReturn(isMutedSharedFlow)
            Mockito.`when`(mockCallingGateway.getIsRecordingSharedFlow())
                .thenReturn(isRecordingSharedFlow)
            Mockito.`when`(mockCallingGateway.getIsTranscribingSharedFlow())
                .thenReturn(isTranscribingSharedFlow)
            Mockito.doReturn(CompletableFuture<Void>()).`when`(mockCallingGateway).startCall(
                any(), any()
            )

            val emitResultFromFlow = mutableListOf<CallInfoModel>()

            val callingService = CallingService(mockCallingGateway, TestContextProvider())

            val job = launch {
                callingService.getCallInfoModelEventSharedFlow().toList(emitResultFromFlow)
            }

            // act
            callingService.startCall(
                CameraState(
                    CameraOperationalStatus.OFF,
                    CameraDeviceSelectionStatus.FRONT,
                    CameraTransmissionStatus.LOCAL,
                ),
                AudioState(
                    AudioOperationalStatus.OFF,
                    AudioDeviceSelectionStatus.RECEIVER_SELECTED,
                    BluetoothState(available = false, deviceName = "bluetooth")
                )
            )
            callingStateWrapperStateFlow.value = CallingStateWrapper(CallState.DISCONNECTED, 1)

            // assert
            Assert.assertEquals(
                CallCompositeErrorCode.CALL_END,
                emitResultFromFlow[1].callStateError!!.callCompositeErrorCode
            )

            job.cancel()
        }

    @ExperimentalCoroutinesApi
    @Test
    fun callingService_getCallStateErrorFlow_when_stateNotConnectedAndInvokedByCallingGatewayWithAnyErrorCodeNonZero_returnErrorTypeCallJoin() =
        mainCoroutineRule.testDispatcher.runBlockingTest {

            // arrange
            val remoteParticipantsInfoModelSharedFlow =
                MutableSharedFlow<Map<String, ParticipantInfoModel>>()

            val callingStateWrapperStateFlow =
                MutableStateFlow(CallingStateWrapper(CallState.NONE, 0))
            val isMutedSharedFlow = MutableSharedFlow<Boolean>()
            val isRecordingSharedFlow = MutableSharedFlow<Boolean>()
            val isTranscribingSharedFlow = MutableSharedFlow<Boolean>()

            Mockito.`when`(mockCallingGateway.getRemoteParticipantInfoModelSharedFlow())
                .thenReturn(remoteParticipantsInfoModelSharedFlow)
            Mockito.`when`(mockCallingGateway.getCallingStateWrapperSharedFlow())
                .thenReturn(callingStateWrapperStateFlow)
            Mockito.`when`(mockCallingGateway.getIsMutedSharedFlow())
                .thenReturn(isMutedSharedFlow)
            Mockito.`when`(mockCallingGateway.getIsRecordingSharedFlow())
                .thenReturn(isRecordingSharedFlow)
            Mockito.`when`(mockCallingGateway.getIsTranscribingSharedFlow())
                .thenReturn(isTranscribingSharedFlow)
            Mockito.doReturn(CompletableFuture<Void>()).`when`(mockCallingGateway).startCall(
                any(), any()
            )

            val emitResultFromFlow = mutableListOf<CallInfoModel>()

            val callingService = CallingService(mockCallingGateway, TestContextProvider())

            val job = launch {
                callingService.getCallInfoModelEventSharedFlow().toList(emitResultFromFlow)
            }

            // act
            callingService.startCall(
                CameraState(
                    CameraOperationalStatus.OFF,
                    CameraDeviceSelectionStatus.FRONT,
                    CameraTransmissionStatus.LOCAL,
                ),
                AudioState(
                    AudioOperationalStatus.OFF,
                    AudioDeviceSelectionStatus.RECEIVER_SELECTED,
                    BluetoothState(available = false, deviceName = "bluetooth")
                )
            )
            callingStateWrapperStateFlow.emit(CallingStateWrapper(CallState.NONE, 1))

            // assert
            Assert.assertEquals(
                CallCompositeErrorCode.CALL_JOIN,
                emitResultFromFlow[1].callStateError!!.callCompositeErrorCode
            )

            job.cancel()
        }
}
