// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.presentation.ui.chat.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Composable
internal fun ChatCompositeActionBar() {
    TopAppBar {
        BasicText(text = "Top Bar")
    }
}

@Preview
@Composable
internal fun PreviewChatCompositeActionBar() {
    Column {
        ChatCompositeActionBar()
    }
}
