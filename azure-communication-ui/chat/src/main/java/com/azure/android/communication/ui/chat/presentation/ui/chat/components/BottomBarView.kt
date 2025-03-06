// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.presentation.ui.chat.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.azure.android.communication.ui.chat.R
import com.azure.android.communication.ui.chat.models.MessageInfoModel
import com.azure.android.communication.ui.chat.models.MessageSendStatus
import com.azure.android.communication.ui.chat.redux.action.Action
import com.azure.android.communication.ui.chat.redux.action.ChatAction
import com.azure.android.communication.ui.chat.service.sdk.wrapper.ChatMessageType
import org.threeten.bp.OffsetDateTime

@Composable
internal fun BottomBarView(
    messageInputTextState: MutableState<String>,
    sendMessageEnabled: Boolean = true,
    postAction: (Action) -> Unit,
    inputAutoFocus: Boolean = false,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {

        MessageInputView(
            contentDescription = stringResource(R.string.azure_communication_ui_chat_message_input_view_content_description),
            messageInputTextState = messageInputTextState,
            postAction = postAction,
            sendMessageEnabled = sendMessageEnabled,
            autoFocus = inputAutoFocus
        )

        SendMessageButtonView(
            contentDescription = stringResource(
                R.string.azure_communication_ui_chat_message_send_button_content_description,
                messageInputTextState.value
            ),
            enabled = sendMessageEnabled && messageInputTextState.value.isNotBlank()
        ) {
            sendButtonOnclick(postAction, messageInputTextState)
        }
    }
}

private fun sendButtonOnclick(
    postAction: (Action) -> Unit,
    messageInputTextState: MutableState<String>,
) {
    postAction(
        ChatAction.SendMessage(
            MessageInfoModel(
                id = null,
                internalId = System.currentTimeMillis().toString(),
                messageType = ChatMessageType.TEXT,
                createdOn = OffsetDateTime.now(),
                sendStatus = MessageSendStatus.SENDING,
                content = messageInputTextState.value.trim(),
                isCurrentUser = true,
            )
        )
    )
    messageInputTextState.value = ""
}

@Preview
@Composable
internal fun PreviewBottomBarView() {
    BottomBarView(remember { mutableStateOf("") }, postAction = {}) 
}
