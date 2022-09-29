// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.presentation.ui.chat.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.azure.android.communication.ui.chat.presentation.theme.ChatCompositeUITheme
import com.azure.android.communication.ui.chat.presentation.ui.chat.chatviewcomponents.AcsChatActionBarViewModel
import com.azure.android.communication.ui.chat.presentation.ui.chat.chatviewcomponents.ChatCompositeActionBar

@Composable
fun ChattingScreen() {
    ChatCompositeUITheme {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()

            ) {
                ChatCompositeActionBar(
                    viewModel = AcsChatActionBarViewModel(4) {}
                )
                BasicText(
                    text = "Hello Chat!",
                )
            }
        }

    }
}


