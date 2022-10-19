// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.presentation.ui.chat.components

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.LaunchedEffect


import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.azure.android.communication.ui.chat.presentation.ui.viewmodel.MessageViewModel
import com.azure.android.communication.ui.chat.presentation.ui.viewmodel.toViewModelList
import com.azure.android.communication.ui.chat.preview.MOCK_LOCAL_USER_ID
import com.azure.android.communication.ui.chat.preview.MOCK_MESSAGES
import com.azure.android.communication.ui.chat.utilities.isScrolledToEnd
import com.azure.android.communication.ui.chat.utilities.outOfViewItemCount
import kotlinx.coroutines.launch

@Composable
internal fun MessageListView(
    modifier: Modifier,
    messages: List<MessageViewModel>,
    scrollState: LazyListState,
) {

    val autoScroll = remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    if (scrollState.isScrollInProgress) {
        autoScroll.value = scrollState.isScrolledToEnd()
    }

    if (scrollState.outOfViewItemCount() > 0 && autoScroll.value) {
        LaunchedEffect(scrollState.outOfViewItemCount()) {
            scope.launch {
                scrollState.animateScrollToItem(messages.size - 1)
            }
        }
    }

    LazyColumn(
        modifier = modifier.fillMaxHeight(),
        state = scrollState
    ) {
        items(messages) { message ->
            MessageView(message)
        }
    }
}

@Preview
@Composable
internal fun PreviewMessageListView() {
    MessageListView(
        modifier = Modifier.padding(0.dp),
        messages = MOCK_MESSAGES.toViewModelList(MOCK_LOCAL_USER_ID),
        scrollState = LazyListState(),
    )
}
