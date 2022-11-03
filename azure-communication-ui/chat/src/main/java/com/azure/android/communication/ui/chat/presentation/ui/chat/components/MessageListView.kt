// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.presentation.ui.chat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.azure.android.communication.ui.chat.presentation.ui.viewmodel.MessageViewModel
import com.azure.android.communication.ui.chat.presentation.ui.viewmodel.toViewModelList
import com.azure.android.communication.ui.chat.preview.MOCK_LOCAL_USER_ID
import com.azure.android.communication.ui.chat.preview.MOCK_MESSAGES
import com.azure.android.communication.ui.chat.redux.Dispatch
import com.azure.android.communication.ui.chat.redux.action.ChatAction
import com.azure.android.communication.ui.chat.utilities.outOfViewItemCount
import com.jakewharton.threetenabp.AndroidThreeTen
import org.threeten.bp.OffsetDateTime

const val MESSAGE_LIST_LOAD_MORE_THRESHOLD = 40

@Composable
internal fun MessageListView(
    modifier: Modifier,
    messages: List<MessageViewModel>,
    showLoading: Boolean,
    scrollState: LazyListState,
    dispatchers: Dispatch,
    latestReadMessageTimestamp: OffsetDateTime
) {
    requestPages(scrollState, messages, dispatchers)
    if (messages.isNotEmpty()) {
        sendReadReceipt(scrollState, messages, dispatchers)
    }
    LazyColumn(
        modifier = modifier.fillMaxHeight(),
        state = scrollState,
        reverseLayout = true,
    ) {
        items(messages.asReversed()) { message ->
            MessageView(message)
        }
        if (messages.isNotEmpty() && showLoading) {
            item {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    FluentCircularIndicator()
                }
            }
        }
    }
}

// Handle paging request
//
// If there is < Threshold out of screen
// we request more pages and mark a bookmark on the oldest message
// The bookmark blocks paging requests until it's no longer the oldest message
// i.e.
// If bookmark is at the end, no paging
// if bookmark is in middle, and out of screen < Threshold, page more data
@Composable
private fun requestPages(
    scrollState: LazyListState,
    messages: List<MessageViewModel>,
    dispatch: Dispatch
) {
    if (scrollState.layoutInfo.totalItemsCount == 0) return
    val currentLastMessage = messages.first()
    if (scrollState.outOfViewItemCount() < MESSAGE_LIST_LOAD_MORE_THRESHOLD) {
        val lastTrigger = remember { mutableStateOf("0") }
        if (lastTrigger.value != currentLastMessage.message.id) {
            lastTrigger.value = currentLastMessage.message.id ?: "0"
            dispatch(ChatAction.FetchMessages())
        }
    }
}

@Composable
private fun sendReadReceipt(
    scrollState: LazyListState,
    messages: List<MessageViewModel>,
    dispatch: Dispatch
) {
    val firstVisibleItemIndex = scrollState.firstVisibleItemIndex
    val currentBottomMessage = messages[messages.count() - firstVisibleItemIndex - 1]
    if (!currentBottomMessage.isLocalUser) {
        currentBottomMessage.message.id?.let {
            if (it.isNotEmpty()) {
                LaunchedEffect(it) {
                    dispatch(ChatAction.MessageRead(it))
                }
            }
        }
    }
}

@Preview
@Composable
internal fun PreviewMessageListView() {
    AndroidThreeTen.init(LocalContext.current)
    Box(modifier = Modifier.background(Color.White)) {
        MessageListView(
            showLoading = false,
            modifier = Modifier.padding(0.dp),
            messages = MOCK_MESSAGES.toViewModelList(LocalContext.current, MOCK_LOCAL_USER_ID),
            scrollState = LazyListState(),
            latestReadMessageTimestamp = OffsetDateTime.now()
        ) {}
    }
}
