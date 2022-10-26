// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.presentation.style

import androidx.annotation.Dimension
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
internal data class ChatCompositeDimensions(
    @Dimension
    val messageBubbleLeftSpacing: Dp = 48.dp,
    val messageAvatarSize: Dp = 24.dp,
    val messageUsernamePaddingEnd: Dp = 8.dp,
    val messagePadding: PaddingValues = PaddingValues(start = 10.dp, end = 10.dp, top = 8.dp, bottom = 8.dp),
    val systemMessagePadding: PaddingValues = PaddingValues(start = 20.dp, end = 5.dp, top = 10.dp, bottom = 10.dp),
    val typingIndicatorAreaHeight: Dp = 36.dp,
    val unreadMessagesIndicatorHeight: Dp = 48.dp,
    val unreadMessagesIndicatorIconHeight: Dp = 18.dp
    val dateHeaderPadding: PaddingValues = PaddingValues(start = 0.dp, end = 0.dp, top = 16.dp, bottom = 0.dp)
)

internal val LocalChatCompositeDimensions = staticCompositionLocalOf {
    ChatCompositeDimensions()
}
