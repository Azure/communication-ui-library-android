// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.presentation.style

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class ChatCompositeColors(
    val content: Color,
    val component: Color,
    val background: Color,
    val messageBackground: Color,
    val messageBackgroundSelf: Color,
)

val ChatCompositeColorPalette = staticCompositionLocalOf {
    ChatCompositeColors(
        content = Color.Black,
        component = Color.Gray,
        background = Color.White,
        messageBackground = Color(0xFFF1F1F1),
        messageBackgroundSelf = Color(0xFFDEECF9)
    )
}
