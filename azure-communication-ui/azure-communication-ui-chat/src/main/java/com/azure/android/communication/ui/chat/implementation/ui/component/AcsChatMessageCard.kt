package com.azure.android.communication.ui.chat.implementation.ui.component

import android.annotation.SuppressLint
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.azure.android.communication.ui.chat.implementation.redux.states.MockMessage
import com.azure.android.communication.ui.chat.implementation.ui.mock.MessageFaker
import com.azure.android.communication.ui.chat.implementation.ui.theme.AzurecommunicationuiTheme
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@SuppressLint("NewApi")
@Composable
fun AcsChatMessageCard(msg: MockMessage, isGrouped: Boolean) {
    val participantMessageBackground = Color(0xFFF1F1F1)
    val selfMessageBackground = Color(0xFFDEECF9)
    AzurecommunicationuiTheme() {
        val timeFormatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)

        Column {
            if (!isGrouped) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (!msg.mockParticipant.isCurrentUser) {
                        Text(
                            text = msg.mockParticipant.displayName,
                            style = MaterialTheme.typography.subtitle2
                        )
                        Spacer(modifier = Modifier.padding(4.dp))
                    }
                    Text(
                        text = timeFormatter.format(msg.receivedAt),
                        style = MaterialTheme.typography.overline,
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
            }
            Surface(
                shape = MaterialTheme.shapes.medium,
                elevation = 1.dp,
                color = if (msg.mockParticipant.isCurrentUser) selfMessageBackground else participantMessageBackground
            ) {
                Text(
                    text = msg.message,
                    style = MaterialTheme.typography.body1,
                    modifier = Modifier.padding(4.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewAcsChatMessageCard() {
    Column {
        AcsChatMessageCard(MessageFaker(LocalContext.current).generateMessages(1)[0], isGrouped = false)
        AcsChatMessageCard(MessageFaker(LocalContext.current).generateMessages(1)[0], isGrouped = false)
    }
}
