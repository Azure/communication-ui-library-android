package com.azure.android.communication.ui.chat.implementation.ui.component

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import com.azure.android.communication.ui.chat.R

data class AcsChatUnreadMessagesIndicatorViewModel(
    val count: Int
)
/**
 * Shows a button that lets the user scroll to the bottom.
 */
@Composable
fun AcsChatUnreadMessagesIndicator(
    unreadCount: Int,
    onClicked: () -> Unit
) {
    ExtendedFloatingActionButton(
        icon = {
            Icon(
                painterResource(id = R.drawable.azure_communication_ui_chat_ic_arrow_down_16_filled),
                modifier = Modifier.height(18.dp),
                contentDescription = null
            )
        },
        text = {
            Text(text = "$unreadCount new messages")
        },
        onClick = onClicked,
        containerColor = Color("#0078D4".toColorInt()),
        contentColor = Color("#FFFFFF".toColorInt()),
        modifier = Modifier
            .height(48.dp)
            .clip(RoundedCornerShape(100.dp))
    )
}

@Preview
@Composable
fun AcsChatJumpToBottomPreview() {
    AcsChatUnreadMessagesIndicator(20) {}
}
