// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.presentation.ui.chat.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.azure.android.communication.ui.chat.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

@Composable
internal fun ChatCompositeSendMessageButton(
    contentDescription: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    val semantics = Modifier.semantics {
        this.contentDescription = contentDescription
        this.role = Role.Image
    }
    val painter = painterResource(id = R.drawable.azure_communication_ui_chat_ic_fluent_send_message_button_20_filled)
    Box(modifier = Modifier.clickable { onClick } ) {
        Image(
            painter = painter,
            contentDescription = contentDescription,
            contentScale = ContentScale.Crop,
            modifier = modifier
                .padding(8.dp)
                .size(19.5.dp, 19.5.dp)

                .then(semantics)
        )

    }
}

@Composable
@Preview(showBackground = true)
fun PreviewChatCompositeSendMessageButton() {
    ChatCompositeSendMessageButton(contentDescription = "Send Message Button")
}
