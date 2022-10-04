package com.azure.android.communication.ui.chat.implementation.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.azure.android.communication.ui.chat.implementation.redux.states.MockMessage
import com.azure.android.communication.ui.chat.implementation.ui.mock.MessageFaker
import com.azure.android.communication.ui.chat.implementation.ui.screen.messagePadding

@Composable
fun AcsChatMessageView(msg: MockMessage, isGrouped: Boolean = false) {
    val offsetPadding = (LocalConfiguration.current.screenWidthDp * 0.1).dp
    fun Modifier.participantMessageView(): Modifier = this.padding(end = offsetPadding, start = messagePadding, top = messagePadding, bottom = messagePadding)
    fun Modifier.selfMessageView(): Modifier = this.padding(end = messagePadding, start = offsetPadding, top = messagePadding, bottom = messagePadding)
    Row(modifier = if (msg.mockParticipant.isCurrentUser) Modifier.selfMessageView() else Modifier.participantMessageView()) {
        Box(modifier = Modifier.padding(horizontal = 12.dp)) {
            AcsChatAvatar(name = msg.mockParticipant.displayName, color = null, image = msg.mockParticipant.drawableAvatar)
        }
        AcsChatMessageCard(msg = msg, isGrouped)
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewAcsChatMessageView() {
    Column {
        AcsChatMessageView(MessageFaker(LocalContext.current).generateMessages(1)[0], isGrouped = false)
        AcsChatMessageView(MessageFaker(LocalContext.current).generateMessages(1)[0], isGrouped = false)
    }
}
