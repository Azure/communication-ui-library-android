// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.presentation.style

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.microsoft.fluentui.theme.FluentTheme
import com.microsoft.fluentui.theme.ThemeMode
import com.microsoft.fluentui.theme.token.AliasTokens

@Composable
internal fun ChatCompositeTheme(
    primaryColor: Int = 0xFFFFFFFF.toInt(),
    themeMode: ThemeMode = ThemeMode.Auto,
    content: @Composable () -> Unit,
) {
    val fluentTypography = FluentTheme.aliasTokens.typography
    val customTypography = ChatCompositeTypography(
        body = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = fluentTypography[AliasTokens.TypographyTokens.Body1].weight,
            fontSize = fluentTypography[AliasTokens.TypographyTokens.Body1].fontSize.size,
        ),
        title = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = fluentTypography[AliasTokens.TypographyTokens.Title1].weight,
            fontSize = fluentTypography[AliasTokens.TypographyTokens.Title1].fontSize.size
        )
    )
    // TODO: determine which colors to use from FluentTheme before adding them to CompositionLocalProvider
    val acsChatColors = ChatCompositeColors(
        content = Color(0xFFDD0D3C),
        component = Color(0xFFC20029),
        background = Color.White,
        messageBackgroundSelf = Color(0xFFDEECF9),
        messageBackground = Color(primaryColor),
    )
    val acsChatShapes = ChatCompositeShapes(
        messageBubble = RoundedCornerShape(4.dp),
    )

    CompositionLocalProvider(
        LocalChatCompositeTypography provides customTypography,
        LocalChatCompositeShapes provides acsChatShapes
    ) {
        FluentTheme(
            themeMode = themeMode,
            content = content
        )
    }
}

// Usage: ChatCompositeTheme.typography.body
object ChatCompositeTheme {

    val typography: ChatCompositeTypography
        @Composable
        get() = LocalChatCompositeTypography.current
    val colors: ChatCompositeColors
        @Composable
        get() = ChatCompositeColorPalette.current
    val shapes: ChatCompositeShapes
        @Composable
        get() = LocalChatCompositeShapes.current
}
