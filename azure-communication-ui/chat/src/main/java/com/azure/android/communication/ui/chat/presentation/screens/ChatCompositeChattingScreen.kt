// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.presentation.screens

import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import com.azure.android.communication.ui.chat.presentation.theme.ChatCompositeUITheme

@Composable
fun ChattingScreen() {
    ChatCompositeUITheme {
        BasicText(
            text = "Hello Chat!",
        )
    }
}
