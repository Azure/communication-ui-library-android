// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.presentation.ui.chat.components

import android.widget.TextView
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.width

import androidx.compose.foundation.text.BasicText
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.HtmlCompat
import com.azure.android.communication.ui.chat.R
import com.azure.android.communication.ui.chat.presentation.style.ChatCompositeTheme
import com.azure.android.communication.ui.chat.presentation.ui.chat.UITestTags
import com.azure.android.communication.ui.chat.presentation.ui.viewmodel.MessageViewModel
import com.azure.android.communication.ui.chat.presentation.ui.viewmodel.toViewModelList
import com.azure.android.communication.ui.chat.preview.MOCK_LOCAL_USER_ID
import com.azure.android.communication.ui.chat.preview.MOCK_MESSAGES
import com.azure.android.communication.ui.chat.redux.Dispatch
import com.azure.android.communication.ui.chat.service.sdk.wrapper.ChatMessageType
import com.jakewharton.threetenabp.AndroidThreeTen
import com.microsoft.fluentui.persona.AvatarSize
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter

val timeFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("h:mm a")

@Composable
internal fun MessageView(viewModel: MessageViewModel, dispatch: Dispatch) {

    Column(
        modifier = Modifier.padding(ChatCompositeTheme.dimensions.messageOuterPadding),
    ) {

        // Date Header Part
        if (viewModel.dateHeaderText != null) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        ChatCompositeTheme.dimensions.dateHeaderPadding
                    )
            ) {
                BasicText(
                    viewModel.dateHeaderText,
                    style = ChatCompositeTheme.typography.messageHeaderDate
                )
            }
        }
        when (viewModel.message.messageType) {
            ChatMessageType.TEXT -> BasicChatMessage(viewModel, dispatch)
            ChatMessageType.HTML -> BasicChatMessage(viewModel, dispatch)
            ChatMessageType.TOPIC_UPDATED -> SystemMessage(
                icon = R.drawable.azure_communication_ui_chat_ic_topic_changed_filled, /* TODO: update icon */
                stringResource = R.string.azure_communication_ui_chat_topic_updated,
                substitution = listOf(viewModel.message.topic ?: "Unknown")

            )
            ChatMessageType.PARTICIPANT_ADDED -> SystemMessage(
                icon = R.drawable.azure_communication_ui_chat_ic_participant_added_filled,
                stringResource = R.string.azure_communication_ui_chat_joined_chat,
                substitution = viewModel.message.participants
            )
            ChatMessageType.PARTICIPANT_REMOVED -> if (viewModel.message.isCurrentUser)
                SystemMessage(
                    icon = R.drawable.azure_communication_ui_chat_ic_participant_removed_filled,
                    stringResource = R.string.azure_communication_ui_chat_you_removed_from_chat,
                    substitution = emptyList()
                ) else
                SystemMessage(
                    icon = R.drawable.azure_communication_ui_chat_ic_participant_removed_filled,
                    stringResource = R.string.azure_communication_ui_chat_left_chat,
                    substitution = viewModel.message.participants
                )
            else -> {
                BasicText(
                    text = "${viewModel.message.content} !TYPE NOT DETECTED!" ?: "Empty"
                )
            }
        }
    }
}

@Composable
private fun SystemMessage(icon: Int, stringResource: Int, substitution: List<String>) {

    val text = if (substitution.isEmpty())
        LocalContext.current.getString(stringResource) else
        LocalContext.current.getString(stringResource, substitution.joinToString(", "))
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(ChatCompositeTheme.dimensions.systemMessagePadding)
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = text,
            tint = ChatCompositeTheme.colors.systemIconColor
        )
        BasicText(text = text, style = ChatCompositeTheme.typography.systemMessage)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun BasicChatMessage(viewModel: MessageViewModel, dispatch: Dispatch) {
    Box(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.align(alignment = if (viewModel.isLocalUser) Alignment.TopEnd else Alignment.TopStart)) {
            // Avatar Rail (Left Padding)
            Box(modifier = Modifier.width(ChatCompositeTheme.dimensions.messageAvatarRailWidth)) {
                // Display the Avatar
                if (viewModel.showUsername) {
                    AvatarView(
                        name = viewModel.message.senderDisplayName,
                        avatarSize = AvatarSize.SMALL,
                        modifier = Modifier
                            .align(alignment = Alignment.TopEnd)
                            .padding(ChatCompositeTheme.dimensions.messageAvatarPadding)
                    )
                }
            }

            Box(modifier = Modifier.weight(1.0f)) {
                Box(
                    Modifier
                        .background(
                            color = when (viewModel.isLocalUser) {
                                true -> ChatCompositeTheme.colors.messageBackgroundSelf
                                false -> ChatCompositeTheme.colors.messageBackground
                            },
                            shape = ChatCompositeTheme.shapes.messageBubble,
                        )
                        .align(alignment = if (viewModel.isLocalUser) Alignment.TopEnd else Alignment.TopStart)
                    /* TODO: Add this block back in to add Context Menu Code
                    .combinedClickable(onLongClick = {
                        dispatch(ChatAction.ShowMessageContextMenu(viewModel.message))
                    }, onClick = {})
                    */
                ) {
                    messageContent(viewModel)
                }
            }

            Box(
                modifier = Modifier
                    .width(ChatCompositeTheme.dimensions.messageReceiptRailWidth)
                    .align(alignment = Alignment.Bottom)
            ) {
                // Display the Read Receipt
                androidx.compose.animation.AnimatedVisibility(visible = viewModel.isRead) {
                    Icon(
                        painter =
                        painterResource(
                            id =
                            R.drawable.azure_communication_ui_chat_ic_fluent_message_read_10_filled
                        ),
                        contentDescription = "Message Read",
                        tint = ChatCompositeTheme.colors.unreadMessageIndicatorBackground,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun messageContent(viewModel: MessageViewModel) {
    Box(
        modifier = Modifier.padding(ChatCompositeTheme.dimensions.messageInnerPadding)
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
                            viewModel.message.createdOn
                                ?.atZoneSameInstant(ZoneId.systemDefault())
                                ?.format(timeFormat)
                                ?: "Unknown Time",
                            style = ChatCompositeTheme.typography.messageHeaderDate,
                            modifier = Modifier.testTag(UITestTags.MESSAGE_TIME_CONTENT)
                        )
                    }
                }
            }
            if (viewModel.message.messageType == ChatMessageType.HTML) {
                HtmlText(html = viewModel.message.content ?: "Empty")
            } else {
                BasicText(
                    text = viewModel.message.content ?: "Empty",
                    modifier = Modifier.testTag(UITestTags.MESSAGE_BASIC_CONTENT)
                )
            }
        }
    }
}

@Composable
fun HtmlText(html: String, modifier: Modifier = Modifier) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
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
    AndroidThreeTen.init(LocalContext.current)
    Column(
        modifier = Modifier
            .width(500.dp)
            .background(color = ChatCompositeTheme.colors.background)
    ) {
        val vms = MOCK_MESSAGES.toViewModelList(
            LocalContext.current,
            MOCK_LOCAL_USER_ID,
            OffsetDateTime.now()
        )
        for (a in 0 until vms.size) {
            MessageView(vms[a]) { }
        }
    }
}
