// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.service.calling

import com.azure.android.communication.calling.CallState
import com.azure.android.communication.ui.calling.service.CallingService
import com.azure.android.communication.ui.calling.error.ErrorCode
import com.azure.android.communication.ui.calling.models.CallCompositeEventCode
import com.azure.android.communication.ui.helper.MockitoHelper.any
import com.azure.android.communication.ui.calling.redux.state.CallingStatus
import com.azure.android.communication.ui.calling.redux.state.CameraOperationalStatus
import com.azure.android.communication.ui.calling.redux.state.CameraState
import com.azure.android.communication.ui.calling.redux.state.CameraDeviceSelectionStatus
import com.azure.android.communication.ui.calling.redux.state.CameraTransmissionStatus
import com.azure.android.communication.ui.calling.redux.state.AudioState
import com.azure.android.communication.ui.calling.redux.state.BluetoothState
import com.azure.android.communication.ui.calling.redux.state.AudioDeviceSelectionStatus
import com.azure.android.communication.ui.calling.redux.state.AudioOperationalStatus
import com.azure.android.communication.ui.calling.service.sdk.CallingStateWrapper
import com.azure.android.communication.ui.ACSBaseTestCoroutine
import com.azure.android.communication.ui.helper.UnconfinedTestContextProvider
import com.azure.android.communication.ui.calling.models.CallInfoModel
import com.azure.android.communication.ui.calling.models.ParticipantInfoModel
import com.azure.android.communication.ui.calling.models.ParticipantStatus
import com.azure.android.communication.ui.calling.service.sdk.CallingSDK
import com.azure.android.communication.ui.calling.service.sdk.CallingStateWrapper.Companion.CALL_END_REASON_EVICTED
import com.azure.android.communication.ui.calling.service.sdk.CallingStateWrapper.Companion.CALL_END_REASON_SUB_CODE_DECLINED
import com.azure.android.communication.ui.calling.service.sdk.DominantSpeakersInfo
import com.azure.android.communication.ui.calling.service.sdk.LocalVideoStream

import java9.util.concurrent.CompletableFuture
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
internal class CallingServiceUnitTests : ACSBaseTestCoroutine() {

    @Mock
    private lateinit var mockCallingGateway: CallingSDK

    @Mock
    private lateinit var mockLocalVideoStream: LocalVideoStream
    private val contextProvider = UnconfinedTestContextProvider()

    private fun provideCallingService(
        callState: CallState = CallState.NONE,
    ): Pair<CallingService, MutableStateFlow<CallingStateWrapper>> {
        val remoteParticipantsInfoModelSharedFlow =
            MutableSharedFlow<Map<String, ParticipantInfoModel>>()

        val callingStateWrapperStateFlow =
            MutableStateFlow(CallingStateWrapper(callState, 0))
        val callIdFlow = MutableStateFlow<String?>(null)
        val isMutedSharedFlow = MutableSharedFlow<Boolean>()
        val isRecordingSharedFlow = MutableSharedFlow<Boolean>()
        val isTranscribingSharedFlow = MutableSharedFlow<Boolean>()
        val dominantSpeakersSharedFlow = MutableSharedFlow<DominantSpeakersInfo>()

        Mockito.`when`(mockCallingGateway.getRemoteParticipantInfoModelSharedFlow())
            .thenReturn(remoteParticipantsInfoModelSharedFlow)
        Mockito.`when`(mockCallingGateway.getCallingStateWrapperSharedFlow())
            .thenReturn(callingStateWrapperStateFlow)
        Mockito.`when`(mockCallingGateway.getCallIdStateFlow())
            .thenReturn(callIdFlow)
        Mockito.`when`(mockCallingGateway.getIsMutedSharedFlow())
            .thenReturn(isMutedSharedFlow)
        Mockito.`when`(mockCallingGateway.getIsRecordingSharedFlow())
            .thenReturn(isRecordingSharedFlow)
        Mockito.`when`(mockCallingGateway.getIsTranscribingSharedFlow())
            .thenReturn(isTranscribingSharedFlow)
        Mockito.doReturn(CompletableFuture<Void>()).`when`(mockCallingGateway).startCall(
            any(), any()
        )
        Mockito.`when`(mockCallingGateway.getDominantSpeakersSharedFlow())
            .thenReturn(dominantSpeakersSharedFlow)

        return Pair(
            CallingService(mockCallingGateway, contextProvider),
            callingStateWrapperStateFlow
        )
    }

