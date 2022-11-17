// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.configuration

import com.azure.android.communication.ui.chat.ChatCompositeEventHandler
import com.azure.android.communication.ui.chat.models.ChatCompositeEvent

class ChatCompositeEventsHandlerRepository {
    var onLocalParticipantRemovedEventHandler: ChatCompositeEventHandler<ChatCompositeEvent>? = null
}
