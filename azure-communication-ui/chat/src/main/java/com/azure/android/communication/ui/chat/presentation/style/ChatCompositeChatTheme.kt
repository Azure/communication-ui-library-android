// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.presentation.style

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import com.microsoft.fluentui.theme.FluentTheme
import com.microsoft.fluentui.theme.ThemeMode
import com.microsoft.fluentui.theme.token.AliasTokens

@Immutable
data class AcsChatTypography(
    val title: TextStyle,
    val body: TextStyle,
)

val LocalCustomTypography = staticCompositionLocalOf {
    // Define additional custom typography styles as required by Figma
    AcsChatTypography(
        body = TextStyle.Default,
        title = TextStyle.Default,
    )
}

@Composable
internal fun ChatCompositeUITheme(
    themeMode: ThemeMode = ThemeMode.Auto,
    content: @Composable () -> Unit,
) {
    val fluentTypography = FluentTheme.aliasTokens.typography
    val customTypography = AcsChatTypography(
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
    CompositionLocalProvider(
        LocalCustomTypography provides customTypography,
    ) {
        FluentTheme(
            themeMode = themeMode,
            content = content
        )
    }
}

// Usage: ChatCompositeUITheme.typography.body
object ChatCompositeUITheme {

    val typography: AcsChatTypography
        @Composable
        get() = LocalCustomTypography.current
}
