// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.presentation.style

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.microsoft.fluentui.theme.FluentTheme
import com.microsoft.fluentui.theme.token.AliasTokens

@Immutable
internal data class ChatCompositeTypography(
    val title: TextStyle,
    val body: TextStyle,
    val messageHeader: TextStyle,
    val messageHeaderDate: TextStyle,
    val messageBody: TextStyle,
    val systemMessage: TextStyle,
    val unreadMessageText: TextStyle,
    // Define additional custom typography styles as required by Figma
) {
    companion object {
        // Construct this in the current composition (using defaults from Fluent)
        @Composable
        fun buildDefault(): ChatCompositeTypography {
            val fluentTypography = FluentTheme.aliasTokens.typography

            return ChatCompositeTypography(
                title =
                    TextStyle(
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = fluentTypography[AliasTokens.TypographyTokens.Title1].weight,
                        fontSize = fluentTypography[AliasTokens.TypographyTokens.Title1].fontSize.size,
                        color = ChatCompositeTheme.colors.textColor,
                    ),
                body =
                    TextStyle(
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = fluentTypography[AliasTokens.TypographyTokens.Body1].weight,
                        fontSize = fluentTypography[AliasTokens.TypographyTokens.Body1].fontSize.size,
                        color = ChatCompositeTheme.colors.textColor,
                    ),
                messageHeader =
                    TextStyle.Default.copy(
                        fontSize = 12.sp,
                        color = ChatCompositeTheme.colors.textColor,
                    ),
                messageHeaderDate =
                    TextStyle.Default.copy(
                        fontSize = 12.sp,
                        color = Color(0xFF6E6E6E),
                    ),
                messageBody =
                    TextStyle.Default.copy(
                        fontSize = 17.sp,
                    ),
                systemMessage =
                    TextStyle.Default.copy(
                        fontSize = 11.sp,
                        color = Color(0xFF6E6E6E),
                    ),
                unreadMessageText =
                    TextStyle.Default.copy(
                        fontSize = 16.sp,
                        color = ChatCompositeTheme.colors.inverseTextColor,
                        fontWeight = FontWeight.W500,
                    ),
            )
        }
    }
}

internal val LocalChatCompositeTypography =
    staticCompositionLocalOf {
        ChatCompositeTypography(
            title = TextStyle.Default,
            body = TextStyle.Default,
            messageHeader = TextStyle.Default.copy(fontSize = 12.sp),
            messageHeaderDate =
                TextStyle.Default.copy(
                    fontSize = 12.sp,
                    color = Color(0xFF6E6E6E),
                ),
            messageBody =
                TextStyle.Default.copy(
                    fontSize = 17.sp,
                ),
            systemMessage =
                TextStyle.Default.copy(
                    fontSize = 11.sp,
                    color = Color(0xFF6E6E6E),
                ),
            unreadMessageText = TextStyle.Default.copy(fontSize = 14.sp),
        )
    }
