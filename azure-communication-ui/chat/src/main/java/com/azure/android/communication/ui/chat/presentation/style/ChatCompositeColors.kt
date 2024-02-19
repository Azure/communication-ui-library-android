// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.presentation.style

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

internal open class ChatCompositeColors(
    val content: Color,
    val inverseContent: Color,
    val component: Color,
    val background: Color,
    val textColor: Color,
    val inverseTextColor: Color,
    val outlineColor: Color,
    val systemIconColor: Color,
    val messageBackground: Color,
    val messageBackgroundSelf: Color,
    val messageBackgroundSelfError: Color,
    val unreadMessageIndicatorBackground: Color,
) {
    object Dark : ChatCompositeColors(
        content = Color.White,
        inverseContent = Color.Black,
        component = Color.Gray,
        background = Color.Black,
        textColor = Color(0xFFE1E1E1),
        inverseTextColor = Color(0xFF212121),
        outlineColor = Color(0xFF292929),
        systemIconColor = Color(0xFF6E6E6E),
        messageBackground = Color(0xFF212121),
        messageBackgroundSelf = Color(0xFF043862),
        messageBackgroundSelfError = Color(0xCAA80000),
        unreadMessageIndicatorBackground = Color(0xFF0086F0),
    )

    object Light : ChatCompositeColors(
        content = Color.Black,
        inverseContent = Color.White,
        component = Color.Gray,
        background = Color.White,
        textColor = Color(0xFF212121),
        inverseTextColor = Color(0xFFE1E1E1),
        outlineColor = Color(0xFFE1E1E1),
        systemIconColor = Color(0xFF919191),
        messageBackground = Color(0xFFF1F1F1),
        messageBackgroundSelf = Color(0xFFDEECF9),
        messageBackgroundSelfError = Color(0xCAA80000),
        unreadMessageIndicatorBackground = Color(0xFF0078D4),
    )
}

internal val ChatCompositeColorPaletteLight =
    staticCompositionLocalOf {
        ChatCompositeColors.Light
    }

internal val ChatCompositeColorPaletteDark =
    staticCompositionLocalOf {
        ChatCompositeColors.Dark
    }
