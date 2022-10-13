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
import com.azure.android.communication.ui.chat.presentation.style.ChatCompositeDimensions
import com.azure.android.communication.ui.chat.presentation.style.ChatCompositeShapes
import com.azure.android.communication.ui.chat.presentation.style.ChatCompositeTheme
import com.azure.android.communication.ui.chat.presentation.ui.viewmodel.MessageViewModel
import com.azure.android.communication.ui.chat.service.sdk.wrapper.ChatMessageType
import com.azure.android.communication.ui.chat.service.sdk.wrapper.CommunicationIdentifier
import org.threeten.bp.OffsetDateTime
import java.time.format.DateTimeFormatter

val timeFormat = org.threeten.bp.format.DateTimeFormatter.ofPattern("h:m a")

@Composable
internal fun ChatCompositeMessage(viewModel: MessageViewModel) {

    when (viewModel.message.messageType) {
        ChatMessageType.TEXT -> BasicChatMessage(viewModel)
        ChatMessageType.HTML -> BasicChatMessage(viewModel)
        ChatMessageType.TOPIC_UPDATED -> BasicText("Topic Updated")
        ChatMessageType.PARTICIPANT_ADDED -> BasicText("Participant Added")
        ChatMessageType.PARTICIPANT_REMOVED -> BasicText("Participant Removed")
        else -> {
            BasicText(
                text = "${viewModel.message.content} !TYPE NOT DETECTED!" ?: "Empty"
            )
        }
    }
}

@Composable
private fun BasicChatMessage(viewModel: MessageViewModel) {
    Row(
        Modifier.padding(8.dp),
    ) {
        if (viewModel.isLocalUser) {
            Box(modifier = Modifier.weight(1.0f))
        }
        Box(modifier = Modifier.size(ChatCompositeTheme.dimensions.messageBubbleLeftSpacing)) {
            if (viewModel.showUserInfo) {
                Box(
                    Modifier
                        .size(ChatCompositeTheme.dimensions.messageAvatarSize)
                        .background(color = Color.Red)
                )
            }
        }
        Box(
            Modifier.background(
                color = when (viewModel.isLocalUser) {
                    true ->ChatCompositeTheme.colors.messageBackgroundSelf
                    false ->ChatCompositeTheme.colors.messageBackground
                },

                shape = ChatCompositeTheme.shapes.messageBubble
            )
        ) {
            Box(
                modifier = Modifier.padding(ChatCompositeTheme.dimensions.messagePadding)
            ) {
                Column {
                    if (viewModel.showUserInfo) {
                        Row() {
                            BasicText(
                                viewModel.message.senderDisplayName ?: "Unknown Sender",
                                style = ChatCompositeTheme.typography.messageHeader,
                                modifier = Modifier.padding(PaddingValues(end = ChatCompositeTheme.dimensions.messageUsernamePaddingEnd))
                            )
                            BasicText(
                                viewModel.message.createdOn?.format(timeFormat) ?: "Unknown Time",
                                style = ChatCompositeTheme.typography.messageHeaderDate,
                            )
                        }

                    }
                    BasicText(
                        text = viewModel.message.content ?: "Empty"
                    )
                }
            }

        }

    }
}

@Preview
@Composable
internal fun PreviewChatCompositeMessage() {
    Column(
    modifier = Modifier.width(500.dp)
    ) {
        val userA_ID = CommunicationIdentifier.UnknownIdentifier("User A")
        val userA_Display = "Peter Terry"

        val userB_ID = CommunicationIdentifier.UnknownIdentifier("User B")
        val userB_Display = "Local User"

        ChatCompositeMessage(
            viewModel = MessageViewModel(
                message = MessageInfoModel(
                    senderCommunicationIdentifier = userA_ID,
                    senderDisplayName = userA_Display,
                    content = "Hey!!",
                    messageType = ChatMessageType.TEXT,
                    id = null,
                    internalId = null,
                    createdOn = OffsetDateTime.parse("2007-12-23T10:15:30+01:00")
                ),
                showDateHeader = true,
                showUserInfo = true,
                isLocalUser = false,
            )
        )
        ChatCompositeMessage(
            viewModel = MessageViewModel(
                message = MessageInfoModel(
                    senderCommunicationIdentifier = userB_ID,
                    senderDisplayName = userB_Display,
                    content = "Hi Peter, thanks for following up with me",
                    messageType = ChatMessageType.TEXT,
                    id = null,
                    internalId = null,
                    createdOn = OffsetDateTime.parse("2007-12-23T10:15:30+01:00")
                ),
                showDateHeader = false,
                showUserInfo = false,
                isLocalUser = true,
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
