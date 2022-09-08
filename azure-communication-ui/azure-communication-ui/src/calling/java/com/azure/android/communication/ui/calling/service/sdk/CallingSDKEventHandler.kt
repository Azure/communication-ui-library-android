// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.service.sdk

import com.azure.android.communication.calling.Call
import com.azure.android.communication.calling.CallState
import com.azure.android.communication.calling.CameraFacing
import com.azure.android.communication.calling.MediaStreamType
import com.azure.android.communication.calling.ParticipantsUpdatedEvent
import com.azure.android.communication.calling.ParticipantsUpdatedListener
import com.azure.android.communication.calling.PropertyChangedListener
import com.azure.android.communication.calling.RecordingCallFeature
import com.azure.android.communication.calling.RemoteParticipant
import com.azure.android.communication.calling.RemoteVideoStreamsUpdatedListener
import com.azure.android.communication.calling.TranscriptionCallFeature
import com.azure.android.communication.calling.ParticipantState
import com.azure.android.communication.calling.VideoDeviceInfo
import com.azure.android.communication.calling.VideoDeviceType
import com.azure.android.communication.ui.calling.models.ParticipantInfoModel
import com.azure.android.communication.ui.calling.models.ParticipantStatus
import com.azure.android.communication.ui.calling.models.VideoDeviceInfoModel
import com.azure.android.communication.ui.calling.service.ParticipantIdentifierHelper
import com.azure.android.communication.ui.calling.utilities.CoroutineContextProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.sample
import kotlinx.coroutines.launch

