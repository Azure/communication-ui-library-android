// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.presentation.ui.chat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.azure.android.communication.ui.chat.models.MessageInfoModel
import com.azure.android.communication.ui.chat.presentation.ui.viewmodel.MessageViewModel
import com.azure.android.communication.ui.chat.service.sdk.wrapper.ChatMessageType

@Composable
internal fun ChatCompositeMessage(viewModel: MessageViewModel) {


    Row(
        Modifier.padding(8.dp)
    ) {
        if (viewModel.showUserInfo) {
            Box(
                Modifier
                    .size(32.dp)
                    .background(color = Color.Red)
            )
        }
        Box(
            Modifier.background(
                color = Color.White
            )
        ) {
            Column {
                if (viewModel.showUserInfo) {
                    Row() {
                        BasicText(viewModel.message.senderDisplayName ?: "Unknown Sender")
                        BasicText(viewModel.message.createdOn?.toString() ?: "Unknown Time", modifier = Modifier.padding(
                            PaddingValues(horizontal = 20.dp)))
                    }

                }
                when (viewModel.message.messageType) {
                    ChatMessageType.TEXT -> BasicText(text = viewModel.message.content ?: "Empty")
                    ChatMessageType.HTML -> BasicText(text = viewModel.message.content ?: "Empty")
                    ChatMessageType.TOPIC_UPDATED -> BasicText("Topic Updated")
                    ChatMessageType.PARTICIPANT_ADDED -> BasicText("Participant Added")
                    ChatMessageType.PARTICIPANT_REMOVED -> BasicText("Participant Removed")
                    else -> {
                        BasicText(text = "${viewModel.message.content} !TYPE NOT DETECTED!" ?: "Empty")
                    }
                }

            }

        }

    }
}

@Preview
@Composable
internal fun PreviewChatCompositeMessage() {
    Column(

    ) {
        ChatCompositeMessage(
            viewModel = MessageViewModel(
                message = MessageInfoModel(
                    content = "Hello World",
                    messageType = ChatMessageType.TEXT,
                    id = null,
                    internalId = null
                ),
                showDateHeader = false,
                showUserInfo = false,
                isLocalUser = false,
            )
        )
        ChatCompositeMessage(
            viewModel = MessageViewModel(
                message = MessageInfoModel(
                    content = "Hello World",
                    messageType = ChatMessageType.TEXT,
                    id = null,
                    internalId = null
                ),
                showDateHeader = false,
                showUserInfo = true,
                isLocalUser = false,
            )
        )
        ChatCompositeMessage(
            viewModel = MessageViewModel(
                message = MessageInfoModel(
                    content = null,
                    messageType = ChatMessageType.PARTICIPANT_ADDED,
                    id = null,
                    internalId = null
                ),
                showDateHeader = false,
                showUserInfo = false,
                isLocalUser = false,

            )
        )
        ChatCompositeMessage(
            viewModel = MessageViewModel(
                message = MessageInfoModel(
                    content = null,
                    messageType = ChatMessageType.PARTICIPANT_REMOVED,
                    id = null,
                    internalId = null
                ),
                showDateHeader = false,
                showUserInfo = false,
                isLocalUser = false,

            )
        )
        ChatCompositeMessage(
            viewModel = MessageViewModel(
                message = MessageInfoModel(
                    content = null,
                    messageType = ChatMessageType.TOPIC_UPDATED,
                    id = null,
                    internalId = null,
                ),
                showDateHeader = false,
                showUserInfo = false,
                isLocalUser = false,
            )
        )
    }
}
