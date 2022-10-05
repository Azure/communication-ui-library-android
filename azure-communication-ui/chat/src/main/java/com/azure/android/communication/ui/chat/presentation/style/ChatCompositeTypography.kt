// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.presentation.style

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle

@Immutable
data class ChatCompositeTypography(
    val title: TextStyle,
    val body: TextStyle,
    // Define additional custom typography styles as required by Figma
)

val LocalChatCompositeTypography = staticCompositionLocalOf {
    ChatCompositeTypography(
        body = TextStyle.Default,
        title = TextStyle.Default,
    )
}
