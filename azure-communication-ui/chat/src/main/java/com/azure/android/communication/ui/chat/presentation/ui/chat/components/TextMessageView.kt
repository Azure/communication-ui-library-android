// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.presentation.ui.chat.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.azure.android.communication.ui.chat.models.MessageInfoModel
import com.azure.android.communication.ui.chat.presentation.style.ChatCompositeTheme
import com.azure.android.communication.ui.chat.service.sdk.wrapper.ChatMessageType
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.FormatStyle

@Composable
internal fun TextMessageView(
    message: MessageInfoModel,
    isGrouped: Boolean,
) {
    Column {
        Row {
            Column {
                Box(modifier = Modifier.padding(horizontal = 12.dp)) {
                    AvatarView(name = message.senderDisplayName, color = null)
                }
            }
            Column {
                if (!isGrouped) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (!message.isCurrentUser) {
                            Text(
                                text = message.senderDisplayName ?: "Unnamed participant",
                                style = MaterialTheme.typography.subtitle2,
                            )
                            Spacer(modifier = Modifier.padding(4.dp))
                        }
                        message.createdOn?.let {
                            Text(
                                text = it.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)),
                                style = MaterialTheme.typography.overline,
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                }
                Surface(
                    shape = MaterialTheme.shapes.medium,
                    elevation = 1.dp,
                    color = if (message.isCurrentUser) ChatCompositeTheme.colors.messageBackgroundSelf else ChatCompositeTheme.colors.messageBackground,
                ) {
                    Text(
                        text = message.content ?: "",
                        style = MaterialTheme.typography.body1,
                        modifier = Modifier.padding(4.dp),
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
internal fun PreviewTextMessageView() {
    TextMessageView(
        MessageInfoModel(
            id = "1",
            content = "Test Message",
            messageType = ChatMessageType.TEXT,
        ),
        isGrouped = false,
    )
}
