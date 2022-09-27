// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.presentation.theme

import androidx.compose.runtime.Composable
import com.microsoft.fluentui.theme.FluentTheme
import com.microsoft.fluentui.theme.ThemeMode

@Composable
fun ChatCompositeUITheme(
    themeMode: ThemeMode = ThemeMode.Auto,
    content: @Composable () -> Unit,
) {

    FluentTheme(
        themeMode = themeMode,
        content = content
    )
}
