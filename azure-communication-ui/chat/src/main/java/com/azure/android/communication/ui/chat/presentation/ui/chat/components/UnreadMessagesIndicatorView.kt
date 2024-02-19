// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.presentation.ui.chat.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.focus.focusTarget
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.azure.android.communication.ui.chat.R
import com.azure.android.communication.ui.chat.presentation.style.ChatCompositeTheme
import com.microsoft.fluentui.theme.ThemeMode
import kotlinx.coroutines.launch

/**
 * Shows a button that lets the user scroll to the bottom.
 */
@Composable
internal fun UnreadMessagesIndicatorView(
    scrollState: LazyListState,
    visible: Boolean,
    unreadCount: Int,
) {
    val scope = rememberCoroutineScope()
    val content = LocalContext.current
    AnimatedVisibility(visible = visible) {
        ExtendedFloatingActionButton(
            icon = {
                Icon(
                    painterResource(id = R.drawable.azure_communication_ui_chat_ic_fluent_arrow_down_16_filled),
                    modifier =
                        Modifier
                            .height(ChatCompositeTheme.dimensions.unreadMessagesIndicatorIconHeight)
                            .padding(ChatCompositeTheme.dimensions.unreadMessagesIndicatorIconPadding),
                    contentDescription = null,
                    tint = ChatCompositeTheme.colors.inverseContent,
                )
            },
            text = {
                Text(
                    text =
                        when (unreadCount) {
                            in Int.MIN_VALUE..0 -> return@ExtendedFloatingActionButton
                            1 -> content.getString(R.string.azure_communication_ui_chat_unread_new_message)
                            in 2..99 ->
                                content.getString(
                                    R.string.azure_communication_ui_chat_unread_new_messages,
                                    unreadCount.toString(),
                                )
                            else -> content.getString(R.string.azure_communication_ui_chat_many_unread_new_messages)
                        },
                    fontSize = ChatCompositeTheme.dimensions.unreadMessagesIndicatorTextFontSize,
                    style = ChatCompositeTheme.typography.unreadMessageText,
                )
            },
            onClick = {
                scope.launch {
                    scrollState.animateScrollToItem(0)
                }
            },
            backgroundColor = ChatCompositeTheme.colors.unreadMessageIndicatorBackground,
            contentColor = ChatCompositeTheme.colors.background,
            modifier =
                Modifier
                    .height(ChatCompositeTheme.dimensions.unreadMessagesIndicatorHeight)
                    .clip(ChatCompositeTheme.shapes.unreadMessagesIndicator)
                    .focusable(true)
                    .focusTarget(),
        )
    }
}

@Preview
@Composable
internal fun PreviewUnreadMessagesIndicatorView() {
    Column {
        Text("Dark")
        ChatCompositeTheme(themeMode = ThemeMode.Dark) {
            UnreadMessagesIndicatorView(
                rememberLazyListState(),
                visible = true,
                unreadCount = 20,
            )
        }

        Text("Light")
        ChatCompositeTheme(themeMode = ThemeMode.Light) {
            UnreadMessagesIndicatorView(
                rememberLazyListState(),
                visible = true,
                unreadCount = 20,
            )
        }
    }
}
