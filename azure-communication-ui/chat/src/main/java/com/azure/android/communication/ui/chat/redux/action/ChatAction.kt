// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.redux.action

import com.azure.android.communication.ui.chat.error.ChatStateError
import com.azure.android.communication.ui.chat.models.ParticipantInfoModel

internal sealed class ChatAction : Action {
    class Initialization : ChatAction()
    class Initialized : ChatAction()
    class Error(val chatStateError: ChatStateError) : ChatAction()
    class LocalParticipantInfo(val localParticipantInfoModel: ParticipantInfoModel) : ChatAction()
    class ChatThreadID(val chatThreadId: String) : ChatAction()
}
