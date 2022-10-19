// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.presentation.ui.chat.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.microsoft.fluentui.persona.AvatarSize

@Composable
internal fun TypingIndicatorView(typingParticipantsDisplayName: List<String>) {
    Row(
        modifier = Modifier.padding(horizontal = 10.dp)
            .height(25.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy((-12).dp)
    ) {

        typingParticipantsDisplayName.forEach { name ->
            AnimatedVisibility(
                visible = true,
                enter = expandHorizontally(),
                exit = shrinkHorizontally()
            ) { AvatarView(name = name, avatarSize = AvatarSize.SMALL) }
        }

        if (typingParticipantsDisplayName.isNotEmpty()) {
            val firstName = typingParticipantsDisplayName.first().substringBefore(" ")
            val typingMessage: String = when (typingParticipantsDisplayName.size) {
                1 -> "$firstName is typing"
                2 -> {
                    val secondName = typingParticipantsDisplayName[1].substringBefore(" ")
                    "$firstName and $secondName are typing"
                }
                else -> {
                    val size = typingParticipantsDisplayName.size - 2
                    val secondName = typingParticipantsDisplayName[1].substringBefore(" ")
                    "$firstName, $secondName, and $size others are typing"
                }
            }
            Text(
                typingMessage,
                Modifier
                    .padding(start = 15.dp)
                    .align(alignment = Alignment.CenterVertically)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
internal fun PreviewTypingIndicatorView() {
    TypingIndicatorView(
        typingParticipantsDisplayName = listOf("User A", "User B",),
    )
}
