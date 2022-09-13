// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.azure.android.communication.ui.chat.R
import com.azure.android.communication.ui.chat.presentation.components.microcomponents.ChatCompositeActionBarBackButton

@Composable
fun ChatCompsiteActionBar(
    onNavIconPressed: () -> Unit = { }
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        Row() {
            ChatCompositeActionBarBackButton(
                contentDescription = "Back button"
            )
            Column() {
                BasicText(text = stringResource(id = R.string.azure_communication_ui_chat_action_bar_title))
                BasicText(text = "2 participants")
            }
        }
    }
}

@Preview
@Composable
fun PreviewChatCompsiteActionBar() {
    Column() {
        ChatCompsiteActionBar(
            onNavIconPressed = {},
        )

        ChatCompsiteActionBar(
            onNavIconPressed = {},
        )
    }
}
