package com.azure.android.communication.ui.chat.implementation.ui

import androidx.compose.foundation.lazy.LazyListState

fun LazyListState.isScrolledToEnd() = layoutInfo.visibleItemsInfo.lastOrNull()?.index == layoutInfo.totalItemsCount - 1
fun LazyListState.outOfViewItemCount(): Int {
    val lastIdx = layoutInfo.visibleItemsInfo.lastOrNull()?.index
    val totalItems = layoutInfo.totalItemsCount
    return if (lastIdx == null) {
        0
    } else {
        totalItems - lastIdx - 1
    }
}
