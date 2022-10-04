package com.azure.android.communication.ui.chat.implementation.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.azure.android.communication.ui.chat.implementation.redux.states.MockParticipant

@ExperimentalAnimationApi
@Composable
fun AcsChatTypingIndicator(mockParticipants: List<MockParticipant>) {
    val typers = mockParticipants.filter { it.isTyping }

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

            mockParticipants.forEach {
                AnimatedVisibility(
                    visible = it.isTyping,
                    enter = expandHorizontally(), exit = shrinkHorizontally()

                ) {
                    AcsChatAvatar(name = it.displayName)
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
fun PreviewAcsChatTypingIndicator() {
    Column() {
        AcsChatTypingIndicator(
            mockParticipants = listOf(
                MockParticipant("User A", isTyping = true),
                MockParticipant("User B", isTyping = true)
            )
        )
    }
}
