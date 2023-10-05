// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.service.sdk

import android.util.Log
import com.azure.android.communication.calling.Call
import com.azure.android.communication.calling.CallState
import com.azure.android.communication.calling.DiagnosticFlagChangedListener
import com.azure.android.communication.calling.DiagnosticQualityChangedListener
import com.azure.android.communication.calling.DominantSpeakersCallFeature
import com.azure.android.communication.calling.LocalUserDiagnosticsCallFeature
import com.azure.android.communication.calling.MediaDiagnostics
import com.azure.android.communication.calling.MediaStreamType
import com.azure.android.communication.calling.NetworkDiagnostics
import com.azure.android.communication.calling.ParticipantsUpdatedEvent
import com.azure.android.communication.calling.ParticipantsUpdatedListener
import com.azure.android.communication.calling.PropertyChangedListener
import com.azure.android.communication.calling.RecordingCallFeature
import com.azure.android.communication.calling.RemoteParticipant
import com.azure.android.communication.calling.RemoteVideoStreamsUpdatedListener
import com.azure.android.communication.calling.TranscriptionCallFeature
import com.azure.android.communication.ui.calling.models.CallDiagnosticModel
import com.azure.android.communication.ui.calling.models.CallDiagnosticQuality
import com.azure.android.communication.ui.calling.models.MediaCallDiagnostic
import com.azure.android.communication.ui.calling.models.MediaCallDiagnosticModel
import com.azure.android.communication.ui.calling.models.NetworkCallDiagnostic
import com.azure.android.communication.ui.calling.models.NetworkCallDiagnosticModel
import com.azure.android.communication.ui.calling.models.NetworkQualityCallDiagnosticModel
import com.azure.android.communication.ui.calling.models.ParticipantInfoModel
import com.azure.android.communication.ui.calling.service.ParticipantIdentifierHelper
import com.azure.android.communication.ui.calling.utilities.CoroutineContextProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
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
    private var dominantSpeakersSharedFlow = MutableSharedFlow<DominantSpeakersInfo>()
    private var callingStateWrapperSharedFlow = MutableSharedFlow<CallingStateWrapper>()
    private var callIdSharedFlow = MutableStateFlow<String?>(null)
    private var remoteParticipantsInfoModelSharedFlow =
        MutableSharedFlow<Map<String, ParticipantInfoModel>>()

    //region Call Diagnostics
    private var networkQualityCallDiagnosticsSharedFlow = MutableSharedFlow<NetworkQualityCallDiagnosticModel>()
    private var networkCallDiagnosticsSharedFlow = MutableSharedFlow<NetworkCallDiagnosticModel>()
    private var mediaCallDiagnosticsSharedFlow = MutableSharedFlow<MediaCallDiagnosticModel>()
    //endregion

    private val remoteParticipantsInfoModelMap = mutableMapOf<String, ParticipantInfoModel>()
    private val videoStreamsUpdatedListenersMap =
        mutableMapOf<String, RemoteVideoStreamsUpdatedListener>()
    private val mutedChangedListenersMap = mutableMapOf<String, PropertyChangedListener>()
    private val isSpeakingChangedListenerMap = mutableMapOf<String, PropertyChangedListener>()
    private val isStateChangedListenerMap = mutableMapOf<String, PropertyChangedListener>()

    private val remoteParticipantsCacheMap = mutableMapOf<String, RemoteParticipant>()
    private var call: Call? = null

    private lateinit var recordingFeature: RecordingCallFeature
    private lateinit var transcriptionFeature: TranscriptionCallFeature
    private lateinit var dominantSpeakersCallFeature: DominantSpeakersCallFeature

    private var networkDiagnostics: NetworkDiagnostics? = null
    private var mediaDiagnostics: MediaDiagnostics? = null

    fun getRemoteParticipantsMap(): Map<String, RemoteParticipant> = remoteParticipantsCacheMap

    fun getCallingStateWrapperSharedFlow(): SharedFlow<CallingStateWrapper> =
        callingStateWrapperSharedFlow

    fun getCallIdStateFlow(): StateFlow<String?> = callIdSharedFlow

    fun getIsMutedSharedFlow(): SharedFlow<Boolean> = isMutedSharedFlow

    fun getIsRecordingSharedFlow(): SharedFlow<Boolean> = isRecordingSharedFlow

    fun getIsTranscribingSharedFlow(): SharedFlow<Boolean> = isTranscribingSharedFlow

    // region Call Diagnostics
    fun getNetworkQualityCallDiagnosticsSharedFlow(): SharedFlow<NetworkQualityCallDiagnosticModel> = networkQualityCallDiagnosticsSharedFlow
    fun getNetworkCallDiagnosticsSharedFlow(): SharedFlow<NetworkCallDiagnosticModel> = networkCallDiagnosticsSharedFlow
    fun getMediaCallDiagnosticsSharedFlow(): SharedFlow<MediaCallDiagnosticModel> = mediaCallDiagnosticsSharedFlow
    //endregion
    fun getDominantSpeakersSharedFlow(): SharedFlow<DominantSpeakersInfo> = dominantSpeakersSharedFlow

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
        dominantSpeakersCallFeature = call.feature { DominantSpeakersCallFeature::class.java }
        dominantSpeakersCallFeature.addOnDominantSpeakersChangedListener(onDominantSpeakersChanged)
        subscribeToUserFacingDiagnosticsEvents()
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
        dominantSpeakersCallFeature.removeOnDominantSpeakersChangedListener(onDominantSpeakersChanged)

        call?.removeOnIsMutedChangedListener(onIsMutedChanged)
        unsubscribeFromUserFacingDiagnosticsEvents()
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

    //region Call Diagnostics
    private val onNetworkReconnectionQualityChanged = DiagnosticQualityChangedListener {
        Log.d("TEST", "onNetworkReconnectionQualityChanged: ${it.name} value: ${it.value}")
        coroutineScope.launch {
            val model =
                CallDiagnosticModel(NetworkCallDiagnostic.NETWORK_RECONNECTION_QUALITY, CallDiagnosticQuality.valueOf(it.value.toString()))
            networkQualityCallDiagnosticsSharedFlow.emit(model)
        }
    }

    private val onNetworkReceiveQualityChanged = DiagnosticQualityChangedListener {
        Log.d("TEST", "onNetworkReceiveQualityChanged: ${it.name} value: ${it.value}")
        coroutineScope.launch {
            val model =
                CallDiagnosticModel(NetworkCallDiagnostic.NETWORK_RECEIVE_QUALITY, CallDiagnosticQuality.valueOf(it.value.toString()))
            networkQualityCallDiagnosticsSharedFlow.emit(model)
        }
    }

    private val onNetworkSendQualityChanged = DiagnosticQualityChangedListener {
        Log.d("TEST", "onNetworkSendQualityChanged: ${it.name} value: ${it.value}")
        coroutineScope.launch {
            val model =
                CallDiagnosticModel(NetworkCallDiagnostic.NETWORK_SEND_QUALITY, CallDiagnosticQuality.valueOf(it.value.toString()))
            networkQualityCallDiagnosticsSharedFlow.emit(model)
        }
    }

    private val onIsNetworkUnavailableChanged = DiagnosticFlagChangedListener {
        Log.d("TEST", "onIsNetworkUnavailableChanged: ${it.name} value: ${it.value}")
        coroutineScope.launch {
            val model = CallDiagnosticModel(NetworkCallDiagnostic.NETWORK_UNAVAILABLE, it.value)
            networkCallDiagnosticsSharedFlow.emit(model)
        }
    }

    private val onIsNetworkRelaysUnreachableChanged = DiagnosticFlagChangedListener {
        Log.d("TEST", "onIsNetworkRelaysUnreachableChanged: ${it.name} value: ${it.value}")
        coroutineScope.launch {
            val model = CallDiagnosticModel(NetworkCallDiagnostic.NETWORK_RELAYS_UNREACHABLE, it.value)
            networkCallDiagnosticsSharedFlow.emit(model)
        }
    }

    private val onIsSpeakerNotFunctioningChanged = DiagnosticFlagChangedListener {
        Log.d("TEST", "onIsSpeakerNotFunctioningChanged: ${it.name} value: ${it.value}")
        coroutineScope.launch {
            val model = CallDiagnosticModel(MediaCallDiagnostic.SPEAKER_NOT_FUNCTIONING, it.value)
            mediaCallDiagnosticsSharedFlow.emit(model)
        }
    }

    private val onIsSpeakerBusyChanged = DiagnosticFlagChangedListener {
        Log.d("TEST", "onIsSpeakerBusyChanged: ${it.name} value: ${it.value}")
        coroutineScope.launch {
            val model = CallDiagnosticModel(MediaCallDiagnostic.SPEAKER_BUSY, it.value)
            mediaCallDiagnosticsSharedFlow.emit(model)
        }
    }

    private val onIsSpeakerMutedChanged = DiagnosticFlagChangedListener {
        Log.d("TEST", "onIsSpeakerMutedChanged: ${it.name} value: ${it.value}")
        coroutineScope.launch {
            val model = CallDiagnosticModel(MediaCallDiagnostic.SPEAKER_MUTED, it.value)
            mediaCallDiagnosticsSharedFlow.emit(model)
        }
    }

    private val onIsSpeakerVolumeZeroChanged = DiagnosticFlagChangedListener {
        Log.d("TEST", "onIsSpeakerVolumeZeroChanged: ${it.name} value: ${it.value}")
        coroutineScope.launch {
            val model = CallDiagnosticModel(MediaCallDiagnostic.SPEAKER_VOLUME_ZERO, it.value)
            mediaCallDiagnosticsSharedFlow.emit(model)
        }
    }

    private val onIsNoSpeakerDevicesAvailableChanged = DiagnosticFlagChangedListener {
        Log.d("TEST", "onIsNoSpeakerDevicesAvailableChanged: ${it.name} value: ${it.value}")
        coroutineScope.launch {
            val model = CallDiagnosticModel(MediaCallDiagnostic.NO_SPEAKER_DEVICES_AVAILABLE, it.value)
            mediaCallDiagnosticsSharedFlow.emit(model)
        }
    }

    private val onIsSpeakingWhileMicrophoneIsMutedChanged = DiagnosticFlagChangedListener {
        Log.d("TEST", "onIsSpeakingWhileMicrophoneIsMutedChanged: ${it.name} value: ${it.value}")
        coroutineScope.launch {
            val model = CallDiagnosticModel(MediaCallDiagnostic.SPEAKING_WHILE_MICROPHONE_IS_MUTED, it.value)
            mediaCallDiagnosticsSharedFlow.emit(model)
        }
    }

    private val onIsMicrophoneNotFunctioningChanged = DiagnosticFlagChangedListener {
        Log.d("TEST", "onIsMicrophoneNotFunctioningChanged: ${it.name} value: ${it.value}")
        coroutineScope.launch {
            val model = CallDiagnosticModel(MediaCallDiagnostic.MICROPHONE_NOT_FUNCTIONING, it.value)
            mediaCallDiagnosticsSharedFlow.emit(model)
        }
    }

    private val onIsMicrophoneBusyChanged = DiagnosticFlagChangedListener {
        Log.d("TEST", "onIsMicrophoneBusyChanged: ${it.name} value: ${it.value}")
        coroutineScope.launch {
            val model = CallDiagnosticModel(MediaCallDiagnostic.MICROPHONE_BUSY, it.value)
            mediaCallDiagnosticsSharedFlow.emit(model)
        }
    }

    private val onIsMicrophoneMutedUnexpectedlyChanged = DiagnosticFlagChangedListener {
        Log.d("TEST", "onIsMicrophoneMutedUnexpectedlyChanged: ${it.name} value: ${it.value}")
        coroutineScope.launch {
            val model = CallDiagnosticModel(MediaCallDiagnostic.MICROPHONE_MUTED_UNEXPECTEDLY, it.value)
            mediaCallDiagnosticsSharedFlow.emit(model)
        }
    }

    private val onIsNoMicrophoneDevicesAvailableChanged = DiagnosticFlagChangedListener {
        Log.d("TEST", "onIsNoMicrophoneDevicesAvailableChanged: ${it.name} value: ${it.value}")
        coroutineScope.launch {
            val model = CallDiagnosticModel(MediaCallDiagnostic.NO_MICROPHONE_DEVICES_AVAILABLE, it.value)
            mediaCallDiagnosticsSharedFlow.emit(model)
        }
    }

    private val onIsCameraFrozenChanged = DiagnosticFlagChangedListener {
        Log.d("TEST", "onIsCameraFrozenChanged: ${it.name} value: ${it.value}")
        coroutineScope.launch {
            val model = CallDiagnosticModel(MediaCallDiagnostic.CAMERA_FROZEN, it.value)
            mediaCallDiagnosticsSharedFlow.emit(model)
        }
    }

    private val onIsCameraStartFailedChanged = DiagnosticFlagChangedListener {
        Log.d("TEST", "onIsCameraStartFailedChanged: ${it.name} value: ${it.value}")
        coroutineScope.launch {
            val model = CallDiagnosticModel(MediaCallDiagnostic.CAMERA_START_FAILED, it.value)
            mediaCallDiagnosticsSharedFlow.emit(model)
        }
    }

    private val onIsCameraStartTimedOutChanged = DiagnosticFlagChangedListener {
        Log.d("TEST", "onIsCameraStartTimedOutChanged: ${it.name} value: ${it.value}")
        coroutineScope.launch {
            val model = CallDiagnosticModel(MediaCallDiagnostic.CAMERA_START_TIMED_OUT, it.value)
            mediaCallDiagnosticsSharedFlow.emit(model)
        }
    }

    private val onIsCameraPermissionDeniedChanged = DiagnosticFlagChangedListener {
        Log.d("TEST", "onIsCameraPermissionDeniedChanged: ${it.name} value: ${it.value}")
        coroutineScope.launch {
            val model = CallDiagnosticModel(MediaCallDiagnostic.CAMERA_PERMISSION_DENIED, it.value)
            mediaCallDiagnosticsSharedFlow.emit(model)
        }
    }
    //endregion
    private val onDominantSpeakersChanged =
        PropertyChangedListener {
            onDominantSpeakersChanged()
        }

    private val onParticipantsUpdated =
        ParticipantsUpdatedListener {
            onParticipantsUpdated(it)
        }

    private fun onCallStateChange() {
        coroutineScope.launch {
            callIdSharedFlow.emit(call?.id)
        }

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

    private fun onDominantSpeakersChanged() {
        coroutineScope.launch {
            dominantSpeakersSharedFlow.emit(dominantSpeakersCallFeature.dominantSpeakersInfo.into())
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
        remoteParticipantsInfoModelMap[id]?.modifiedTimestamp = System.currentTimeMillis()
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
            participantStatus = participant.state.into()
        )
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
                    remoteParticipantsCacheMap[id]!!.state.into()
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
        dominantSpeakersSharedFlow = MutableSharedFlow()
        callingStateWrapperSharedFlow = MutableSharedFlow()
        callIdSharedFlow = MutableStateFlow(null)
        remoteParticipantsInfoModelSharedFlow = MutableSharedFlow()

        //region Call Diagnostics
        networkQualityCallDiagnosticsSharedFlow = MutableSharedFlow()
        networkCallDiagnosticsSharedFlow = MutableSharedFlow()
        mediaCallDiagnosticsSharedFlow = MutableSharedFlow()
        //endregion
    }

    private fun subscribeToUserFacingDiagnosticsEvents() {
        networkDiagnostics = this.call?.feature { LocalUserDiagnosticsCallFeature::class.java }?.networkDiagnostics
        networkDiagnostics?.addOnNetworkReconnectionQualityChangedListener(onNetworkReconnectionQualityChanged)
        networkDiagnostics?.addOnNetworkReceiveQualityChangedListener(onNetworkReceiveQualityChanged)
        networkDiagnostics?.addOnNetworkSendQualityChangedListener(onNetworkSendQualityChanged)
        networkDiagnostics?.addOnIsNetworkUnavailableChangedListener(onIsNetworkUnavailableChanged)
        networkDiagnostics?.addOnIsNetworkRelaysUnreachableChangedListener(onIsNetworkRelaysUnreachableChanged)

        mediaDiagnostics = call?.feature { LocalUserDiagnosticsCallFeature::class.java }?.mediaDiagnostics
        mediaDiagnostics?.addOnIsSpeakerNotFunctioningChangedListener(onIsSpeakerNotFunctioningChanged)
        mediaDiagnostics?.addOnIsSpeakerBusyChangedListener(onIsSpeakerBusyChanged)
        mediaDiagnostics?.addOnIsSpeakerMutedChangedListener(onIsSpeakerMutedChanged)
        mediaDiagnostics?.addOnIsSpeakerVolumeZeroChangedListener(onIsSpeakerVolumeZeroChanged)
        mediaDiagnostics?.addOnIsNoSpeakerDevicesAvailableChangedListener(onIsNoSpeakerDevicesAvailableChanged)
        mediaDiagnostics?.addOnIsSpeakingWhileMicrophoneIsMutedChangedListener(onIsSpeakingWhileMicrophoneIsMutedChanged)
        mediaDiagnostics?.addOnIsMicrophoneNotFunctioningChangedListener(onIsMicrophoneNotFunctioningChanged)
        mediaDiagnostics?.addOnIsMicrophoneBusyChangedListener(onIsMicrophoneBusyChanged)
        mediaDiagnostics?.addOnIsMicrophoneMutedUnexpectedlyChangedListener(onIsMicrophoneMutedUnexpectedlyChanged)
        mediaDiagnostics?.addOnIsNoMicrophoneDevicesAvailableChangedListener(onIsNoMicrophoneDevicesAvailableChanged)
        mediaDiagnostics?.addOnIsCameraFrozenChangedListener(onIsCameraFrozenChanged)
        mediaDiagnostics?.addOnIsCameraStartFailedChangedListener(onIsCameraStartFailedChanged)
        mediaDiagnostics?.addOnIsCameraStartTimedOutChangedListener(onIsCameraStartTimedOutChanged)
        mediaDiagnostics?.addOnIsCameraPermissionDeniedChangedListener(onIsCameraPermissionDeniedChanged)
    }

    private fun unsubscribeFromUserFacingDiagnosticsEvents() {
        networkDiagnostics?.removeOnNetworkReconnectionQualityChangedListener(onNetworkReconnectionQualityChanged)
        networkDiagnostics?.removeOnNetworkReceiveQualityChangedListener(onNetworkReceiveQualityChanged)
        networkDiagnostics?.removeOnNetworkSendQualityChangedListener(onNetworkSendQualityChanged)
        networkDiagnostics?.removeOnIsNetworkUnavailableChangedListener(onIsNetworkUnavailableChanged)
        networkDiagnostics?.removeOnIsNetworkRelaysUnreachableChangedListener(onIsNetworkRelaysUnreachableChanged)

        mediaDiagnostics?.removeOnIsSpeakerNotFunctioningChangedListener(onIsSpeakerNotFunctioningChanged)
        mediaDiagnostics?.removeOnIsSpeakerBusyChangedListener(onIsSpeakerBusyChanged)
        mediaDiagnostics?.removeOnIsSpeakerMutedChangedListener(onIsSpeakerMutedChanged)
        mediaDiagnostics?.removeOnIsSpeakerVolumeZeroChangedListener(onIsSpeakerVolumeZeroChanged)
        mediaDiagnostics?.removeOnIsNoSpeakerDevicesAvailableChangedListener(onIsNoSpeakerDevicesAvailableChanged)
        mediaDiagnostics?.removeOnIsSpeakingWhileMicrophoneIsMutedChangedListener(onIsSpeakingWhileMicrophoneIsMutedChanged)
        mediaDiagnostics?.removeOnIsMicrophoneNotFunctioningChangedListener(onIsMicrophoneNotFunctioningChanged)
        mediaDiagnostics?.removeOnIsMicrophoneBusyChangedListener(onIsMicrophoneBusyChanged)
        mediaDiagnostics?.removeOnIsMicrophoneMutedUnexpectedlyChangedListener(onIsMicrophoneMutedUnexpectedlyChanged)
        mediaDiagnostics?.removeOnIsNoMicrophoneDevicesAvailableChangedListener(onIsNoMicrophoneDevicesAvailableChanged)
        mediaDiagnostics?.removeOnIsCameraFrozenChangedListener(onIsCameraFrozenChanged)
        mediaDiagnostics?.removeOnIsCameraStartFailedChangedListener(onIsCameraStartFailedChanged)
        mediaDiagnostics?.removeOnIsCameraStartTimedOutChangedListener(onIsCameraStartTimedOutChanged)
        mediaDiagnostics?.removeOnIsCameraPermissionDeniedChangedListener(onIsCameraPermissionDeniedChanged)
    }
}
