// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.presentation.ui.chat.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExtendedFloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.azure.android.communication.ui.chat.R
import com.azure.android.communication.ui.chat.presentation.style.ChatCompositeTheme
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
    val content = LocalContext.current
    AnimatedVisibility(visible = visible) {
        ExtendedFloatingActionButton(
            icon = {
                Icon(
                    painterResource(id = R.drawable.azure_communication_ui_chat_ic_fluent_arrow_down_16_filled),
                    modifier = Modifier.height(ChatCompositeTheme.dimensions.unreadMessagesIndicatorIconHeight)
                        .padding(ChatCompositeTheme.dimensions.unreadMessagesIndicatorIconPadding),
                    contentDescription = null
                )
            },
            text = {
                Text(
                    text = when (unreadCount) {
                        1 -> content.getString(R.string.azure_communication_ui_chat_unread_new_messages)
                        else -> content.getString(R.string.azure_communication_ui_chat_unread_new_messages, unreadCount.toString())
                    },
                    fontSize = ChatCompositeTheme.dimensions.unreadMessagesIndicatorTextFontSize
                )
            },
            onClick = {
                scope.launch {
                    scrollState.animateScrollToItem(totalMessages)
                }
            },
            backgroundColor = ChatCompositeTheme.colors.unreadMessageIndicatorBackground,
            contentColor = ChatCompositeTheme.colors.background,
            modifier = Modifier
                .height(ChatCompositeTheme.dimensions.unreadMessagesIndicatorHeight)
                .clip(ChatCompositeTheme.shapes.unreadMessagesIndicator)
        )
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
