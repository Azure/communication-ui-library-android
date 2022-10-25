// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.models

import com.azure.android.communication.ui.chat.service.sdk.wrapper.CommunicationIdentifier
import org.threeten.bp.OffsetDateTime

internal data class ParticipantTimestampInfoModel(
    val userIdentifier: CommunicationIdentifier,
    val receivedOn: OffsetDateTime,
) : BaseInfoModel
