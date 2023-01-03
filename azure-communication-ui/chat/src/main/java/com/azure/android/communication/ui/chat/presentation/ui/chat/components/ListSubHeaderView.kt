/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License.
 */

package com.azure.android.communication.ui.chat.presentation.ui.chat.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView

@Composable
internal fun ListSubHeaderView(
    text: String,
    modifier: Modifier = Modifier,
) {
    AndroidView(modifier = modifier.fillMaxWidth(), factory = { context ->
        com.microsoft.fluentui.listitem.ListSubHeaderView(context).apply {
            title = text
        }
    })
}

@Preview
@Composable
internal fun PreviewListSubHeaderView() {
    ListSubHeaderView(text = "In this chat (4)")
}
