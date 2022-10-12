// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.presentation.ui.chat.components

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.azure.android.communication.ui.chat.models.RemoteParticipantInfoModel
import com.azure.android.communication.ui.chat.service.sdk.wrapper.CommunicationIdentifier

@ExperimentalAnimationApi
@Composable
internal fun TypingIndicator(participants: List<RemoteParticipantInfoModel>) {
    val typers = participants.filter { it.isTyping }

    AnimatedVisibility(
            visible = typers.isNotEmpty(),
            enter = expandVertically(),
            exit = shrinkVertically()
    ) {

        Row(
                modifier = Modifier.padding(horizontal = 10.dp),
                horizontalArrangement = Arrangement.spacedBy((-12).dp)
        ) {
            Box(Modifier.size(width = 0.dp, height = 40.dp))

            participants.forEach {
                AnimatedVisibility(
                        visible = it.isTyping,
                        enter = expandHorizontally(), exit = shrinkHorizontally()

                ) {
                    AvatarView(name = it.displayName)
                }
            }

            if (typers.isNotEmpty()) {
                Text(
                        "is Typing",
                        Modifier
                            .padding(start = 15.dp)
                            .align(alignment = Alignment.CenterVertically)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@ExperimentalAnimationApi
@Composable
internal fun PreviewTypingIndicator() {
    TypingIndicator(
            participants = listOf(
                    RemoteParticipantInfoModel(
                            CommunicationIdentifier.CommunicationUserIdentifier(""), displayName = "User A", isTyping = true),
                    RemoteParticipantInfoModel(
                            CommunicationIdentifier.CommunicationUserIdentifier(""), displayName = "User B", isTyping = true),
            )
    )

}