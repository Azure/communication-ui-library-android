// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.service.sdk.models

import org.threeten.bp.OffsetDateTime

internal data class ListChatMessagesOptions(
    val maxPageSize: Int? = null,
    val startTime: OffsetDateTime? = null,
)
