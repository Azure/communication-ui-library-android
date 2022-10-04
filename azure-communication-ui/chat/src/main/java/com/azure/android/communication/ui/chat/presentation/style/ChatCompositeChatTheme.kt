// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.presentation.style

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.microsoft.fluentui.theme.FluentTheme
import com.microsoft.fluentui.theme.ThemeMode
import com.microsoft.fluentui.theme.token.AliasTokens

@Immutable
data class AcsChatColors(
    val content: Color,
    val component: Color,
    val background: Color,
    val messageBackground: Color,
    val messageBackgroundSelf: Color,
)

@Immutable
data class AcsChatTypography(
    val title: TextStyle,
    val body: TextStyle,
    // Define additional custom typography styles as required by Figma
)

@Immutable
data class AcsChatShapes(
    val messageBubble: Shape
)

val LocalAcsChatColors = staticCompositionLocalOf {
    AcsChatColors(
        content = Color.Black,
        component = Color.Gray,
        background = Color.White,
        messageBackground = Color(0xFFF1F1F1),
        messageBackgroundSelf = Color(0xFFDEECF9)
    )
}

val LocalCustomTypography = staticCompositionLocalOf {
    AcsChatTypography(
        body = TextStyle.Default,
        title = TextStyle.Default,
    )
}

val LocalCustomShapes = staticCompositionLocalOf {
    AcsChatShapes(
        messageBubble = RoundedCornerShape(4.dp),
    )
}

@Composable
internal fun ChatCompositeTheme(
    primaryColor: Int = 0xFFFFFFFF.toInt(),
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
    // TODO: determine which colors to use from FluentTheme before adding them to CompositionLocalProvider
    val acsChatColors = AcsChatColors(
        content = Color(0xFFDD0D3C),
        component = Color(0xFFC20029),
        background = Color.White,
        messageBackgroundSelf = Color(0xFFDEECF9),
        messageBackground = Color(primaryColor),
    )
    val acsChatShapes = AcsChatShapes(
        messageBubble = RoundedCornerShape(4.dp),
    )

    CompositionLocalProvider(
        LocalCustomTypography provides customTypography,
        LocalCustomShapes provides acsChatShapes
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
    val colors: AcsChatColors
        @Composable
        get() = LocalAcsChatColors.current
    val shapes: AcsChatShapes
        @Composable
        get() = LocalCustomShapes.current
}
