package com.azure.android.communication.ui.chat.presentation.ui.chat.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.azure.android.communication.ui.chat.models.MessageInfoModel
import com.azure.android.communication.ui.chat.presentation.ui.viewmodel.MessageViewModel
import com.azure.android.communication.ui.chat.service.sdk.wrapper.ChatMessageType

@Composable
internal fun ChatCompositeMessage(viewModel: MessageViewModel) {
    when (viewModel.message.messageType) {
        ChatMessageType.TEXT -> BasicText(text = viewModel.message.content ?: "Empty")
        ChatMessageType.HTML -> BasicText(text = viewModel.message.content ?: "Empty")
        ChatMessageType.TOPIC_UPDATED -> BasicText("Topic Updated")
        ChatMessageType.PARTICIPANT_ADDED -> BasicText("Participant Added")
        ChatMessageType.PARTICIPANT_REMOVED -> BasicText("Participant Removed")
    }
}

@Preview
@Composable
internal fun PreviewChatCompositeMessage() {
    Column() {
        ChatCompositeMessage(
            viewModel = MessageViewModel(
                message = MessageInfoModel(
                    content = "Hello World",
                    messageType = ChatMessageType.TEXT,
                    id = null,
                    internalId = null
                )
            )
        )
        ChatCompositeMessage(
            viewModel = MessageViewModel(
                message = MessageInfoModel(
                    content = null,
                    messageType = ChatMessageType.PARTICIPANT_ADDED,
                    id = null,
                    internalId = null
                )
            )
        )
        ChatCompositeMessage(
            viewModel = MessageViewModel(
                message = MessageInfoModel(
                    content = null,
                    messageType = ChatMessageType.PARTICIPANT_REMOVED,
                    id = null,
                    internalId = null
                )
            )
        )
        ChatCompositeMessage(
            viewModel = MessageViewModel(
                message = MessageInfoModel(
                    content = null,
                    messageType = ChatMessageType.TOPIC_UPDATED,
                    id = null,
                    internalId = null
                )
            )
        )
    }
}
