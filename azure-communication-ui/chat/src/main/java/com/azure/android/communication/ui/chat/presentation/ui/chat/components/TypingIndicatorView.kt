// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.presentation.ui.chat.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.azure.android.communication.ui.chat.R
import com.azure.android.communication.ui.chat.presentation.style.ChatCompositeTheme
import com.microsoft.fluentui.theme.ThemeMode

@Composable
internal fun TypingIndicatorView(
    typingParticipants: List<String>,
    modifier: Modifier = Modifier,
) {
    AnimatedVisibility(
        visible = typingParticipants.isNotEmpty(),
        enter = expandHorizontally(),
        exit = shrinkHorizontally(),
    ) {
        Row(
            modifier =
                modifier
                    .padding(horizontal = 5.dp)
                    .height(R.dimen.fluentui_avatar_size_small.dp)
                    .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy((5).dp),
        ) {
            AvatarGroup(typingParticipants)
            if (typingParticipants.isNotEmpty()) {
                val firstName = typingParticipants.first().substringBefore(" ")
                val context = LocalContext.current
                val typingMessage: String =
                    when (typingParticipants.size) {
                        1 ->
                            context.getString(
                                R.string.azure_communication_ui_chat_first_name_is_typing,
                                firstName,
                            )
                        2 -> {
                            val secondName = typingParticipants[1].substringBefore(" ")
                            context.getString(
                                R.string.azure_communication_ui_chat_two_names_are_typing,
                                firstName,
                                secondName,
                            )
                        }
                        else -> {
                            val size = typingParticipants.size - 2
                            val othersMessage =
                                if (size == 1) {
                                    stringResource(R.string.azure_communication_ui_chat_other)
                                } else {
                                    stringResource(R.string.azure_communication_ui_chat_others)
                                }
                            val secondName = typingParticipants[1].substringBefore(" ")
                            context.getString(
                                R.string.azure_communication_ui_chat_three_or_more_are_typing,
                                firstName,
                                secondName,
                                size,
                                othersMessage,
                            )
                        }
                    }
                Text(
                    typingMessage,
                    Modifier
                        .align(alignment = Alignment.CenterVertically),
                    style = ChatCompositeTheme.typography.body,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
internal fun PreviewTypingIndicatorView() {
    Column {
        ChatCompositeTheme(themeMode = ThemeMode.Dark) {
            Box(modifier = Modifier.background(ChatCompositeTheme.colors.background)) {
                TypingIndicatorView(
                    typingParticipants = listOf("User A", "User B", "User C", "User D"),
                )
            }
        }
        ChatCompositeTheme(themeMode = ThemeMode.Light) {
            TypingIndicatorView(
                typingParticipants = listOf("User A", "User B", "User C", "User D"),
            )
        }
    }
}
