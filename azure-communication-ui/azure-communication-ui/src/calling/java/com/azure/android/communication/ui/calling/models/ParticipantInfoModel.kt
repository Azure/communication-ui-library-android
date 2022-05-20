// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models

internal data class ParticipantInfoModel(
    val displayName: String,
    val userIdentifier: String,
    var isOnHold: Boolean,
    var isMuted: Boolean,
    var isSpeaking: Boolean,
    var screenShareVideoStreamModel: VideoStreamModel?,
    var cameraVideoStreamModel: VideoStreamModel?,
    var modifiedTimestamp: Number,
    var speakingTimestamp: Number,
)
