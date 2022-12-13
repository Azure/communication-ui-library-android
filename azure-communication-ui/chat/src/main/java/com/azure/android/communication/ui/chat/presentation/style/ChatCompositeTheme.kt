// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.presentation.style

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.microsoft.fluentui.theme.FluentTheme
import com.microsoft.fluentui.theme.ThemeMode
import com.microsoft.fluentui.theme.token.AliasTokens

val LocalThemeMode = staticCompositionLocalOf {
    ThemeMode.Auto
}

@Composable
internal fun ChatCompositeTheme(
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

    val acsChatShapes = ChatCompositeShapes(
        messageBubble = RoundedCornerShape(4.dp),
        unreadMessagesIndicator = RoundedCornerShape(100.dp)
    )

    CompositionLocalProvider(
        LocalChatCompositeTypography provides customTypography,
        LocalChatCompositeShapes provides acsChatShapes,
        LocalThemeMode provides themeMode,
    ) {
        FluentTheme(
            themeMode = themeMode,
            content = content,

        )
    }
}

// Usage: ChatCompositeTheme.typography.body
internal object ChatCompositeTheme {
    val dimensions: ChatCompositeDimensions
        @Composable
        get() = LocalChatCompositeDimensions.current

    val typography: ChatCompositeTypography
        @Composable
        get() = LocalChatCompositeTypography.current

    val colors: ChatCompositeColors
        @Composable
        get() = if (isSystemInDarkTheme() || LocalThemeMode.current == ThemeMode.Dark) ChatCompositeColorPaletteDark.current else ChatCompositeColorPaletteLight.current

    val shapes: ChatCompositeShapes
        @Composable
        get() = LocalChatCompositeShapes.current
}
