// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.presentation.ui.chat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.azure.android.communication.ui.chat.models.MessageInfoModel
import com.azure.android.communication.ui.chat.presentation.style.ChatCompositeTheme
import com.azure.android.communication.ui.chat.presentation.ui.viewmodel.MessageViewModel
import com.azure.android.communication.ui.chat.service.sdk.wrapper.ChatMessageType
import com.azure.android.communication.ui.chat.service.sdk.wrapper.CommunicationIdentifier
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.format.DateTimeFormatter

val timeFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("h:m a")

@Composable
internal fun MessageView(viewModel: MessageViewModel) {
    when (viewModel.message.messageType) {
        ChatMessageType.TEXT -> BasicChatMessage(viewModel)
        ChatMessageType.HTML -> BasicChatMessage(viewModel)
        ChatMessageType.TOPIC_UPDATED -> BasicText("Topic Updated")
        ChatMessageType.PARTICIPANT_ADDED -> UserJoinedMessage(viewModel)
        ChatMessageType.PARTICIPANT_REMOVED -> UserLeftMessage(viewModel)
        else -> {
            BasicText(
                text = "${viewModel.message.content} !TYPE NOT DETECTED!" ?: "Empty"
            )
        }
    }
}

@Composable
private fun UserJoinedMessage(viewModel: MessageViewModel) {
    BasicText(
        "${viewModel.message.senderDisplayName} joined the chat",
        style = ChatCompositeTheme.typography.systemMessage
    )
}

@Composable
private fun UserLeftMessage(viewModel: MessageViewModel) {
    BasicText(
        "${viewModel.message.senderDisplayName} left the chat",
        style = ChatCompositeTheme.typography.systemMessage
    )
}

@Composable
private fun BasicChatMessage(viewModel: MessageViewModel) {
    Row(
        Modifier.padding(2.dp),
    ) {
        if (viewModel.isLocalUser) {
            Box(modifier = Modifier.weight(1.0f))
        }
        Box(modifier = Modifier.size(ChatCompositeTheme.dimensions.messageBubbleLeftSpacing)) {
            if (viewModel.showUsername) {
                AvatarView(name = viewModel.message.senderDisplayName)
            }
        }
        Box(
            Modifier.background(
                color = when (viewModel.isLocalUser) {
                    true -> ChatCompositeTheme.colors.messageBackgroundSelf
                    false -> ChatCompositeTheme.colors.messageBackground
                },

                shape = ChatCompositeTheme.shapes.messageBubble
            )
        ) {
            Box(
                modifier = Modifier.padding(ChatCompositeTheme.dimensions.messagePadding)
            ) {
                Column {
                    if (viewModel.showUsername || viewModel.showTime) {
                        Row {
                            if (viewModel.showUsername) {
                                BasicText(
                                    viewModel.message.senderDisplayName ?: "Unknown Sender",
                                    style = ChatCompositeTheme.typography.messageHeader,
                                    modifier = Modifier.padding(PaddingValues(end = ChatCompositeTheme.dimensions.messageUsernamePaddingEnd))
                                )
                            }
                            if (viewModel.showTime) {
                                BasicText(
                                    viewModel.message.createdOn?.format(timeFormat)
                                        ?: "Unknown Time",
                                    style = ChatCompositeTheme.typography.messageHeaderDate,
                                )
                            }
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
        modifier = Modifier
            .width(500.dp)
            .background(color = ChatCompositeTheme.colors.background)
    ) {
        val userA_ID = CommunicationIdentifier.UnknownIdentifier("Peter")
        val userA_Display = "Peter Terry"

        val userB_ID = CommunicationIdentifier.UnknownIdentifier("User B")
        val userB_Display = "Local User"

        val userC_ID = CommunicationIdentifier.UnknownIdentifier("Carlos")
        val userC_Display = "Carlos Slattery"

        val userD_ID = CommunicationIdentifier.UnknownIdentifier("Johnnie")
        val userD_Display = "Johnnie McConnell"

        MessageView(
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
                showUsername = true,
                showTime = true,
                isLocalUser = false,
            )
        )

        MessageView(
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
                showUsername = false,
                showTime = true,
                isLocalUser = true,
            )
        )

        MessageView(
            viewModel = MessageViewModel(
                message = MessageInfoModel(
                    content = null,
                    messageType = ChatMessageType.PARTICIPANT_ADDED,
                    senderCommunicationIdentifier = userC_ID,
                    senderDisplayName = userC_Display,
                    id = null,
                    internalId = null
                ),
                showDateHeader = false,
                showUsername = false,
                isLocalUser = false,
                showTime = false,
            )
        )

        MessageView(
            viewModel = MessageViewModel(
                message = MessageInfoModel(
                    senderCommunicationIdentifier = userA_ID,
                    senderDisplayName = userA_Display,
                    content = "No Problem",
                    messageType = ChatMessageType.TEXT,
                    id = null,
                    internalId = null,
                    createdOn = OffsetDateTime.parse("2007-12-23T10:15:30+01:00")
                ),
                showDateHeader = false,
                showUsername = true,
                showTime = true,
                isLocalUser = false,
            )
        )

        MessageView(
            viewModel = MessageViewModel(
                message = MessageInfoModel(
                    senderCommunicationIdentifier = userA_ID,
                    senderDisplayName = userA_Display,
                    content = "Let's work through the feedback we received on our wednesday meeting",
                    messageType = ChatMessageType.TEXT,
                    id = null,
                    internalId = null,
                    createdOn = OffsetDateTime.parse("2007-12-23T10:15:30+01:00")
                ),
                showDateHeader = false,
                showUsername = false,
                showTime = false,
                isLocalUser = false,
            )
        )

        MessageView(
            viewModel = MessageViewModel(
                message = MessageInfoModel(
                    content = null,
                    messageType = ChatMessageType.PARTICIPANT_REMOVED,
                    senderCommunicationIdentifier = userD_ID,
                    senderDisplayName = userD_Display,
                    id = null,
                    internalId = null
                ),
                showDateHeader = false,
                showUsername = false,
                isLocalUser = false,
                showTime = false,
            )
        )

        MessageView(
            viewModel = MessageViewModel(
                message = MessageInfoModel(
                    content = null,
                    messageType = ChatMessageType.TOPIC_UPDATED,
                    id = null,
                    internalId = null,
                ),
                showDateHeader = false,
                showUsername = false,
                isLocalUser = false,
                showTime = false
            )
        )
    }
}
