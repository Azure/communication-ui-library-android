// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.service.sdk

/*  <CALL_START_TIME> */
import android.view.View
import com.azure.android.communication.calling.CameraFacing
import com.azure.android.communication.calling.CreateViewOptions
import com.azure.android.communication.calling.MediaStreamType
import com.azure.android.communication.calling.ParticipantState
import com.azure.android.communication.calling.PropertyChangedListener
import com.azure.android.communication.calling.RemoteVideoStreamsUpdatedListener
import com.azure.android.communication.calling.ScalingMode
import com.azure.android.communication.calling.VideoDeviceType
import com.azure.android.communication.ui.calling.models.CallCompositeCaptionsData
import com.azure.android.communication.ui.calling.models.CallCompositeCaptionsType
import com.azure.android.communication.ui.calling.models.CallCompositeLobbyErrorCode
import com.azure.android.communication.ui.calling.models.CapabilitiesChangedEvent
import com.azure.android.communication.ui.calling.models.MediaCallDiagnosticModel
import com.azure.android.communication.ui.calling.models.NetworkCallDiagnosticModel
import com.azure.android.communication.ui.calling.models.NetworkQualityCallDiagnosticModel
import com.azure.android.communication.ui.calling.models.ParticipantCapabilityType
import com.azure.android.communication.ui.calling.models.ParticipantInfoModel
import com.azure.android.communication.ui.calling.models.ParticipantRole
import com.azure.android.communication.ui.calling.models.RttMessage
import com.azure.android.communication.ui.calling.redux.state.AudioState
import com.azure.android.communication.ui.calling.redux.state.CameraDeviceSelectionStatus
import com.azure.android.communication.ui.calling.redux.state.CameraState
import java9.util.concurrent.CompletableFuture
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.File
import java.util.Date
/* </CALL_START_TIME> */

/**
 * An interface that describes our interactions with the underlying calling SDK.
 */
internal interface CallingSDK {
    // Internal helpers. Refactor these out further.
    fun setupCall(): CompletableFuture<Void>
    fun dispose()

    // Interactions.
    fun turnOnVideoAsync(): CompletableFuture<LocalVideoStream>
    fun turnOffVideoAsync(): CompletableFuture<Void>
    fun turnOnMicAsync(): CompletableFuture<Void>
    fun turnOffMicAsync(): CompletableFuture<Void>
    fun switchCameraAsync(): CompletableFuture<CameraDeviceSelectionStatus>
    fun startCall(cameraState: CameraState, audioState: AudioState): CompletableFuture<Void>
    fun endCall(): CompletableFuture<Void>
    fun hold(): CompletableFuture<Void>
    fun resume(): CompletableFuture<Void>

    // State.
    fun getLocalVideoStream(): CompletableFuture<LocalVideoStream>
    fun getRemoteParticipantsMap(): Map<String, RemoteParticipant>
    fun getIsTranscribingSharedFlow(): SharedFlow<Boolean>
    fun getDominantSpeakersSharedFlow(): SharedFlow<DominantSpeakersInfo>
    fun getIsRecordingSharedFlow(): SharedFlow<Boolean>
    fun getIsMutedSharedFlow(): SharedFlow<Boolean>
    fun getCallingStateWrapperSharedFlow(): SharedFlow<CallingStateWrapper>
    fun getCallIdStateFlow(): StateFlow<String?>
    fun getRemoteParticipantInfoModelSharedFlow(): Flow<Map<String, ParticipantInfoModel>>
    /*  <CALL_START_TIME> */
    fun getCallStartTimeSharedFlow(): SharedFlow<Date>
    /* </CALL_START_TIME> */
    fun getCamerasCountStateFlow(): StateFlow<Int>
    fun admitAll(): CompletableFuture<CallCompositeLobbyErrorCode?>
    fun admit(userIdentifier: String): CompletableFuture<CallCompositeLobbyErrorCode?>
    fun reject(userIdentifier: String): CompletableFuture<CallCompositeLobbyErrorCode?>
    fun removeParticipant(userIdentifier: String): CompletableFuture<Void>
    fun getLocalParticipantRoleSharedFlow(): SharedFlow<ParticipantRole?>
    fun getTotalRemoteParticipantCountSharedFlow(): SharedFlow<Int>
    fun getCapabilitiesChangedEventSharedFlow(): SharedFlow<CapabilitiesChangedEvent>
    fun getCapabilities(): Set<ParticipantCapabilityType>

