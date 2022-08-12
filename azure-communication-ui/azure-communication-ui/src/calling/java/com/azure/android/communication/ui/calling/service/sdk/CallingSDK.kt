// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.service.sdk

import android.view.View
import com.azure.android.communication.calling.*
import com.azure.android.communication.ui.calling.models.ParticipantInfoModel
import com.azure.android.communication.ui.calling.redux.state.AudioState
import com.azure.android.communication.ui.calling.redux.state.CameraDeviceSelectionStatus
import com.azure.android.communication.ui.calling.redux.state.CameraState
import com.azure.android.communication.ui.calling.service.sdk.LocalVideoStream
import com.azure.android.communication.ui.calling.service.sdk.RemoteParticipant
import com.azure.android.communication.ui.calling.service.sdk.RemoteVideoStream
import com.azure.android.communication.ui.calling.service.sdk.StreamSize
import com.azure.android.communication.ui.calling.service.sdk.VideoDeviceInfo
import com.azure.android.communication.ui.calling.service.sdk.VideoStreamRendererView
import java9.util.concurrent.CompletableFuture
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow

/**
 * An interface that describes our interactions with the underlying calling SDK.
 */
internal interface CallingSDK {
    // Internal helpers. Refactor these out further.
    fun setupCall(): CompletableFuture<DeviceManager>
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
    fun getIsRecordingSharedFlow(): SharedFlow<Boolean>
    fun getIsMutedSharedFlow(): SharedFlow<Boolean>
    fun getCallingStateWrapperSharedFlow(): SharedFlow<CallingStateWrapper>
    fun getRemoteParticipantInfoModelSharedFlow(): Flow<Map<String, ParticipantInfoModel>>
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
}

internal data class StreamSize(val width: Int, val height: Int)
