// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.presentation.ui.chat.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
internal fun TypingIndicatorView(typingParticipantsDisplayName: List<String>) {
    Row(
        modifier = Modifier.padding(horizontal = 10.dp),
        horizontalArrangement = Arrangement.spacedBy((-12).dp)
    ) {
        Box(Modifier.size(width = 0.dp, height = 40.dp))

        typingParticipantsDisplayName.forEach {
            AnimatedVisibility(
                visible = true,
                enter = expandHorizontally(), exit = shrinkHorizontally()
            ) {
                AvatarView(name = it)
            }
        }

        if (typingParticipantsDisplayName.isNotEmpty()) {
            Text(
                "is Typing",
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
