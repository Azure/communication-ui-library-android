// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.presentation.ui.chat.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
internal fun ChatCompositeBottomBar(postMessage: (String) -> Unit) {
    Row(
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        TextField(value = "", onValueChange = {}, Modifier.weight(1f))
        ChatCompositeSendMessageButton("Send Message Button") {
            postMessage("Test Message @ ${System.currentTimeMillis()}")
        }
    }
}

@Preview
@Composable
internal fun PreviewChatCompositeBottomBar() {
    Column {
        ChatCompositeBottomBar {}
    }
}
