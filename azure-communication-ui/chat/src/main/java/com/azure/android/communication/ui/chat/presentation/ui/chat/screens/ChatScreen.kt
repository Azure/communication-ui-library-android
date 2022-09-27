// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.presentation.ui.chat.screens

import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.azure.android.communication.ui.chat.presentation.style.ChatCompositeUITheme

@Composable
fun ChatScreen() {
    BasicText(
        text = "Hello Chat!",
    )
}

@Preview
@Composable
fun ChatScreenPreview() {
    ChatCompositeUITheme {
        ChatScreen()
    }
}
