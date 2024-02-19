// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.presentation.style

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

@Immutable
internal data class ChatCompositeShapes(
    val messageBubble: Shape,
    val unreadMessagesIndicator: Shape,
)

internal val LocalChatCompositeShapes =
    staticCompositionLocalOf {
        ChatCompositeShapes(
            messageBubble = RoundedCornerShape(4.dp),
            unreadMessagesIndicator = RoundedCornerShape(100.dp),
        )
    }
