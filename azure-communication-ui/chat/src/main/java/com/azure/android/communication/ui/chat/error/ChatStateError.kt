// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.error

import com.azure.android.communication.ui.chat.models.ChatCompositeEventCode

internal class ChatStateError(
    val errorCode: ErrorCode,
    val chatCompositeEventCode: ChatCompositeEventCode? = null,
)
