// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.presentation.ui.chat.components

import android.widget.TextView
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
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.HtmlCompat
import com.azure.android.communication.ui.chat.presentation.style.ChatCompositeTheme
import com.azure.android.communication.ui.chat.presentation.ui.viewmodel.MessageViewModel
import com.azure.android.communication.ui.chat.presentation.ui.viewmodel.toViewModelList
import com.azure.android.communication.ui.chat.preview.MOCK_LOCAL_USER_ID
import com.azure.android.communication.ui.chat.preview.MOCK_MESSAGES
import com.azure.android.communication.ui.chat.service.sdk.wrapper.ChatMessageType
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
    if (viewModel.message.participants.size <= 1) {
        BasicText(
            "${viewModel.message.participants.firstOrNull() ?: "Unknown"} joined the chat",
            style = ChatCompositeTheme.typography.systemMessage
        )
    } else {
        BasicText(
            "${viewModel.message.participants.joinToString(", ")} joined the chat",
            style = ChatCompositeTheme.typography.systemMessage
        )
    }
}

@Composable
private fun UserLeftMessage(viewModel: MessageViewModel) {
    if (viewModel.message.participants.size <= 1) {
        BasicText(
            "${viewModel.message.participants.firstOrNull() ?: "Unknown"} left the chat",
            style = ChatCompositeTheme.typography.systemMessage
        )
    } else {
        BasicText(
            "${viewModel.message.participants.joinToString(", ")} left the chat",
            style = ChatCompositeTheme.typography.systemMessage
        )
    }
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
                    if (viewModel.message.messageType == ChatMessageType.HTML) {
                        HtmlText(html = viewModel.message.content ?: "Empty")
                    } else {
                        BasicText(
                            text = viewModel.message.content ?: "Empty"
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HtmlText(html: String, modifier: Modifier = Modifier) {
    AndroidView(
        modifier = Modifier,
        factory = {
            context ->
            TextView(context)
        },
        update = {
            it.text = HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_COMPACT)
        }
    )
}

@Preview
@Composable
internal fun PreviewChatCompositeMessage() {
    Column(
        modifier = Modifier
            .width(500.dp)
            .background(color = ChatCompositeTheme.colors.background)
    ) {
        val vms = MOCK_MESSAGES.toViewModelList(MOCK_LOCAL_USER_ID)
        for (a in 0 until vms.size) {
            MessageView(vms[a])
        }
    }
}
