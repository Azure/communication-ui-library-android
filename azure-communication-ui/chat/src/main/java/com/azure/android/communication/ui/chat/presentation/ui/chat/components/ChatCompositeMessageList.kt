package com.azure.android.communication.ui.chat.presentation.ui.chat.components

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.azure.android.communication.ui.chat.models.MessageInfoModel
import com.azure.android.communication.ui.chat.presentation.ui.viewmodel.MessageViewModel
import com.azure.android.communication.ui.chat.presentation.ui.viewmodel.toViewModelList
import com.azure.android.communication.ui.chat.service.sdk.wrapper.ChatMessageType

@Composable
internal fun ChatCompositeMessageList(modifier: Modifier, messages: List<MessageViewModel>) {
    LazyColumn(modifier = modifier.fillMaxHeight()) {
        items(messages) { message ->
            BasicText(text = message.message.content ?: "Empty")
        }
    }
}

@Preview
@Composable
internal fun PreviewChatCompositeMessageList() {
    Column {
        ChatCompositeMessageList(
            modifier = Modifier.padding(0.dp),
            messages = listOf(
                MessageInfoModel(
                    messageType = ChatMessageType.TEXT,
                    content = "Test Message",
                    internalId = null,
                    id = null
                ),

                MessageInfoModel(
                    messageType = ChatMessageType.TEXT,
                    content = "Test Message 2 ",
                    internalId = null,
                    id = null
                ),

                MessageInfoModel(
                    messageType = ChatMessageType.TEXT,
                    content = "Test Message 3",
                    internalId = null,
                    id = null
                ),

                ).toViewModelList()
        )
    }
}
