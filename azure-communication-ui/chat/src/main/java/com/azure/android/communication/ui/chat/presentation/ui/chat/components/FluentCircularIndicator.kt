// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.presentation.ui.chat.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import com.azure.android.communication.ui.chat.R
import com.microsoft.fluentui.progress.ProgressBar

@Composable
internal fun FluentCircularIndicator() {
    AndroidView(factory = { context ->
        ProgressBar(context, null, 0, R.style.Widget_FluentUI_CircularProgress_Small)
    })
}

@Preview
@Composable
internal fun previewFluentProgressBar() {
    FluentCircularIndicator()
}
