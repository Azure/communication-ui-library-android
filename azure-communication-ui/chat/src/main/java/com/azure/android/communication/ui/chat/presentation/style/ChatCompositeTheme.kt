// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.presentation.style

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import com.microsoft.fluentui.theme.FluentTheme
import com.microsoft.fluentui.theme.ThemeMode

internal val LocalThemeMode =
    staticCompositionLocalOf {
        ThemeMode.Auto
    }

@Composable
internal fun ChatCompositeTheme(
    themeMode: ThemeMode = ThemeMode.Auto,
    content: @Composable () -> Unit,
) = CompositionLocalProvider(LocalThemeMode provides themeMode) {
    CompositionLocalProvider(LocalChatCompositeTypography provides ChatCompositeTypography.buildDefault()) {
        // Needs the 2 composition local providers because the second (typography) requires the first to be in scope
        // I.e. ChatCompositeTheme.colors returns dark/light based on LocalThemeMode
        //      LocalChatCompositeTypography uses ChatCompositeTheme.colors, so it needs LocalThemeMode
        //      to call buildDefault()
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
        get() {
            return if (LocalThemeMode.current == ThemeMode.Auto) {
                if (isSystemInDarkTheme()) {
                    ChatCompositeColorPaletteDark.current
                } else {
                    ChatCompositeColorPaletteLight.current
                }
            } else {
                if (LocalThemeMode.current == ThemeMode.Dark) {
                    ChatCompositeColorPaletteDark.current
                } else {
                    ChatCompositeColorPaletteLight.current
                }
            }
        }

    val shapes: ChatCompositeShapes
        @Composable
        get() = LocalChatCompositeShapes.current
}
