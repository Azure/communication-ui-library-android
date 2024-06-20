// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models

internal enum class ParticipantStatus {
    IDLE,
    EARLY_MEDIA,
    CONNECTING,
    CONNECTED,
    HOLD,
    DISCONNECTED,
    IN_LOBBY,
    RINGING,
}

internal data class ParticipantInfoModel(
    var displayName: String,
    val userIdentifier: String,
    var isMuted: Boolean,
    var isCameraDisabled: Boolean,
    var isSpeaking: Boolean,
    var participantStatus: ParticipantStatus?,
    var screenShareVideoStreamModel: VideoStreamModel?,
    var cameraVideoStreamModel: VideoStreamModel?,
    var modifiedTimestamp: Number,
)