    @ExperimentalCoroutinesApi
    @Test
    fun callingService_getCallInfoModelEventSharedFlow_when_disconnected_normally() =
        runScopedTest {

            // arrange
            val emitResultFromFlow = mutableListOf<CallInfoModel>()
            val (callingService, callingStateWrapperStateFlow) = provideCallingService()
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
            callingStateWrapperStateFlow.value = CallingStateWrapper(
                CallState.DISCONNECTED,
                0,
                0
            )

            // assert
            Assert.assertEquals(CallingStatus.NONE, emitResultFromFlow[0].callingStatus)
            Assert.assertEquals(CallingStatus.CONNECTED, emitResultFromFlow[1].callingStatus)
            Assert.assertEquals(CallingStatus.DISCONNECTED, emitResultFromFlow[2].callingStatus)
            Assert.assertNull(emitResultFromFlow[2].callStateError)

            job.cancel()
        }

    @ExperimentalCoroutinesApi
    @Test
    fun callingService_getCallInfoModelEventSharedFlow_when_evicted() =
        runScopedTest {

            // arrange
            val (callingService, callingStateWrapperStateFlow) = provideCallingService()
            val emitResultFromFlow = mutableListOf<CallInfoModel>()
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
            callingStateWrapperStateFlow.value = CallingStateWrapper(
                CallState.DISCONNECTED,
                0,
                CALL_END_REASON_EVICTED
            )

            // assert
            Assert.assertEquals(CallingStatus.NONE, emitResultFromFlow[0].callingStatus)
            Assert.assertEquals(CallingStatus.CONNECTED, emitResultFromFlow[1].callingStatus)
            Assert.assertEquals(CallingStatus.DISCONNECTED, emitResultFromFlow[2].callingStatus)
            Assert.assertEquals(
                CallCompositeEventCode.CALL_EVICTED,
                emitResultFromFlow[2].callStateError!!.callCompositeEventCode
            )

            job.cancel()
        }

    @ExperimentalCoroutinesApi
    @Test
    fun callingService_getCallInfoModelEventSharedFlow_when_declined() =
        runScopedTest {

            // arrange
            val (callingService, callingStateWrapperStateFlow) = provideCallingService()
            val emitResultFromFlow = mutableListOf<CallInfoModel>()
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
            callingStateWrapperStateFlow.value = CallingStateWrapper(
                CallState.DISCONNECTED,
                0,
                CALL_END_REASON_SUB_CODE_DECLINED
            )

            // assert
            Assert.assertEquals(CallingStatus.NONE, emitResultFromFlow[0].callingStatus)
            Assert.assertEquals(CallingStatus.CONNECTED, emitResultFromFlow[1].callingStatus)
            Assert.assertEquals(CallingStatus.DISCONNECTED, emitResultFromFlow[2].callingStatus)
            Assert.assertEquals(
                CallCompositeEventCode.CALL_DECLINED,
                emitResultFromFlow[2].callStateError!!.callCompositeEventCode
            )

            job.cancel()
        }

