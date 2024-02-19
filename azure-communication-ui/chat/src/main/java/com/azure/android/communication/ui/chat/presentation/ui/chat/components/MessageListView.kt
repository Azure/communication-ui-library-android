// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.presentation.ui.chat.components

import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
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
import kotlinx.coroutines.launch

internal const val MESSAGE_LIST_LOAD_MORE_THRESHOLD = 40

@Composable
internal fun MessageListView(
    modifier: Modifier,
    messages: List<MessageViewModel>,
    showLoading: Boolean,
    scrollState: LazyListState,
    dispatchers: Dispatch,
) {
    requestPages(scrollState, messages, dispatchers)
    // dismissKeyboardWhenScrollUp(scrollState)
    if (messages.isNotEmpty()) {
        sendReadReceipt(scrollState, messages, dispatchers)
        autoScrollToBottom(scrollState, messages)
    }

    LazyColumn(
        modifier = modifier.fillMaxHeight(),
        state = scrollState,
        reverseLayout = true,
    ) {
        itemsIndexed(
            messages.asReversed(),
            key = { index, item -> item.message.normalizedID },
        ) { _, message ->
            MessageView(message, dispatchers)
        }
        if (messages.isNotEmpty() && showLoading) {
            item {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxWidth(),
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
    dispatch: Dispatch,
) {
    if (scrollState.layoutInfo.totalItemsCount == 0) return
    val currentLastMessage = messages.first()
    if (scrollState.outOfViewItemCount() < MESSAGE_LIST_LOAD_MORE_THRESHOLD) {
        val lastTrigger = remember { mutableStateOf(0L) }
        if (lastTrigger.value != currentLastMessage.message.normalizedID) {
            lastTrigger.value = currentLastMessage.message.normalizedID ?: 0
            dispatch(ChatAction.FetchMessages())
        }
    }
}

@Composable
private fun autoScrollToBottom(
    scrollState: LazyListState,
    messages: List<MessageViewModel>,
) {
    val lastList = remember { mutableStateOf(messages) }
    val wasAtEnd = remember { mutableStateOf(scrollState.firstVisibleItemIndex) }

    if (wasAtEnd.value == 0 &&
        messages.last().message.normalizedID != lastList.value.last().message.normalizedID
    ) {
        LaunchedEffect(messages.last().message.normalizedID) {
            scrollState.scrollToItem(0)
        }
    }
    wasAtEnd.value = scrollState.firstVisibleItemIndex
    lastList.value = messages
}

@Composable
private fun dismissKeyboardWhenScrollUp(scrollState: LazyListState) {
    val coroutineScope = rememberCoroutineScope()
    val opened = remember { mutableStateOf(false) }
    val view = LocalView.current

    val atBottom = remember { mutableStateOf(scrollState.firstVisibleItemIndex == 0) }
    val currentlyAtBottom = scrollState.firstVisibleItemIndex == 0
    if (atBottom.value && !currentlyAtBottom && opened.value) {
        val activity = (LocalView.current.context as Activity)
        LaunchedEffect("KeyboardCloseEffect") {
            coroutineScope.launch {
                val imm: InputMethodManager? =
                    activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
                imm?.hideSoftInputFromWindow(view.windowToken, 0)
            }
        }
    }
    atBottom.value = currentlyAtBottom

    DisposableEffect(view) {
        val onGlobalListener =
            ViewTreeObserver.OnGlobalLayoutListener {
                val rect = Rect()
                view.getWindowVisibleDisplayFrame(rect)
                val screenHeight = view.rootView.height
                val keypadHeight = screenHeight - rect.bottom
                opened.value = (keypadHeight > screenHeight * 0.15)
            }
        view.viewTreeObserver.addOnGlobalLayoutListener(onGlobalListener)

        onDispose {
            view.viewTreeObserver.removeOnGlobalLayoutListener(onGlobalListener)
        }
    }
}

@Composable
private fun sendReadReceipt(
    scrollState: LazyListState,
    messages: List<MessageViewModel>,
    dispatch: Dispatch,
) {
    val firstVisibleItemIndex = scrollState.firstVisibleItemIndex
    val currentBottomMessage = messages[messages.count() - firstVisibleItemIndex - 1]
    currentBottomMessage.message.normalizedID.let {
        if (it != 0L) {
            LaunchedEffect(it) {
                if (!currentBottomMessage.isLocalUser) {
                    dispatch(ChatAction.MessageRead("$it"))
                } else {
                    dispatch(ChatAction.MessageLastReceived("$it"))
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
            messages =
                MOCK_MESSAGES.toViewModelList(
                    context = LocalContext.current,
                    localUserIdentifier = MOCK_LOCAL_USER_ID,
                    hiddenParticipant = mutableSetOf(),
                ),
            scrollState = LazyListState(),
        ) {}
    }
}
