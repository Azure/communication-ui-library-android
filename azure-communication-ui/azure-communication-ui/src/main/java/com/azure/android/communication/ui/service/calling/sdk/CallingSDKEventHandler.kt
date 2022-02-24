// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.service.calling.sdk

import com.azure.android.communication.calling.Call
import com.azure.android.communication.calling.CallState
import com.azure.android.communication.calling.MediaStreamType
import com.azure.android.communication.calling.ParticipantsUpdatedEvent
import com.azure.android.communication.calling.ParticipantsUpdatedListener
import com.azure.android.communication.calling.PropertyChangedListener
import com.azure.android.communication.calling.RecordingCallFeature
import com.azure.android.communication.calling.RemoteParticipant
import com.azure.android.communication.calling.RemoteVideoStreamsUpdatedListener
import com.azure.android.communication.calling.TranscriptionCallFeature
import com.azure.android.communication.common.CommunicationUserIdentifier
import com.azure.android.communication.common.MicrosoftTeamsUserIdentifier
import com.azure.android.communication.common.PhoneNumberIdentifier
import com.azure.android.communication.common.UnknownIdentifier
import com.azure.android.communication.ui.model.ParticipantInfoModel
import com.azure.android.communication.ui.utilities.CoroutineContextProvider
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
    private var remoteParticipantsInfoModelSharedFlow =
        MutableSharedFlow<Map<String, ParticipantInfoModel>>()

    private val remoteParticipantsInfoModelMap = mutableMapOf<String, ParticipantInfoModel>()
    private val videoStreamsUpdatedListenersMap =
        mutableMapOf<String, RemoteVideoStreamsUpdatedListener>()
    private val mutedChangedListenersMap = mutableMapOf<String, PropertyChangedListener>()
    private val isSpeakingChangedListenerMap = mutableMapOf<String, PropertyChangedListener>()

    private val remoteParticipantsCacheMap = mutableMapOf<String, RemoteParticipant>()
    private var call: Call? = null

    private lateinit var recordingFeature: RecordingCallFeature
    private lateinit var transcriptionFeature: TranscriptionCallFeature

    fun getRemoteParticipantsMap() = remoteParticipantsCacheMap

    fun getCallingStateWrapperSharedFlow(): SharedFlow<CallingStateWrapper> =
        callingStateWrapperSharedFlow

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
        }
        remoteParticipantsCacheMap.clear()
        videoStreamsUpdatedListenersMap.clear()
        mutedChangedListenersMap.clear()
        isSpeakingChangedListenerMap.clear()
        recordingFeature.removeOnIsRecordingActiveChangedListener(onRecordingChanged)
        transcriptionFeature.removeOnIsTranscriptionActiveChangedListener(
            onTranscriptionChanged
        )
        call?.removeOnIsMutedChangedListener(onIsMutedChanged)
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
        var callState = call?.state
        var callEndReason = 0

        when (callState) {
            CallState.CONNECTED -> {
                addParticipants(call!!.remoteParticipants)
                onRemoteParticipantUpdated()
            }
            CallState.NONE, CallState.DISCONNECTED -> {
                val code = call!!.callEndReason.code
                callEndReason = code
                call?.removeOnStateChangedListener(onCallStateChanged)
            }
        }
        coroutineScope.launch {
            if (callState != null) {
                callingStateWrapperSharedFlow.emit(CallingStateWrapper(callState, callEndReason))
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
            val id = getRemoteParticipantId(addedParticipant)
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
            participant.displayName,
            getRemoteParticipantId(participant),
            participant.isMuted,
            participant.isSpeaking && !participant.isMuted,
            createVideoStreamModel(participant, MediaStreamType.SCREEN_SHARING),
            createVideoStreamModel(participant, MediaStreamType.VIDEO),
            currentTimestamp,
            if (participant.isSpeaking) currentTimestamp else 0
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
            val id = getRemoteParticipantId(addedParticipant)
            if (!remoteParticipantsCacheMap.containsKey(id)) {
                onParticipantAdded(id, addedParticipant)
            }
        }

        participantsUpdatedEvent.removedParticipants.forEach { removedParticipant ->
            val id = getRemoteParticipantId(removedParticipant)
            if (remoteParticipantsCacheMap.containsKey(id)) {
                removedParticipant.removeOnVideoStreamsUpdatedListener(
                    videoStreamsUpdatedListenersMap[id]
                )
                removedParticipant.removeOnIsMutedChangedListener(mutedChangedListenersMap[id])
                removedParticipant.removeOnIsSpeakingChangedListener(isSpeakingChangedListenerMap[id])
                videoStreamsUpdatedListenersMap.remove(id)
                mutedChangedListenersMap.remove(id)
                isSpeakingChangedListenerMap.remove(id)
                remoteParticipantsInfoModelMap.remove(id)
                remoteParticipantsCacheMap.remove(id)
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

        val addOnIsSpeakingChangedEvent =
            PropertyChangedListener {
                remoteParticipantsInfoModelMap[id]?.isSpeaking =
                    remoteParticipantsCacheMap[id]!!.isSpeaking
                onRemoteParticipantSpeakingEvent(id)
            }

        isSpeakingChangedListenerMap[id] = addOnIsSpeakingChangedEvent
        addedParticipant.addOnIsSpeakingChangedListener(isSpeakingChangedListenerMap[id])
    }

    private fun getRemoteParticipantId(remoteParticipant: RemoteParticipant): String {
        return when (val identifier = remoteParticipant.identifier) {
            is PhoneNumberIdentifier -> {
                identifier.phoneNumber
            }
            is MicrosoftTeamsUserIdentifier -> {
                identifier.userId
            }
            is CommunicationUserIdentifier -> {
                identifier.id
            }
            else -> {
                (identifier as UnknownIdentifier).id
            }
        }
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
}
