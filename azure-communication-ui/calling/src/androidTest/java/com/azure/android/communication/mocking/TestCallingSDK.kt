// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.mocking

import androidx.annotation.GuardedBy
import com.azure.android.communication.calling.CameraFacing
import com.azure.android.communication.calling.VideoDeviceType
import com.azure.android.communication.calling.ParticipantState
import com.azure.android.communication.calling.MediaStreamType
import com.azure.android.communication.calling.CallState
import com.azure.android.communication.calling.RemoteVideoStreamsUpdatedListener
import com.azure.android.communication.calling.PropertyChangedListener
import com.azure.android.communication.ui.calling.models.CallCompositeLobbyErrorCode
import com.azure.android.communication.ui.calling.models.CallCompositeInternalParticipantRole
import com.azure.android.communication.ui.calling.models.CallDiagnosticQuality
import com.azure.android.communication.ui.calling.models.MediaCallDiagnostic
import com.azure.android.communication.ui.calling.models.MediaCallDiagnosticModel
import com.azure.android.communication.ui.calling.models.NetworkCallDiagnostic
import com.azure.android.communication.ui.calling.models.NetworkCallDiagnosticModel
import com.azure.android.communication.ui.calling.models.NetworkQualityCallDiagnosticModel
import com.azure.android.communication.ui.calling.models.ParticipantInfoModel
import com.azure.android.communication.ui.calling.models.StreamType
import com.azure.android.communication.ui.calling.models.VideoStreamModel
import com.azure.android.communication.ui.calling.redux.state.AudioState
import com.azure.android.communication.ui.calling.redux.state.CameraDeviceSelectionStatus
import com.azure.android.communication.ui.calling.redux.state.CameraState
import com.azure.android.communication.ui.calling.service.sdk.CallingSDK
import com.azure.android.communication.ui.calling.service.sdk.CallingStateWrapper
import com.azure.android.communication.ui.calling.service.sdk.CommunicationIdentifier
import com.azure.android.communication.ui.calling.service.sdk.DominantSpeakersInfo
import com.azure.android.communication.ui.calling.service.sdk.into
import com.azure.android.communication.ui.calling.service.sdk.LocalVideoStream
import com.azure.android.communication.ui.calling.service.sdk.VideoDeviceInfo
import com.azure.android.communication.ui.calling.service.sdk.RemoteVideoStream
import com.azure.android.communication.ui.calling.service.sdk.RemoteParticipant
import com.azure.android.communication.ui.calling.utilities.CoroutineContextProvider
import java9.util.concurrent.CompletableFuture
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.File

internal interface LocalStreamEventObserver {
    fun onSwitchSource(deviceInfo: VideoDeviceInfo)
}

internal class CallEvents {
    val localStreamObservers: MutableMap<LocalVideoStream, LocalStreamEventObserver> = mutableMapOf()

    fun notifyLocalStreamObservers(f: (LocalStreamEventObserver.() -> Unit)) {
        localStreamObservers.forEach { it.value.f() }
    }
}

internal class LocalVideoStreamTest(
    private val callEvents: CallEvents,
    private val cameraFacing: CameraFacing,
    private val coroutineScope: CoroutineScope
) : LocalVideoStream {
    override val native: Any = 1
    override val source: VideoDeviceInfo
        get() = VideoDeviceInfo(native, "1", "test", cameraFacing, VideoDeviceType.USB_CAMERA)

    override fun switchSource(deviceInfo: VideoDeviceInfo): CompletableFuture<Void> {
        callEvents.notifyLocalStreamObservers { onSwitchSource(deviceInfo) }
        return completedNullFuture()
    }
}

