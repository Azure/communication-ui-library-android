// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.models

internal data class ChatInfoModel(
    val threadId: String,
    val topic: String?,
    val allMessagesFetched: Boolean = false,
    val isThreadDeleted: Boolean = false,
)
