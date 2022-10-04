// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.models

import org.threeten.bp.OffsetDateTime

internal data class ChatThreadInfoModel(
    val topic: String? = null,
    val receivedOn: OffsetDateTime,
) : BaseInfoModel