internal class TestCallingSDK(private val callEvents: CallEvents, coroutineContextProvider: CoroutineContextProvider = CoroutineContextProvider()) :
    CallingSDK {
    private val coroutineScope = CoroutineScope(coroutineContextProvider.Default)
    private var callingStateWrapperSharedFlow = MutableSharedFlow<CallingStateWrapper>()
    private var callIdStateFlow = MutableStateFlow<String?>(null)
    private var remoteParticipantsInfoModelSharedFlow =
        MutableSharedFlow<Map<String, ParticipantInfoModel>>()
    private var isMutedSharedFlow = MutableSharedFlow<Boolean>()
    private var isRecordingSharedFlow = MutableSharedFlow<Boolean>()
    private var dominantSpeakersSharedFlow = MutableSharedFlow<DominantSpeakersInfo>()
    private var isTranscribingSharedFlow = MutableSharedFlow<Boolean>()
    private var getCameraCountStateFlow = MutableStateFlow(2)
    private val participantRoleSharedFlow = MutableSharedFlow<CallCompositeInternalParticipantRole?>()
    private var networkQualityCallDiagnosticSharedFlow = MutableSharedFlow<NetworkQualityCallDiagnosticModel>()
    private var networkCallDiagnosticSharedFlow = MutableSharedFlow<NetworkCallDiagnosticModel>()
    private var mediaCallDiagnosticSharedFlow = MutableSharedFlow<MediaCallDiagnosticModel>()

    @GuardedBy("this")
    private val remoteParticipantsMap: MutableMap<String, RemoteParticipant> = mutableMapOf()
    private val callStarted = AtomicBoolean(false)

    private var localCameraFacing = CameraFacing.FRONT
    private val localVideoStream = LocalVideoStreamTest(callEvents, localCameraFacing, coroutineScope)
    private var lobbyResultCompletableFuture: CompletableFuture<CallCompositeLobbyErrorCode?> = CompletableFuture()

    suspend fun addRemoteParticipant(
        id: CommunicationIdentifier,
        displayName: String,
        state: ParticipantState = ParticipantState.CONNECTED,
        isMuted: Boolean = true,
        isSpeaking: Boolean = false,
        videoStreams: List<MediaStreamType>? = listOf()
    ) {
        val rpi = RemoteParticipantImpl(
            identifier = id,
            displayName = displayName,
            isMuted = isMuted,
            isSpeaking = isSpeaking,
            videoStreams = videoStreams?.mapIndexed { index, type ->
                RemoteVideoStreamImpl(
                    native = 1,
                    id = index,
                    mediaStreamType = type
                )
            } ?: listOf(),
            state = state
        )
        synchronized(this) {
            remoteParticipantsMap[id.id] = rpi
        }

        emitRemoteParticipantFlow()
    }

    suspend fun changeParticipantState(id: String, state: ParticipantState) {
        synchronized(this) {
            if (!remoteParticipantsMap.containsKey(id)) {
                return
            }
            val rpi = remoteParticipantsMap[id]!!
            remoteParticipantsMap[id] = RemoteParticipantImpl(
                identifier = rpi.identifier,
                displayName = rpi.displayName,
                isMuted = rpi.isMuted,
                isSpeaking = rpi.isSpeaking,
                videoStreams = rpi.videoStreams,
                state = state
            )
        }
        emitRemoteParticipantFlow()
    }

    fun setLobbyResultCompletableFuture(lobbyResultCompletableFuture: CompletableFuture<CallCompositeLobbyErrorCode?>) {
        this.lobbyResultCompletableFuture = lobbyResultCompletableFuture
    }

    fun setParticipantRoleSharedFlow(participantRole: CallCompositeInternalParticipantRole?) {
        coroutineScope.launch {
            participantRoleSharedFlow.emit(participantRole)
        }
    }

    suspend fun removeParticipant(id: String) {
        synchronized(this) {
            remoteParticipantsMap.remove(id)
        }
        emitRemoteParticipantFlow()
    }

    suspend fun changeParticipant(
        id: String,
        isMuted: Boolean? = null,
        isSpeaking: Boolean? = null,
        state: ParticipantState? = null
    ) {
        synchronized(this) {
            if (!remoteParticipantsMap.containsKey(id)) {
                return
            }
            val rpi = remoteParticipantsMap[id]!!
            remoteParticipantsMap[id] = RemoteParticipantImpl(
                identifier = rpi.identifier,
                displayName = rpi.displayName,
                isMuted = isMuted ?: rpi.isMuted,
                isSpeaking = isSpeaking ?: rpi.isSpeaking,
                videoStreams = rpi.videoStreams,
                state = state ?: rpi.state
            )
        }
        emitRemoteParticipantFlow()
    }

    suspend fun setLowNetworkRecieveQuality(lowNetworkReceiveQuality: Boolean) {
        val model = NetworkQualityCallDiagnosticModel(NetworkCallDiagnostic.NETWORK_RECEIVE_QUALITY, if (lowNetworkReceiveQuality) CallDiagnosticQuality.BAD else CallDiagnosticQuality.GOOD)
        networkQualityCallDiagnosticSharedFlow.emit(model)
    }

    suspend fun setLowNetworkSendQuality(lowNetworkSendQuality: Boolean) {
        val model = NetworkQualityCallDiagnosticModel(NetworkCallDiagnostic.NETWORK_SEND_QUALITY, if (lowNetworkSendQuality) CallDiagnosticQuality.BAD else CallDiagnosticQuality.GOOD)
        networkQualityCallDiagnosticSharedFlow.emit(model)
    }

    suspend fun setLowNetworkReconnectionQuality(lowNetworkReconnectionQuality: Boolean) {
        val model = NetworkQualityCallDiagnosticModel(NetworkCallDiagnostic.NETWORK_RECONNECTION_QUALITY, if (lowNetworkReconnectionQuality) CallDiagnosticQuality.BAD else CallDiagnosticQuality.GOOD)
        networkQualityCallDiagnosticSharedFlow.emit(model)
    }

    suspend fun setNetworkUnavailable(networkUnavailable: Boolean) {
        val model = NetworkCallDiagnosticModel(NetworkCallDiagnostic.NETWORK_UNAVAILABLE, networkUnavailable)
        networkCallDiagnosticSharedFlow.emit(model)
    }

    suspend fun setNetworkRelaysUnreachable(networkRelaysUnreachable: Boolean) {
        val model = NetworkCallDiagnosticModel(NetworkCallDiagnostic.NETWORK_RELAYS_UNREACHABLE, networkRelaysUnreachable)
        networkCallDiagnosticSharedFlow.emit(model)
    }

    suspend fun setCameraStartFailed(cameraStartFailed: Boolean) {
        val model = MediaCallDiagnosticModel(MediaCallDiagnostic.CAMERA_START_FAILED, cameraStartFailed)
        mediaCallDiagnosticSharedFlow.emit(model)
    }

    suspend fun setCameraStartTimedOut(cameraStartTimedOut: Boolean) {
        val model = MediaCallDiagnosticModel(MediaCallDiagnostic.CAMERA_START_TIMED_OUT, cameraStartTimedOut)
        mediaCallDiagnosticSharedFlow.emit(model)
    }

    suspend fun setSpeakingWhileMuted(speakingWhileMuted: Boolean) {
        val model = MediaCallDiagnosticModel(MediaCallDiagnostic.SPEAKING_WHILE_MICROPHONE_IS_MUTED, speakingWhileMuted)
        mediaCallDiagnosticSharedFlow.emit(model)
    }

    suspend fun setNoSpeakerDevicesAvailable(speakerDevicesAvailable: Boolean) {
        val model = MediaCallDiagnosticModel(MediaCallDiagnostic.NO_SPEAKER_DEVICES_AVAILABLE, speakerDevicesAvailable)
        mediaCallDiagnosticSharedFlow.emit(model)
    }

    suspend fun setNoMicrophoneDevicesAvailable(microphoneDevicesAvailable: Boolean) {
        val model = MediaCallDiagnosticModel(MediaCallDiagnostic.NO_MICROPHONE_DEVICES_AVAILABLE, microphoneDevicesAvailable)
        mediaCallDiagnosticSharedFlow.emit(model)
    }

    suspend fun setMicrophoneNotFunctioning(microphoneNotFunctioning: Boolean) {
        val model = MediaCallDiagnosticModel(MediaCallDiagnostic.MICROPHONE_NOT_FUNCTIONING, microphoneNotFunctioning)
        mediaCallDiagnosticSharedFlow.emit(model)
    }

    suspend fun setSpeakerNotFunctioning(speakerNotFunctioning: Boolean) {
        val model = MediaCallDiagnosticModel(MediaCallDiagnostic.SPEAKER_NOT_FUNCTIONING, speakerNotFunctioning)
        mediaCallDiagnosticSharedFlow.emit(model)
    }

    suspend fun setSpeakerVolumeZero(speakerVolumeZero: Boolean) {
        val model = MediaCallDiagnosticModel(MediaCallDiagnostic.SPEAKER_VOLUME_ZERO, speakerVolumeZero)
        mediaCallDiagnosticSharedFlow.emit(model)
    }

    override fun setupCall(): CompletableFuture<Void> {
        return completedNullFuture()
    }
    override fun dispose() {}

    override fun turnOnVideoAsync(): CompletableFuture<LocalVideoStream> {
        return completedFuture(LocalVideoStreamTest(callEvents, localCameraFacing, coroutineScope))
    }

    override fun turnOffVideoAsync(): CompletableFuture<Void> {
        return completedNullFuture()
    }

    override fun turnOnMicAsync(): CompletableFuture<Void> {
        return completedNullFuture(coroutineScope) {
            isMutedSharedFlow.emit(false)
        }
    }

    override fun turnOffMicAsync(): CompletableFuture<Void> {
        return completedNullFuture(coroutineScope) {
            isMutedSharedFlow.emit(true)
        }
    }

    override fun switchCameraAsync(): CompletableFuture<CameraDeviceSelectionStatus> {
        localCameraFacing = when (localCameraFacing) {
            CameraFacing.FRONT -> CameraFacing.BACK
            CameraFacing.BACK -> CameraFacing.FRONT
            else -> TODO("Camera modes that aren't Front or Back not yet implemented")
        }

        localVideoStream.switchSource(localVideoStream.source.copy(cameraFacing = localCameraFacing)).join()

        // Note that CameraDeviceSelectionStatus.SWITCHING is used by the reducer directly
        // as it's waiting for this future to complete.
        return completedFuture {
            when (localCameraFacing) {
                CameraFacing.FRONT -> CameraDeviceSelectionStatus.FRONT
                CameraFacing.BACK -> CameraDeviceSelectionStatus.BACK
                else -> TODO("Camera modes that aren't Front or Back not yet implemented")
            }
        }
    }

    override fun startCall(
        cameraState: CameraState,
        audioState: AudioState,
    ): CompletableFuture<Void> {
        val startCallCompletableFuture = CompletableFuture<Void>()
        coroutineScope.launch {
            startCallCompletableFuture.complete(null)
            callStarted.compareAndSet(false, true)
            callingStateWrapperSharedFlow.emit(CallingStateWrapper(CallState.CONNECTED, 0, 0))
            callIdStateFlow.emit("callid")
            emitRemoteParticipantFlow()
        }
        return startCallCompletableFuture
    }

    override fun endCall(): CompletableFuture<Void> {
        val endCallCompletableFuture = CompletableFuture<Void>()
        coroutineScope.launch {
            endCallCompletableFuture.complete(null)
            callStarted.compareAndSet(true, false)
            callingStateWrapperSharedFlow.emit(CallingStateWrapper(CallState.DISCONNECTED, 0, 0))
        }
        return endCallCompletableFuture
    }

    override fun hold(): CompletableFuture<Void> {
        return completedNullFuture()
    }

    override fun resume(): CompletableFuture<Void> {
        return completedNullFuture()
    }

    override fun getLocalVideoStream(): CompletableFuture<LocalVideoStream> {
        return completedFuture(LocalVideoStreamTest(callEvents, localCameraFacing, coroutineScope))
    }

    override fun getRemoteParticipantsMap(): Map<String, RemoteParticipant> {
        return if (callStarted.get()) {
            remoteParticipantsMap
        } else {
            mapOf()
        }
    }

    override fun getIsTranscribingSharedFlow(): SharedFlow<Boolean> {
        return isTranscribingSharedFlow
    }

    override fun getDominantSpeakersSharedFlow(): SharedFlow<DominantSpeakersInfo> {
        return dominantSpeakersSharedFlow
    }

    override fun getIsRecordingSharedFlow(): SharedFlow<Boolean> {
        return isRecordingSharedFlow
    }

    override fun getIsMutedSharedFlow(): SharedFlow<Boolean> {
        return isMutedSharedFlow
    }

    override fun getCallingStateWrapperSharedFlow(): SharedFlow<CallingStateWrapper> {
        return callingStateWrapperSharedFlow
    }

    override fun getCallIdStateFlow(): StateFlow<String?> = callIdStateFlow

    override fun getRemoteParticipantInfoModelSharedFlow(): Flow<Map<String, ParticipantInfoModel>> {
        coroutineScope.launch {
            emitRemoteParticipantFlow()
        }

        return remoteParticipantsInfoModelSharedFlow
    }

    override fun getCamerasCountStateFlow(): StateFlow<Int> = getCameraCountStateFlow
    override fun admitAll(): CompletableFuture<CallCompositeLobbyErrorCode?> {
        return lobbyResultCompletableFuture
    }

    override fun admit(userIdentifier: String): CompletableFuture<CallCompositeLobbyErrorCode?> {
        return lobbyResultCompletableFuture
    }

    override fun decline(userIdentifier: String): CompletableFuture<CallCompositeLobbyErrorCode?> {
        return lobbyResultCompletableFuture
    }

    override fun getLocalParticipantRoleSharedFlow(): SharedFlow<CallCompositeInternalParticipantRole?> {
        return participantRoleSharedFlow
    }

    override fun getNetworkQualityCallDiagnosticSharedFlow(): SharedFlow<NetworkQualityCallDiagnosticModel> {
        return networkQualityCallDiagnosticSharedFlow
    }

    override fun getNetworkCallDiagnosticSharedFlow(): SharedFlow<NetworkCallDiagnosticModel> {
        return networkCallDiagnosticSharedFlow
    }

    override fun getMediaCallDiagnosticSharedFlow(): SharedFlow<MediaCallDiagnosticModel> {
        return mediaCallDiagnosticSharedFlow
    }

    override fun getLogFiles(): List<File> {
        return emptyList()
    }

    override fun setTelecomManagerAudioRoute(audioRoute: Int) {
    }

    private fun RemoteVideoStream.asVideoStreamModel(): VideoStreamModel {
        return VideoStreamModel(
            this.id.toString(),
            when (this.mediaStreamType) {
                MediaStreamType.VIDEO -> StreamType.VIDEO
                MediaStreamType.SCREEN_SHARING -> StreamType.SCREEN_SHARING
            }
        )
    }

    private suspend fun emitRemoteParticipantFlow() {
        remoteParticipantsInfoModelSharedFlow.emit(
            synchronized(this) {
                this.getRemoteParticipantsMap().mapValues { it.value.asParticipantInfoModel() }
            }
        )
    }

    private fun RemoteParticipant.asParticipantInfoModel(): ParticipantInfoModel {
        return ParticipantInfoModel(
            displayName = this.displayName,
            userIdentifier = this.identifier.id,
            isMuted = this.isMuted,
            isSpeaking = this.isSpeaking,
            participantStatus = this.state.into(),

            screenShareVideoStreamModel = this.videoStreams.find {
                it.mediaStreamType == MediaStreamType.SCREEN_SHARING
            }?.asVideoStreamModel(),
            cameraVideoStreamModel = this.videoStreams.find {
                it.mediaStreamType == MediaStreamType.VIDEO
            }?.asVideoStreamModel(),

            modifiedTimestamp = System.currentTimeMillis(),
            isCameraDisabled = false
        )
    }
}

