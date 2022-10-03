// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.presentation.ui.chat.screens

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.ClickableText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import com.azure.android.communication.ui.chat.R
import com.azure.android.communication.ui.chat.presentation.style.ChatCompositeUITheme
import com.azure.android.communication.ui.chat.presentation.ui.chat.components.AcsChatActionBarViewModel
import com.azure.android.communication.ui.chat.presentation.ui.chat.components.ChatCompositeActionBar
import com.azure.android.communication.ui.chat.presentation.ui.viewmodel.ChatScreenViewModel

@Composable
internal fun ChatScreen(viewModel: ChatScreenViewModel) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()

        ) {
            val dispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
            ChatCompositeActionBar(
                AcsChatActionBarViewModel(
                    participantCount = 4,
                    topic = stringResource(R.string.azure_communication_ui_chat_chat_action_bar_title)
                )
            ) {
                dispatcher?.onBackPressed()
            }
            BasicText(
                text = "Hello Chat! ${viewModel.messages.size} ${viewModel.state} builds: ${viewModel.buildCount}",
            )
            ClickableText(
                text = AnnotatedString("Click me for random message"),
                onClick = {

                    viewModel.postMessage("Random Message @ ${System.currentTimeMillis()}")
                }

            )
        }
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
