package com.azure.android.communication.ui.chat.presentation.ui.chat.chatviewcomponents

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.azure.android.communication.ui.chat.R

@Composable
internal fun AcsChatBackButton(
    contentDescription: String,
    onBackButtonPressed: () -> Unit = { }
) {
    Icon(
        painter = painterResource(id = R.drawable.azure_communication_ui_chat_ic_fluent_arrow_left_20_filled),
        contentDescription = null,
        modifier = Modifier
            .padding(19.dp)
            .size(24.dp)
            .clickable(
                onClick = onBackButtonPressed,
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(bounded = false)
            )
            .semantics {
                this.contentDescription = contentDescription
                this.role = Role.Image
            }
    )
}

@Composable
@Preview(showBackground = true)
fun PreviewAcsChatBackButton() {
    AcsChatBackButton(contentDescription = "Back Button")
}