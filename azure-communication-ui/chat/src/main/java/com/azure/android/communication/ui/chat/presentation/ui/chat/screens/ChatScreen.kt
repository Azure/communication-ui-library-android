// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.presentation.ui.chat.screens

import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.azure.android.communication.ui.chat.presentation.style.ChatCompositeUITheme
import com.azure.android.communication.ui.chat.presentation.ui.viewmodel.ChatScreenViewModel

@Composable
internal fun ChatScreen(viewModel: ChatScreenViewModel) {
    BasicText(
        text = "Hello Chat! ${viewModel.messages.size} ${viewModel.state} builds: ${viewModel.buildCount}",
    )
}

@Preview
@Composable
fun ChatScreenPreview() {
    ChatCompositeUITheme {
        ChatScreen(viewModel = ChatScreenViewModel(listOf(), "state", 2))
    }
}
