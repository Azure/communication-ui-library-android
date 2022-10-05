// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.presentation.ui.chat.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.graphics.toColorInt

@Composable
internal fun ChatCompositeBottomBar() {
    Box(modifier = Modifier.fillMaxWidth()) {
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {

            MessageInputView(
                textColor = Color("#212121".toColorInt()),
                outlineColor = Color("#E1E1E1".toColorInt()),
                contentDescription = "Message Input Field"
            )

            AcsChatSendMessageButton(contentDescription = "Send Message Button")
        }
    }
}

@Preview
@Composable
internal fun PreviewChatCompositeBottomBar() {
    Column {
        ChatCompositeBottomBar()
    }
}
