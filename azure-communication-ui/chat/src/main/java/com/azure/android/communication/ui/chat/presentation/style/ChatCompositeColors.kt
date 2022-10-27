// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.presentation.style

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
internal data class ChatCompositeColors(
    val content: Color = Color.Black,
    val component: Color = Color.Gray,
    val background: Color = Color.White,
    val textColor: Color = Color(0xFF212121),
    val outlineColor: Color = Color(0xFFE1E1E1),
    val messageBackground: Color = Color(0xFFF1F1F1),
    val systemIconColor: Color = Color(0xFF919191),
    val messageBackgroundSelf: Color = Color(0xFFDEECF9),
    val unreadMessageIndicatorBackground: Color = Color(0xFF0078D4)
)
internal val ChatCompositeColorPalette = staticCompositionLocalOf {
    ChatCompositeColors()
}
