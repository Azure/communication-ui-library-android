// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.redux.action

import com.azure.android.communication.ui.chat.models.MessageInfoModel

internal sealed class ChatAction : Action {
    class StartChat : ChatAction()

    class Initialization : ChatAction()

    class Initialized : ChatAction()

    class ThreadDeleted : ChatAction()

    class TopicUpdated(val topic: String) : ChatAction()

    class SendMessage(val messageInfoModel: MessageInfoModel) : ChatAction()

    class MessageSent(val messageInfoModel: MessageInfoModel, val id: String) : ChatAction()

    class MessageSentFailed(val messageInfoModel: MessageInfoModel) : ChatAction()

    class DeleteMessage(val message: MessageInfoModel) : ChatAction()

    class MessageDeleted(val message: MessageInfoModel) : ChatAction()

    class FetchMessages : ChatAction()

    class AllMessagesFetched : ChatAction()

    class MessagesPageReceived(val messages: List<MessageInfoModel>) : ChatAction()

    class EndChat : ChatAction()

    class MessageReceived(val message: MessageInfoModel) : ChatAction()

    class EditMessage(val message: MessageInfoModel) : ChatAction()

    class MessageEdited(val message: MessageInfoModel) : ChatAction()

    class MessageRead(val messageId: String) : ChatAction()

    class MessageLastReceived(val messageId: String) : ChatAction()

    class TypingIndicator : ChatAction()

    class ShowMessageContextMenu(val message: MessageInfoModel) : ChatAction()

    class HideMessageContextMenu : ChatAction()

    class CopyMessageText(val message: MessageInfoModel) : ChatAction()
}
