// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.presentation.style

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp

@Immutable
data class ChatCompositeTypography(
    val title: TextStyle = TextStyle.Default,
    val body: TextStyle = TextStyle.Default,
    val messageHeader: TextStyle = TextStyle.Default.copy(
        fontSize = 12.sp
    ),

    val messageHeaderDate: TextStyle = TextStyle.Default.copy(
        fontSize = 12.sp,
        color = Color(0xFF6E6E6E)
    ),

    val messageBody: TextStyle = TextStyle.Default.copy(
        fontSize = 17.sp
    ),

    val systemMessage: TextStyle = TextStyle.Default.copy(
        fontSize = 11.sp,
        color = Color(0xFF6E6E6E)
    ),

    // Define additional custom typography styles as required by Figma
)

val LocalChatCompositeTypography = staticCompositionLocalOf {
    ChatCompositeTypography()
}
