// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.calling.captions

import android.graphics.Bitmap
import com.azure.android.communication.common.CommunicationIdentifier
import com.azure.android.communication.ui.calling.presentation.manager.AvatarViewManager
import java.util.Date

internal data class CaptionsRecord(
    val displayName: String,
    val displayText: String,
    val speakerRawId: String,
    val languageCode: String,
    val isFinal: Boolean,
    val timestamp: Date
)

internal fun CaptionsRecord.into(avatarViewManager: AvatarViewManager, identifier: CommunicationIdentifier?): CaptionsRttEntryModel {
    var speakerName = this.displayName
    var bitMap: Bitmap? = null

    val remoteParticipantViewData = avatarViewManager.getRemoteParticipantViewData(this.speakerRawId)
    if (remoteParticipantViewData != null) {
        speakerName = remoteParticipantViewData.displayName
        bitMap = remoteParticipantViewData.avatarBitmap
    }
    val localParticipantViewData = avatarViewManager.callCompositeLocalOptions?.participantViewData
    if (localParticipantViewData != null && identifier?.rawId == this.speakerRawId) {
        speakerName = localParticipantViewData.displayName
        bitMap = localParticipantViewData.avatarBitmap
    }
    return CaptionsRttEntryModel(
        displayName = speakerName,
        displayText = this.displayText,
        avatarBitmap = bitMap,
        speakerRawId = this.speakerRawId,
        languageCode = this.languageCode
    )
}
