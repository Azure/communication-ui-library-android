// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.presentation.ui.chat.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
internal fun BottomBarView(postMessage: (String) -> Unit) {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {

        MessageInputView(contentDescription = "Message Input Field")

        SendMessageButtonView("Send Message Button") {
            postMessage("Test Message @ ${System.currentTimeMillis()}")
        }
    }
}

@Preview
@Composable
internal fun PreviewBottomBarView() {
    BottomBarView {}
}
