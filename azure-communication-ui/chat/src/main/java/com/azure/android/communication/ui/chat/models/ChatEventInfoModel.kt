// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.models

import com.azure.android.communication.ui.chat.service.sdk.wrapper.ChatEventType

internal data class ChatEventInfoModel(
    val eventType: ChatEventType,
    val infoModel: BaseInfoModel,
)
