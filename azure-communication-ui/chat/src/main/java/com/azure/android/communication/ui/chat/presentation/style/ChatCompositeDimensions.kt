// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.presentation.style

import androidx.annotation.Dimension
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Immutable
internal data class ChatCompositeDimensions(
    // Left Rail where Avatar is
    @Dimension
    val messageAvatarRailWidth: Dp = 32.dp,
    val messageReceiptRailWidth: Dp = 20.dp,
    val messageUsernamePaddingEnd: Dp = 8.dp,
    val messageListMaxWidth: Dp = 560.dp,
    val messageOuterPadding: PaddingValues =
        PaddingValues(
            start = 0.dp,
            end = 0.dp,
            top = 1.dp,
            bottom = 1.dp,
        ),
    val messageInnerPadding: PaddingValues =
        PaddingValues(
            start = 8.dp,
            end = 8.dp,
            top = 8.dp,
            bottom = 8.dp,
        ),
    val systemMessagePadding: PaddingValues =
        PaddingValues(
            start = 20.dp,
            end = 5.dp,
            top = 8.dp,
            bottom = 8.dp,
        ),
    val typingIndicatorAreaHeight: Dp = 36.dp,
    val unreadMessagesIndicatorHeight: Dp = 48.dp,
    val unreadMessagesIndicatorIconHeight: Dp = 18.dp,
    val unreadMessagesIndicatorPadding: PaddingValues =
        PaddingValues(
            start = 0.dp,
            end = 0.dp,
            top = 0.dp,
            bottom = 4.dp,
        ),
    val unreadMessagesIndicatorIconPadding: PaddingValues =
        PaddingValues(
            start = 10.dp,
            end = 0.dp,
            top = 0.dp,
            bottom = 0.dp,
        ),
    val unreadMessagesIndicatorTextFontSize: TextUnit = 16.sp,
    val dateHeaderPadding: PaddingValues =
        PaddingValues(
            start = 0.dp,
            end = 0.dp,
            top = 12.dp,
            bottom = 4.dp,
        ),
    val messageAvatarPadding: PaddingValues =
        PaddingValues(
            start = 0.dp,
            end = 4.dp,
            top = 0.dp,
            bottom = 0.dp,
        ),
    val messageRead: PaddingValues =
        PaddingValues(
            start = 3.dp,
            end = 4.99.dp,
            top = 3.dp,
            bottom = 3.dp,
        ),
)

internal val LocalChatCompositeDimensions =
    staticCompositionLocalOf {
        ChatCompositeDimensions()
    }
