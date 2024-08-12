// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models

import java.util.Date

internal enum class CaptionsResultType {
    FINAL,
    PARTIAL
}

internal data class CallCompositeCaptionsData(
    val resultType: CaptionsResultType,
    val speakerRawId: String,
    val speakerName: String,
    val spokenLanguage: String,
    val spokenText: String,
    val timestamp: Date,
    val captionLanguage: String? = null,
    val captionText: String? = null
)

internal fun com.azure.android.communication.calling.CaptionsResultType.into(): CaptionsResultType {
    return when (this) {
        com.azure.android.communication.calling.CaptionsResultType.FINAL -> CaptionsResultType.FINAL
        com.azure.android.communication.calling.CaptionsResultType.PARTIAL -> CaptionsResultType.PARTIAL
    }
}

internal fun com.azure.android.communication.calling.CommunicationCaptionsReceivedEvent.into(): CallCompositeCaptionsData {
    return CallCompositeCaptionsData(
        resultType = this.resultType.into(),
        speakerRawId = this.speaker.identifier.rawId,
        speakerName = this.speaker.displayName,
        spokenLanguage = this.spokenLanguage,
        spokenText = this.spokenText,
        timestamp = this.timestamp
    )
}

internal fun com.azure.android.communication.calling.TeamsCaptionsReceivedEvent.into(): CallCompositeCaptionsData {
    return CallCompositeCaptionsData(
        resultType = this.resultType.into(),
        speakerRawId = this.speaker.identifier.rawId,
        speakerName = this.speaker.displayName,
        spokenLanguage = this.spokenLanguage,
        spokenText = this.spokenText,
        timestamp = this.timestamp,
        captionLanguage = this.captionLanguage,
        captionText = this.captionText
    )
}
