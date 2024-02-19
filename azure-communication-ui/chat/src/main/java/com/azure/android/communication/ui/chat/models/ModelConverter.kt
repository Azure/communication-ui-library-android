// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.models

import org.threeten.bp.OffsetDateTime

internal object ModelConverter {
    fun fromMessageInfoModel(infoModel: MessageInfoModel) =
        ParticipantTimestampInfoModel(
            userIdentifier = infoModel.senderCommunicationIdentifier!!,
            receivedOn = infoModel.createdOn!!,
        )

    fun fromRemoteParticipantsInfoModel(infoModel: RemoteParticipantInfoModel) =
        ParticipantTimestampInfoModel(
            userIdentifier = infoModel.userIdentifier,
            receivedOn = OffsetDateTime.now(),
        )
}
