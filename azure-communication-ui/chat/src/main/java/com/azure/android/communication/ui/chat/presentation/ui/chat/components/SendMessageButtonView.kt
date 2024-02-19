// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.presentation.ui.chat.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.azure.android.communication.ui.chat.R
import com.azure.android.communication.ui.chat.presentation.ui.chat.UITestTags

@Composable
internal fun SendMessageButtonView(
    contentDescription: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = false,
    onClick: () -> Unit = {},
) {
    val semantics =
        Modifier.semantics {
            this.contentDescription = contentDescription
            this.role = Role.Image
        }
    val painter =
        if (enabled) {
            painterResource(id = R.drawable.azure_communication_ui_chat_ic_fluent_send_message_button_20_filled_enabled)
        } else {
            painterResource(id = R.drawable.azure_communication_ui_chat_ic_fluent_send_message_button_20_filled_disabled)
        }
    Box(
        modifier =
            Modifier.testTag(UITestTags.MESSAGE_SEND_BUTTON).clickable {
                if (enabled) {
                    onClick()
                }
            },
    ) {
        Image(
            painter = painter,
            contentDescription = contentDescription,
            contentScale = ContentScale.Crop,
            modifier =
                modifier
                    .size(19.5.dp, 19.5.dp)
                    .then(semantics),
        )
    }
}

@Composable
@Preview(showBackground = true)
internal fun PreviewSendMessageButtonView() {
    SendMessageButtonView(
        contentDescription = "Send Message Button",
    )
}
