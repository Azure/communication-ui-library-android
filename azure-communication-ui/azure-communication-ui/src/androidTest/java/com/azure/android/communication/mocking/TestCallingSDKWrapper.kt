// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.mocking

import com.azure.android.communication.calling.CallState
import com.azure.android.communication.calling.CameraFacing
import com.azure.android.communication.calling.VideoDeviceType
import com.azure.android.communication.ui.calling.models.ParticipantInfoModel
import com.azure.android.communication.ui.calling.models.ParticipantStatus
import com.azure.android.communication.ui.calling.models.StreamType
import com.azure.android.communication.ui.calling.models.VideoStreamModel
import com.azure.android.communication.ui.calling.redux.state.AudioState
import com.azure.android.communication.ui.calling.redux.state.CameraDeviceSelectionStatus
import com.azure.android.communication.ui.calling.redux.state.CameraState
import com.azure.android.communication.ui.calling.service.sdk.CallingSDK
import com.azure.android.communication.ui.calling.service.sdk.CallingStateWrapper
import com.azure.android.communication.ui.calling.service.sdk.LocalVideoStream
import com.azure.android.communication.ui.calling.service.sdk.RemoteParticipant
import com.azure.android.communication.ui.calling.service.sdk.VideoDeviceInfo
import com.azure.android.communication.ui.calling.utilities.CoroutineContextProvider
import java9.util.concurrent.CompletableFuture
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

internal class LocalVideoStreamTest : LocalVideoStream {
    override val native: Any
        get() = LocalVideoStreamTest()
    override val source: VideoDeviceInfo
        get() = VideoDeviceInfo(native, "3", "3", CameraFacing.BACK, VideoDeviceType.USB_CAMERA)

    override fun switchSource(deviceInfo: VideoDeviceInfo): CompletableFuture<Void> {
        val test = CompletableFuture<Void>()
        test.complete(null)
        return test
    }
}

internal class TestCallingSDKWrapper(coroutineContextProvider: CoroutineContextProvider) :
    CallingSDK {
    private val coroutineScope = CoroutineScope((coroutineContextProvider.Default))
    private var callingStateWrapperSharedFlow = MutableSharedFlow<CallingStateWrapper>()
    private var remoteParticipantsInfoModelSharedFlow =
        MutableSharedFlow<Map<String, ParticipantInfoModel>>()

    override fun setupCall() {
    }

    override fun dispose() {
    }

    override fun turnOnVideoAsync(): CompletableFuture<LocalVideoStream> {
        TODO("Not yet implemented")
    }

    override fun turnOffVideoAsync(): CompletableFuture<Void> {
        TODO("Not yet implemented")
    }

    override fun turnOnMicAsync(): CompletableFuture<Void> {
        TODO("Not yet implemented")
    }

    override fun turnOffMicAsync(): CompletableFuture<Void> {
        TODO("Not yet implemented")
    }

    override fun switchCameraAsync(): CompletableFuture<CameraDeviceSelectionStatus> {
        TODO("Not yet implemented")
    }

    override fun startCall(
        cameraState: CameraState,
        audioState: AudioState,
    ): CompletableFuture<Void> {
        val startCallCompletableFuture = CompletableFuture<Void>()
        startCallCompletableFuture.complete(null)
        return startCallCompletableFuture
    }

    override fun endCall(): CompletableFuture<Void> {
        val endCallCompletableFuture = CompletableFuture<Void>()
        coroutineScope.launch {
            callingStateWrapperSharedFlow.emit(CallingStateWrapper(CallState.DISCONNECTED, 0, 0))
        }
        endCallCompletableFuture.complete(null)
        return endCallCompletableFuture
    }

    override fun hold(): CompletableFuture<Void> {
        val holdFuture = CompletableFuture<Void>()
        holdFuture.complete(null)
        return holdFuture
    }

    override fun resume(): CompletableFuture<Void> {
        val resumeFuture = CompletableFuture<Void>()
        resumeFuture.complete(null)
        return resumeFuture
    }

    override fun getLocalVideoStream(): CompletableFuture<LocalVideoStream> {
        val localStreamFuture = CompletableFuture<LocalVideoStream>()
        localStreamFuture.complete(LocalVideoStreamTest())
        return localStreamFuture
    }

    override fun getRemoteParticipantsMap(): Map<String, RemoteParticipant> {
        TODO("Not yet implemented")
    }

    override fun getIsTranscribingSharedFlow(): SharedFlow<Boolean> {
        val flow = MutableSharedFlow<Boolean>()
        coroutineScope.launch {
            flow.emit(false)
        }
        return flow
    }

    override fun getIsRecordingSharedFlow(): SharedFlow<Boolean> {
        val flow = MutableSharedFlow<Boolean>()
        coroutineScope.launch {
            flow.emit(false)
        }
        return flow
    }

    override fun getIsMutedSharedFlow(): SharedFlow<Boolean> {
        val flow = MutableSharedFlow<Boolean>()
        coroutineScope.launch {
            flow.emit(false)
        }
        return flow
    }

    override fun getCallingStateWrapperSharedFlow(): SharedFlow<CallingStateWrapper> {
        coroutineScope.launch {
            delay(3000)
            callingStateWrapperSharedFlow.emit(CallingStateWrapper(CallState.CONNECTED, 0, 0))
        }
        return callingStateWrapperSharedFlow
    }

    override fun getRemoteParticipantInfoModelSharedFlow(): Flow<Map<String, ParticipantInfoModel>> {

        coroutineScope.launch {
            delay(3000)

            val remoteParticipantsMap: MutableMap<String, ParticipantInfoModel> = mutableMapOf()
            remoteParticipantsMap["user1"] = getParticipantInfoModel(
                "user one",
                "user1",
                isMuted = true,
                isSpeaking = true,
                cameraVideoStreamModel = null,
                modifiedTimestamp = 456,
                speakingTimestamp = 567
            )

            remoteParticipantsMap["user2"] = getParticipantInfoModel(
                "user two",
                "user1",
                isMuted = true,
                isSpeaking = true,
                cameraVideoStreamModel = VideoStreamModel("1", StreamType.VIDEO),
                modifiedTimestamp = 456,
                speakingTimestamp = 567
            )

            remoteParticipantsInfoModelSharedFlow.emit(remoteParticipantsMap)
        }

        return remoteParticipantsInfoModelSharedFlow
    }

    private fun getParticipantInfoModel(
        displayName: String,
        userIdentifier: String,
        isMuted: Boolean,
        isSpeaking: Boolean,
        screenShareVideoStreamModel: VideoStreamModel? = null,
        cameraVideoStreamModel: VideoStreamModel? = null,
        modifiedTimestamp: Number,
        speakingTimestamp: Number,
    ) = ParticipantInfoModel(
        displayName,
        userIdentifier,
        isMuted,
        isSpeaking,
        ParticipantStatus.CONNECTED,
        screenShareVideoStreamModel,
        cameraVideoStreamModel,
        modifiedTimestamp,
        speakingTimestamp
    )
}
