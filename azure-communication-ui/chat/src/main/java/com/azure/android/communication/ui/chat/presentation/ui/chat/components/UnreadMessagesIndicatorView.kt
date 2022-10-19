// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.presentation.ui.chat.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.azure.android.communication.ui.chat.presentation.style.ChatCompositeTheme
import com.azure.android.communication.ui.chat.utilities.outOfViewItemCount
import kotlinx.coroutines.launch

/**
 * Shows a button that lets the user scroll to the bottom.
 */
@Composable
internal fun UnreadMessagesIndicatorView(
    scrollState: LazyListState,
    visible: Boolean,
    unreadCount: Int,
    totalMessages: Int,
) {
    val scope = rememberCoroutineScope()
    AnimatedVisibility(visible = visible) {
        Box(
            Modifier
                .fillMaxWidth()
                .background(color = ChatCompositeTheme.colors.component)) {
            BasicText(
                text = "View $unreadCount new messages",
                modifier = Modifier.clickable {
                    scope.launch {
                        scrollState.animateScrollToItem(totalMessages - 1)
                    }
                }
            )
        }
    }


}

@Preview
@Composable
internal fun PreviewUnreadMessagesIndicatorView() {
    UnreadMessagesIndicatorView(
        rememberLazyListState(),
        visible = true,
        unreadCount = 20,
        totalMessages = 30,
    )
}