internal class CallingSDKEventHandler(
    coroutineContextProvider: CoroutineContextProvider,
) {
    companion object {
        private const val SAMPLING_PERIOD_MILLIS: Long = 1000
    }

    private val coroutineScope = CoroutineScope((coroutineContextProvider.Default))

    private var isMutedSharedFlow = MutableSharedFlow<Boolean>()
    private var isRecordingSharedFlow = MutableSharedFlow<Boolean>()
    private var isTranscribingSharedFlow = MutableSharedFlow<Boolean>()
    private var callingStateWrapperSharedFlow = MutableSharedFlow<CallingStateWrapper>()
    private var videoDevicesSharedFlow = MutableSharedFlow<Map<String, VideoDeviceInfoModel>>()

    private var remoteParticipantsInfoModelSharedFlow =
        MutableSharedFlow<Map<String, ParticipantInfoModel>>()

    private val remoteParticipantsInfoModelMap = mutableMapOf<String, ParticipantInfoModel>()
    private val videoStreamsUpdatedListenersMap =
        mutableMapOf<String, RemoteVideoStreamsUpdatedListener>()
    private val mutedChangedListenersMap = mutableMapOf<String, PropertyChangedListener>()
    private val isSpeakingChangedListenerMap = mutableMapOf<String, PropertyChangedListener>()
    private val isStateChangedListenerMap = mutableMapOf<String, PropertyChangedListener>()
    private val videoDevicesCacheMap = mutableMapOf<String, VideoDeviceInfoModel>()

    private val remoteParticipantsCacheMap = mutableMapOf<String, RemoteParticipant>()
    private var call: Call? = null

    private lateinit var recordingFeature: RecordingCallFeature
    private lateinit var transcriptionFeature: TranscriptionCallFeature

    fun getRemoteParticipantsMap(): Map<String, RemoteParticipant> = remoteParticipantsCacheMap

    fun getCallingStateWrapperSharedFlow(): SharedFlow<CallingStateWrapper> =
        callingStateWrapperSharedFlow

    fun getVideoDevicesSharedFlow(): SharedFlow<Map<String, VideoDeviceInfoModel>> =
        videoDevicesSharedFlow

    fun getIsMutedSharedFlow(): SharedFlow<Boolean> = isMutedSharedFlow

    fun getIsRecordingSharedFlow(): SharedFlow<Boolean> = isRecordingSharedFlow

    fun getIsTranscribingSharedFlow(): SharedFlow<Boolean> = isTranscribingSharedFlow

    @OptIn(FlowPreview::class)
    fun getRemoteParticipantInfoModelFlow(): Flow<Map<String, ParticipantInfoModel>> =
        remoteParticipantsInfoModelSharedFlow.sample(SAMPLING_PERIOD_MILLIS)

    fun dispose() {
        coroutineScope.cancel()
        call = null
    }

    fun onJoinCall(call: Call) {
        this.call = call
        call.addOnStateChangedListener(onCallStateChanged)
        call.addOnIsMutedChangedListener(onIsMutedChanged)
        call.addOnRemoteParticipantsUpdatedListener(onParticipantsUpdated)
        recordingFeature = call.feature { RecordingCallFeature::class.java }
        recordingFeature.addOnIsRecordingActiveChangedListener(onRecordingChanged)
        transcriptionFeature = call.feature { TranscriptionCallFeature::class.java }
        transcriptionFeature.addOnIsTranscriptionActiveChangedListener(onTranscriptionChanged)
    }

    fun onEndCall() {
        if (call == null) return
        call?.removeOnRemoteParticipantsUpdatedListener(onParticipantsUpdated)
        remoteParticipantsCacheMap.forEach { (id, remoteParticipant) ->
            remoteParticipant.removeOnVideoStreamsUpdatedListener(videoStreamsUpdatedListenersMap[id])
            remoteParticipant.removeOnIsMutedChangedListener(mutedChangedListenersMap[id])
            remoteParticipant.removeOnIsSpeakingChangedListener(isSpeakingChangedListenerMap[id])
            remoteParticipant.removeOnStateChangedListener(isStateChangedListenerMap[id])
        }
        remoteParticipantsCacheMap.clear()
        videoStreamsUpdatedListenersMap.clear()
        mutedChangedListenersMap.clear()
        isSpeakingChangedListenerMap.clear()
        isStateChangedListenerMap.clear()
        recordingFeature.removeOnIsRecordingActiveChangedListener(onRecordingChanged)
        transcriptionFeature.removeOnIsTranscriptionActiveChangedListener(
            onTranscriptionChanged
        )
        call?.removeOnIsMutedChangedListener(onIsMutedChanged)
    }

    fun onVideoDeviceUpdated(
        addedVideoDevices: MutableList<VideoDeviceInfo>,
        removedVideoDevices: MutableList<VideoDeviceInfo>,
    ) {
        removedVideoDevices.forEach {
            if (videoDevicesCacheMap.contains(it.id)) {
                videoDevicesCacheMap.remove(it.id)
            }
        }

        addedVideoDevices.forEach {
            if (!videoDevicesCacheMap.contains(it.id)) {
                videoDevicesCacheMap[it.id] = VideoDeviceInfoModel(
                    id = it.id,
                    name = it.name,
                    cameraFacing = getCameraFacing(it.cameraFacing),
                    videoDeviceType = getVideoDeviceType(it.deviceType),
                )
            }
        }

        coroutineScope.launch {
            videoDevicesSharedFlow.emit(videoDevicesCacheMap)
        }
    }

    private val onCallStateChanged =
        PropertyChangedListener {
            onCallStateChange()
        }

    private val onIsMutedChanged =
        PropertyChangedListener {
            onIsMutedChange()
        }

    private val onRecordingChanged =
        PropertyChangedListener {
            onRecordingChanged()
        }

    private val onTranscriptionChanged =
        PropertyChangedListener {
            onTranscriptionChanged()
        }

    private val onParticipantsUpdated =
        ParticipantsUpdatedListener {
            onParticipantsUpdated(it)
        }

    private fun onCallStateChange() {
        val callState = call?.state
        var callEndStatus = Pair(0, 0)

        when (callState) {
            CallState.CONNECTED -> {
                addParticipants(call!!.remoteParticipants)
                onRemoteParticipantUpdated()
            }
            CallState.NONE, CallState.DISCONNECTED -> {
                callEndStatus = call?.callEndReason?.let { callEndReason ->
                    Pair(callEndReason.code, callEndReason.subcode)
                } ?: Pair(0, 0)
                call?.removeOnStateChangedListener(onCallStateChanged)
            }
            else -> {}
        }
        callState?.let {
            coroutineScope.launch {
                callingStateWrapperSharedFlow.emit(
                    CallingStateWrapper(it, callEndStatus.first, callEndStatus.second)
                )
                if (callState == CallState.DISCONNECTED || callState == CallState.NONE) {
                    recreateFlows()
                }
            }
        }
    }

    private fun onIsMutedChange() {
        coroutineScope.launch {
            call?.isMuted.let {
                if (it != null) {
                    isMutedSharedFlow.emit(it)
                }
            }
        }
    }

    private fun onRecordingChanged() {
        coroutineScope.launch {
            isRecordingSharedFlow.emit(recordingFeature.isRecordingActive)
        }
    }

    private fun onTranscriptionChanged() {
        coroutineScope.launch {
            isTranscribingSharedFlow.emit(transcriptionFeature.isTranscriptionActive)
        }
    }

    private fun addParticipants(remoteParticipantValue: List<RemoteParticipant>) {
        remoteParticipantValue.forEach { addedParticipant ->
            val id = ParticipantIdentifierHelper.getRemoteParticipantId(addedParticipant.identifier)
            if (!remoteParticipantsCacheMap.containsKey(id)) {
                onParticipantAdded(id, addedParticipant)
            }
        }
    }

    private fun onRemoteParticipantSpeakingEvent(id: String) {
        val timestamp = System.currentTimeMillis()
        if (!remoteParticipantsInfoModelMap[id]?.isMuted!! && remoteParticipantsInfoModelMap[id]?.isSpeaking!!) {
            remoteParticipantsInfoModelMap[id]?.speakingTimestamp = timestamp
        }
        remoteParticipantsInfoModelMap[id]?.modifiedTimestamp = timestamp
        onRemoteParticipantUpdated()
    }

    private fun onRemoteParticipantPropertyChange(id: String) {
        remoteParticipantsInfoModelMap[id]?.modifiedTimestamp = System.currentTimeMillis()
        onRemoteParticipantUpdated()
    }

    private fun getInfoModelFromRemoteParticipant(participant: RemoteParticipant): ParticipantInfoModel {
        val currentTimestamp = System.currentTimeMillis()

        return ParticipantInfoModel(
            displayName = participant.displayName,
            userIdentifier = ParticipantIdentifierHelper.getRemoteParticipantId(participant.identifier),
            isMuted = participant.isMuted,
            isSpeaking = participant.isSpeaking && !participant.isMuted,
            screenShareVideoStreamModel = createVideoStreamModel(
                participant,
                MediaStreamType.SCREEN_SHARING
            ),
            cameraVideoStreamModel = createVideoStreamModel(participant, MediaStreamType.VIDEO),
            modifiedTimestamp = currentTimestamp,
            speakingTimestamp = if (participant.isSpeaking) currentTimestamp else 0,
            participantStatus = getParticipantStatus(participant.state)
        )
    }

    private fun getParticipantStatus(state: ParticipantState?): ParticipantStatus? {
        return when (state) {
            ParticipantState.IDLE -> ParticipantStatus.IDLE
            ParticipantState.EARLY_MEDIA -> ParticipantStatus.EARLY_MEDIA
            ParticipantState.CONNECTING -> ParticipantStatus.CONNECTING
            ParticipantState.HOLD -> ParticipantStatus.HOLD
            ParticipantState.DISCONNECTED -> ParticipantStatus.DISCONNECTED
            ParticipantState.IN_LOBBY -> ParticipantStatus.IN_LOBBY
            ParticipantState.RINGING -> ParticipantStatus.RINGING
            else -> null
        }
    }

    private fun createVideoStreamModel(
        participant: RemoteParticipant,
        mediaStreamType: MediaStreamType,
    ) =
        VideoStreamModelFactory.create(
            participant.videoStreams,
            mediaStreamType
        )

    private fun onParticipantsUpdated(participantsUpdatedEvent: ParticipantsUpdatedEvent) {
        participantsUpdatedEvent.addedParticipants.forEach { addedParticipant ->
            val id = ParticipantIdentifierHelper.getRemoteParticipantId(addedParticipant.identifier)
            if (!remoteParticipantsCacheMap.containsKey(id)) {
                onParticipantAdded(id, addedParticipant)
            }
        }

        participantsUpdatedEvent.removedParticipants.forEach { removedParticipant ->
            val id =
                ParticipantIdentifierHelper.getRemoteParticipantId(removedParticipant.identifier)
            if (remoteParticipantsCacheMap.containsKey(id)) {
                removedParticipant.removeOnVideoStreamsUpdatedListener(
                    videoStreamsUpdatedListenersMap[id]
                )
                removedParticipant.removeOnIsMutedChangedListener(mutedChangedListenersMap[id])
                removedParticipant.removeOnIsSpeakingChangedListener(isSpeakingChangedListenerMap[id])
                removedParticipant.removeOnStateChangedListener(isStateChangedListenerMap[id])

                videoStreamsUpdatedListenersMap.remove(id)
                mutedChangedListenersMap.remove(id)
                isSpeakingChangedListenerMap.remove(id)
                remoteParticipantsInfoModelMap.remove(id)
                remoteParticipantsCacheMap.remove(id)
                isStateChangedListenerMap.remove(id)
            }
        }

        onRemoteParticipantUpdated()
    }

    private fun onParticipantAdded(
        id: String,
        addedParticipant: RemoteParticipant,
    ) {

        val remoteVideoStreamsEvent =
            RemoteVideoStreamsUpdatedListener {
                remoteParticipantsInfoModelMap[id]?.cameraVideoStreamModel = createVideoStreamModel(
                    remoteParticipantsCacheMap.getValue(id),
                    MediaStreamType.VIDEO
                )

                remoteParticipantsInfoModelMap[id]?.screenShareVideoStreamModel =
                    createVideoStreamModel(
                        remoteParticipantsCacheMap.getValue(id),
                        MediaStreamType.SCREEN_SHARING
                    )
                onRemoteParticipantPropertyChange(id)
            }

        remoteParticipantsCacheMap[id] = addedParticipant
        remoteParticipantsInfoModelMap[id] = getInfoModelFromRemoteParticipant(addedParticipant)

        videoStreamsUpdatedListenersMap[id] = remoteVideoStreamsEvent
        addedParticipant.addOnVideoStreamsUpdatedListener(videoStreamsUpdatedListenersMap[id])

        val addOnIsMutedChangedEvent =
            PropertyChangedListener {
                remoteParticipantsInfoModelMap[id]?.isMuted =
                    remoteParticipantsCacheMap[id]!!.isMuted

                remoteParticipantsInfoModelMap[id]?.isSpeaking =
                    !remoteParticipantsInfoModelMap[id]?.isMuted!! && remoteParticipantsInfoModelMap[id]?.isSpeaking!!
                onRemoteParticipantSpeakingEvent(id)
            }

        mutedChangedListenersMap[id] = addOnIsMutedChangedEvent
        addedParticipant.addOnIsMutedChangedListener(mutedChangedListenersMap[id])

        val addOnIsStateChangedEvent =
            PropertyChangedListener {
                remoteParticipantsInfoModelMap[id]?.participantStatus =
                    getParticipantStatus(remoteParticipantsCacheMap[id]!!.state)
                onRemoteParticipantPropertyChange(id)
            }
        isStateChangedListenerMap[id] = addOnIsStateChangedEvent
        addedParticipant.addOnStateChangedListener(isStateChangedListenerMap[id])

        val addOnIsSpeakingChangedEvent =
            PropertyChangedListener {
                remoteParticipantsInfoModelMap[id]?.isSpeaking =
                    remoteParticipantsCacheMap[id]!!.isSpeaking
                onRemoteParticipantSpeakingEvent(id)
            }

        isSpeakingChangedListenerMap[id] = addOnIsSpeakingChangedEvent
        addedParticipant.addOnIsSpeakingChangedListener(isSpeakingChangedListenerMap[id])
    }

    private fun onRemoteParticipantUpdated() {
        val state = call?.state
        if (state == CallState.CONNECTED) {
            coroutineScope.launch {
                remoteParticipantsInfoModelSharedFlow.emit(remoteParticipantsInfoModelMap)
            }
        }
    }

    private fun recreateFlows() {
        isMutedSharedFlow = MutableSharedFlow()
        isRecordingSharedFlow = MutableSharedFlow()
        isTranscribingSharedFlow = MutableSharedFlow()
        callingStateWrapperSharedFlow = MutableSharedFlow()
        remoteParticipantsInfoModelSharedFlow = MutableSharedFlow()
    }

    private fun getVideoDeviceType(deviceType: VideoDeviceType?): com.azure.android.communication.ui.calling.models.VideoDeviceType {

        return when (deviceType) {
            VideoDeviceType.USB_CAMERA -> com.azure.android.communication.ui.calling.models.VideoDeviceType.USB_CAMERA
            VideoDeviceType.CAPTURE_ADAPTER -> com.azure.android.communication.ui.calling.models.VideoDeviceType.CAPTURE_ADAPTER
            VideoDeviceType.VIRTUAL -> com.azure.android.communication.ui.calling.models.VideoDeviceType.VIRTUAL

            else -> {
                com.azure.android.communication.ui.calling.models.VideoDeviceType.UNKNOWN
            }
        }
    }

    private fun getCameraFacing(cameraFacing: CameraFacing?): com.azure.android.communication.ui.calling.models.CameraFacing {
        return when (cameraFacing) {
            CameraFacing.FRONT -> com.azure.android.communication.ui.calling.models.CameraFacing.FRONT
            CameraFacing.BACK -> com.azure.android.communication.ui.calling.models.CameraFacing.BACK
            CameraFacing.EXTERNAL -> com.azure.android.communication.ui.calling.models.CameraFacing.EXTERNAL
            CameraFacing.PANORAMIC -> com.azure.android.communication.ui.calling.models.CameraFacing.PANORAMIC
            CameraFacing.LEFT_FRONT -> com.azure.android.communication.ui.calling.models.CameraFacing.LEFT_FRONT
            CameraFacing.RIGHT_FRONT -> com.azure.android.communication.ui.calling.models.CameraFacing.RIGHT_FRONT

            else -> {
                com.azure.android.communication.ui.calling.models.CameraFacing.UNKNOWN
            }
        }
    }
}
