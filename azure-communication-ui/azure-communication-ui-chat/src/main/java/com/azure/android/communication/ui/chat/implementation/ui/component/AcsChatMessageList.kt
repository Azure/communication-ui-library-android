package com.azure.android.communication.ui.chat.implementation.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.azure.android.communication.ui.chat.implementation.redux.states.MockMessage
import com.azure.android.communication.ui.chat.implementation.ui.outOfViewItemCount
import kotlinx.coroutines.launch

@Composable
fun AcsChatMessageList(
    padding: PaddingValues,
    mockMessages: List<MockMessage>,
    scrollState: LazyListState,
    modifier: Modifier
) {
    val scope = rememberCoroutineScope()

    Box(modifier = modifier) {
        LazyColumn(
            state = scrollState,
            modifier = Modifier.padding(padding)
        ) {
            mockMessages.forEachIndexed() { index, content ->
                item {
                    val shouldGroup =
                        (index > 0 && mockMessages[index - 1].mockParticipant.equals(content.mockParticipant))
                    AcsChatMessageView(content, shouldGroup)
                }
            }
        }

        Box(modifier = Modifier.align(alignment = Alignment.BottomCenter)) {
            AnimatedVisibility(visible = scrollState.outOfViewItemCount() > 0) {
                AcsChatUnreadMessagesIndicator(
                    unreadCount = scrollState.outOfViewItemCount()
                ) {
                    scope.launch {
                        scrollState.animateScrollToItem(mockMessages.size)
                    }
                }
            }
        }
    }
}
