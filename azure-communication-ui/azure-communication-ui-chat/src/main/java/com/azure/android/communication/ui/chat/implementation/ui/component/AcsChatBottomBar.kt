package com.azure.android.communication.ui.chat.implementation.ui.component

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.azure.android.communication.ui.chat.implementation.redux.states.MockParticipant
import com.azure.android.communication.ui.chat.implementation.ui.view_models.MockUiViewModel
import java.util.Collections

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun BottomBar(viewModel: MockUiViewModel, resetScroll: () -> Unit = {}) {
    Column() {
        AcsChatTypingIndicator(viewModel.mockParticipants)
        AcsChatUserInput(
            onMessageSent = { content ->
                viewModel.postMessage(content)
            },
            onUserTyping = { content ->
                viewModel.onUserTyping(content)
            },
            resetScroll = resetScroll,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .imePadding(),
        )
    }
}

@Composable
@Preview(showBackground = true)
fun PreviewAcsChatBottomBar() {
    BottomBar(
        viewModel = MockUiViewModel(
            mockParticipants = listOf(
                MockParticipant("User A", isTyping = true),
                MockParticipant("User B", isTyping = true),
            ),
            mockMessages = Collections.emptyList(),
            onUserTyping = {},
            postMessage = {}
        )
    )
}
