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
import com.azure.android.communication.ui.chat.redux.action.Action
import com.azure.android.communication.ui.chat.redux.action.ChatAction
import com.azure.android.communication.ui.chat.redux.state.ChatStatus
import com.azure.android.communication.ui.chat.service.sdk.wrapper.ChatMessageType

@Composable
internal fun BottomBarView(
    messageInputTextState: MutableState<String>,
    chatStatus: ChatStatus,
    postAction: (Action) -> Unit,
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
        )

        SendMessageButtonView(
            contentDescription = stringResource(
                R.string.azure_communication_ui_chat_message_send_button_content_description,
                messageInputTextState.value
            ),
            chatStatus = chatStatus,
            clickable = messageInputTextState.value.isNotBlank()
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
                messageType = ChatMessageType.TEXT,
                internalId = null,
                content = messageInputTextState.value,
                isCurrentUser = true
            )
        )
    )
    messageInputTextState.value = ""
}

@Preview
@Composable
internal fun PreviewBottomBarView() {
    BottomBarView(remember { mutableStateOf("") }, ChatStatus.INITIALIZED) {}
}