    @ExperimentalCoroutinesApi
    @Test
    fun callingService_getCallStateStateFlow_when_invokedByCallingGateway_returnCallingState() =
        runScopedTest {

            // arrange
            val (callingService, callingStateWrapperStateFlow) = provideCallingService()
            val emitResultFromFlow = mutableListOf<CallInfoModel>()
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
        runScopedTest {

            // arrange
            val remoteParticipantsInfoModelSharedFlow =
                MutableSharedFlow<Map<String, ParticipantInfoModel>>()

            val remoteParticipantsInfoModelMap = mutableMapOf<String, ParticipantInfoModel>()

            val callingStateWrapperStateFlow =
                MutableStateFlow(CallingStateWrapper(CallState.NONE, 0))
            val callIdFlow = MutableStateFlow<String?>(null)
            val isMutedSharedFlow = MutableSharedFlow<Boolean>()
            val isRecordingSharedFlow = MutableSharedFlow<Boolean>()
            val isTranscribingSharedFlow = MutableSharedFlow<Boolean>()
            val dominantSpeakersSharedFlow = MutableSharedFlow<DominantSpeakersInfo>()

            Mockito.`when`(mockCallingGateway.getRemoteParticipantInfoModelSharedFlow())
                .thenReturn(remoteParticipantsInfoModelSharedFlow)
            Mockito.`when`(mockCallingGateway.getRemoteParticipantInfoModelSharedFlow())
                .thenReturn(remoteParticipantsInfoModelSharedFlow)
            Mockito.`when`(mockCallingGateway.getCallIdStateFlow())
                .thenReturn(callIdFlow)
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
            Mockito.`when`(mockCallingGateway.getDominantSpeakersSharedFlow())
                .thenReturn(dominantSpeakersSharedFlow)

            remoteParticipantsInfoModelMap["id1"] = ParticipantInfoModel(
                "user1", "id1",
                isMuted = false,
                isSpeaking = false,
                screenShareVideoStreamModel = null,
                cameraVideoStreamModel = null,
                modifiedTimestamp = 0,
                participantStatus = ParticipantStatus.HOLD,
            )

            remoteParticipantsInfoModelMap["id3"] = ParticipantInfoModel(
                "abc", "id3",
                isMuted = false,
                isSpeaking = false,
                screenShareVideoStreamModel = null,
                cameraVideoStreamModel = null,
                modifiedTimestamp = 0,
                participantStatus = ParticipantStatus.HOLD,
            )

            remoteParticipantsInfoModelMap["0"] = ParticipantInfoModel(
                "xyz", "0",
                isMuted = false,
                isSpeaking = false,
                screenShareVideoStreamModel = null,
                cameraVideoStreamModel = null,
                modifiedTimestamp = 0,
                participantStatus = ParticipantStatus.HOLD,
            )

            remoteParticipantsInfoModelMap["id2"] = ParticipantInfoModel(
                "user2", "id2",
                isMuted = false,
                isSpeaking = false,
                screenShareVideoStreamModel = null,
                cameraVideoStreamModel = null,
                modifiedTimestamp = 0,
                participantStatus = ParticipantStatus.HOLD,
            )

            remoteParticipantsInfoModelMap["id9"] = ParticipantInfoModel(
                "user9", "id9",
                isMuted = false,
                isSpeaking = false,
                screenShareVideoStreamModel = null,
                cameraVideoStreamModel = null,
                modifiedTimestamp = 0,
                participantStatus = ParticipantStatus.HOLD,
            )

            remoteParticipantsInfoModelMap["10"] = ParticipantInfoModel(
                "100", "10",
                isMuted = false,
                isSpeaking = false,
                screenShareVideoStreamModel = null,
                cameraVideoStreamModel = null,
                modifiedTimestamp = 0,
                participantStatus = ParticipantStatus.HOLD,
            )

            val emitResultFromFlow = mutableListOf<Map<String, ParticipantInfoModel>>()

            val callingService = CallingService(mockCallingGateway, UnconfinedTestContextProvider())

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
        val callingService = CallingService(mockCallingGateway, UnconfinedTestContextProvider())
        val mockVideoDevice =
            mock(com.azure.android.communication.ui.calling.service.sdk.VideoDeviceInfo::class.java)

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
        val callingService = CallingService(mockCallingGateway, UnconfinedTestContextProvider())

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
        runScopedTest {

            // arrange
            val emitResultFromFlow = mutableListOf<CallInfoModel>()
            val (callingService, callingStateWrapperStateFlow) = provideCallingService()

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
                ErrorCode.TOKEN_EXPIRED,
                emitResultFromFlow[1].callStateError!!.errorCode
            )

            job.cancel()
        }

    @ExperimentalCoroutinesApi
    @Test
    fun callingService_getCallStateErrorFlow_when_nonErrorErrorCode_doesNotRaiseError() =
        runScopedTest {

            // arrange
            val emitResultFromFlow = mutableListOf<CallInfoModel>()
            val (callingService, callingStateWrapperStateFlow) = provideCallingService()

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
        runScopedTest {

            // arrange
            val emitResultFromFlow = mutableListOf<CallInfoModel>()
            val (callingService, callingStateWrapperStateFlow) = provideCallingService(CallState.CONNECTED)

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
                ErrorCode.CALL_END_FAILED,
                emitResultFromFlow[1].callStateError!!.errorCode
            )

            job.cancel()
        }

    @ExperimentalCoroutinesApi
    @Test
    fun callingService_getCallStateErrorFlow_when_stateNotConnectedAndInvokedByCallingGatewayWithAnyErrorCodeNonZero_returnErrorTypeCallJoin() =
        runScopedTest {

            // arrange
            val emitResultFromFlow = mutableListOf<CallInfoModel>()
            val (callingService, callingStateWrapperStateFlow) = provideCallingService()

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
                ErrorCode.CALL_JOIN_FAILED,
                emitResultFromFlow[1].callStateError!!.errorCode
            )

            job.cancel()
        }
}
