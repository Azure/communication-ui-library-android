// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.service.sdk

import com.azure.android.communication.calling.LocalVideoStream
import com.azure.android.communication.calling.RemoteParticipant
import com.azure.android.communication.ui.calling.models.ParticipantInfoModel
import com.azure.android.communication.ui.calling.redux.state.AudioState
import com.azure.android.communication.ui.calling.redux.state.CameraDeviceSelectionStatus
import com.azure.android.communication.ui.calling.redux.state.CameraState
import java9.util.concurrent.CompletableFuture
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow

/**
 * An interface that describes our interactions with the underlying calling SDK.
 */
internal interface CallingSDK {
    // Internal helpers. Refactor these out further.
    fun setupCall()
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
