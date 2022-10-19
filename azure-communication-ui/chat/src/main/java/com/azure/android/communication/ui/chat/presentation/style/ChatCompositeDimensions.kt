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
    val messagePadding: PaddingValues = PaddingValues(start = 10.dp, end = 10.dp, top = 8.dp, bottom = 8.dp)
)

internal val LocalChatCompositeDimensions = staticCompositionLocalOf {
    ChatCompositeDimensions()
}
