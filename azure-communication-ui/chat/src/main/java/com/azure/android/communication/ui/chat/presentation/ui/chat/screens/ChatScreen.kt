// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.presentation.ui.chat.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.ClickableText
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import com.azure.android.communication.ui.chat.presentation.style.ChatCompositeUITheme
import com.azure.android.communication.ui.chat.presentation.ui.viewmodel.ChatScreenViewModel

@Composable
internal fun ChatScreen(viewModel: ChatScreenViewModel) {
    Column() {
        BasicText(
            text = "Messages: ${viewModel.messages.size} ${viewModel.state} builds: ${viewModel.buildCount}",
        )
        ClickableText(
            text = AnnotatedString("Click me for random message"),
            onClick = {

                viewModel.postMessage("Random Message @ ${System.currentTimeMillis()}")
            }

        )
    }
}

@Preview
@Composable
internal fun ChatScreenPreview() {
    ChatCompositeUITheme {
        ChatScreen(
            viewModel = ChatScreenViewModel(
                listOf(),
                state = "state",
                buildCount = 2,
                postMessage = {}
            )
        )
    }
}
