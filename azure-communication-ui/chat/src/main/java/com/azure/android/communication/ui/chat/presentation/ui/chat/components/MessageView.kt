// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.presentation.ui.chat.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.azure.android.communication.ui.chat.models.MessageInfoModel
import com.azure.android.communication.ui.chat.presentation.ui.viewmodel.MessageViewModel
import com.azure.android.communication.ui.chat.service.sdk.wrapper.ChatMessageType

@Composable
internal fun MessageView(viewModel: MessageViewModel, isGrouped: Boolean = false) {
    val offsetPadding = (LocalConfiguration.current.screenWidthDp * 0.1).dp

    val messagePadding: Dp = 6.dp
    fun Modifier.participantMessageView(): Modifier = this.padding(end = offsetPadding, start = messagePadding, top = messagePadding, bottom = messagePadding)
    fun Modifier.selfMessageView(): Modifier = this.padding(end = messagePadding, start = offsetPadding, top = messagePadding, bottom = messagePadding)

    Row(modifier = if (viewModel.message.isCurrentUser) Modifier.selfMessageView() else Modifier.participantMessageView()) {
        TextMessageView(message = viewModel.message, isGrouped)
    }
}

@Preview(showBackground = true)
@Composable
internal fun PreviewMessageView() {
    MessageView(
        MessageViewModel(
            MessageInfoModel(
                messageType = ChatMessageType.TEXT,
                content = "Test Message",
                internalId = null,
                id = null
            )
        ),
        isGrouped = false
    )
}
