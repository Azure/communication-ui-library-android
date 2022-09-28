// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.models

import java.lang.Error

internal data class MessagesPageModel(
    val messages: List<MessageInfoModel>?,
    val error: Error?
)