internal fun <T> completedFuture(f: () -> T): CompletableFuture<T> {
    return CompletableFuture<T>().also { it.complete(f.invoke()) }
}

internal fun <T> completedFuture(res: T): CompletableFuture<T> {
    return CompletableFuture<T>().also { it.complete(res) }
}

internal fun completedNullFuture(): CompletableFuture<Void> {
    return CompletableFuture<Void>().also { it.complete(null) }
}

internal fun completedNullFuture(coroutineScope: CoroutineScope, f: suspend () -> Any): CompletableFuture<Void> {
    val future = CompletableFuture<Void>()
    coroutineScope.launch {
        f.invoke()
        future.complete(null)
    }
    return future
}

internal class RemoteParticipantImpl(
    override val identifier: CommunicationIdentifier,
    override val displayName: String,
    override val isSpeaking: Boolean,
    override val isMuted: Boolean,
    override val videoStreams: List<RemoteVideoStream>,
    override val state: ParticipantState,
) : RemoteParticipant {
    override fun addOnVideoStreamsUpdatedListener(listener: RemoteVideoStreamsUpdatedListener?) {
        TODO("Not yet implemented")
    }

    override fun removeOnVideoStreamsUpdatedListener(listener: RemoteVideoStreamsUpdatedListener?) {
        TODO("Not yet implemented")
    }

    override fun addOnIsMutedChangedListener(listener: PropertyChangedListener?) {
        TODO("Not yet implemented")
    }

    override fun removeOnIsMutedChangedListener(listener: PropertyChangedListener?) {
        TODO("Not yet implemented")
    }

    override fun addOnIsSpeakingChangedListener(listener: PropertyChangedListener?) {
        TODO("Not yet implemented")
    }

    override fun removeOnIsSpeakingChangedListener(listener: PropertyChangedListener?) {
        TODO("Not yet implemented")
    }

    override fun addOnStateChangedListener(listener: PropertyChangedListener?) {
        TODO("Not yet implemented")
    }

    override fun removeOnStateChangedListener(listener: PropertyChangedListener?) {
        TODO("Not yet implemented")
    }
}

internal class RemoteVideoStreamImpl(
    override val native: Any,
    override val id: Int,
    override val mediaStreamType: MediaStreamType,
) : RemoteVideoStream