    //region Call Diagnostics
    fun getNetworkQualityCallDiagnosticSharedFlow(): SharedFlow<NetworkQualityCallDiagnosticModel>
    fun getNetworkCallDiagnosticSharedFlow(): SharedFlow<NetworkCallDiagnosticModel>
    fun getMediaCallDiagnosticSharedFlow(): SharedFlow<MediaCallDiagnosticModel>
    fun getLogFiles(): List<File>

    //endregion

    fun getRttSharedFlow(): SharedFlow<RttMessage>
    fun sendRttMessage(message: String, isFinalized: Boolean)

    fun setTelecomManagerAudioRoute(audioRoute: Int)

    //region Captions
    fun startCaptions(spokenLanguage: String?): CompletableFuture<Void>
    fun stopCaptions(): CompletableFuture<Void>
    fun setCaptionsSpokenLanguage(language: String): CompletableFuture<Void>
    fun setCaptionsCaptionLanguage(language: String): CompletableFuture<Void>
    fun getCaptionsSupportedSpokenLanguagesSharedFlow(): SharedFlow<List<String>>
    fun getCaptionsSupportedCaptionLanguagesSharedFlow(): SharedFlow<List<String>>
    fun getIsCaptionsTranslationSupportedSharedFlow(): SharedFlow<Boolean>
    fun getCaptionsReceivedSharedFlow(): SharedFlow<CallCompositeCaptionsData>
    fun getActiveSpokenLanguageChangedSharedFlow(): SharedFlow<String>
    fun getActiveCaptionLanguageChangedSharedFlow(): SharedFlow<String>
    fun getCaptionsEnabledChangedSharedFlow(): SharedFlow<Boolean>
    fun getCaptionsTypeChangedSharedFlow(): SharedFlow<CallCompositeCaptionsType>
    //endregion

    /*  <CALL_START_TIME> */
    fun getCallStartTime(): Date?
    /* </CALL_START_TIME> */
}

internal interface RemoteParticipant {
    val identifier: CommunicationIdentifier
    val displayName: String
    val isSpeaking: Boolean
    val isMuted: Boolean
    val state: ParticipantState
    val videoStreams: List<RemoteVideoStream>
    fun addOnVideoStreamsUpdatedListener(listener: RemoteVideoStreamsUpdatedListener?)
    fun removeOnVideoStreamsUpdatedListener(listener: RemoteVideoStreamsUpdatedListener?)
    fun addOnIsMutedChangedListener(listener: PropertyChangedListener?)
    fun removeOnIsMutedChangedListener(listener: PropertyChangedListener?)
    fun addOnIsSpeakingChangedListener(listener: PropertyChangedListener?)
    fun removeOnIsSpeakingChangedListener(listener: PropertyChangedListener?)
    fun addOnStateChangedListener(listener: PropertyChangedListener?)
    fun removeOnStateChangedListener(listener: PropertyChangedListener?)
}

internal interface DominantSpeakersInfo {
    val speakers: List<String>
}

internal sealed class CommunicationIdentifier(val id: String) {
    data class CommunicationUserIdentifier(val userId: String) : CommunicationIdentifier(userId)
    data class MicrosoftTeamsUserIdentifier(val userId: String, val isAnonymous: Boolean) :
        CommunicationIdentifier(userId)

    data class PhoneNumberIdentifier(val phoneNumber: String) : CommunicationIdentifier(phoneNumber)
    data class UnknownIdentifier(val genericId: String) : CommunicationIdentifier(genericId)
}

internal interface RemoteVideoStream {
    val native: Any
    val id: Int
    val mediaStreamType: MediaStreamType
}

internal interface LocalVideoStream {
    val native: Any
    val source: VideoDeviceInfo
    fun switchSource(deviceInfo: VideoDeviceInfo): CompletableFuture<Void>
}

internal data class VideoDeviceInfo(
    val native: Any,
    val id: String,
    val name: String,
    val cameraFacing: CameraFacing,
    val deviceType: VideoDeviceType,
)

internal interface VideoStreamRenderer {
    fun createView(): VideoStreamRendererView?
    fun createView(options: CreateViewOptions): VideoStreamRendererView?
    fun dispose()
    fun getStreamSize(): StreamSize?
}

internal interface VideoStreamRendererView {
    fun dispose()
    fun getView(): View?
    fun updateScalingMode(scalingMode: ScalingMode)
}

internal data class StreamSize(val width: Int, val height: Int)
