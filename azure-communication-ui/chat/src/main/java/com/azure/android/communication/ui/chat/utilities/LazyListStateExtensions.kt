package com.azure.android.communication.ui.chat.utilities

import androidx.compose.foundation.lazy.LazyListState

internal fun LazyListState.isScrolledToEnd() = layoutInfo.visibleItemsInfo.lastOrNull()?.index == layoutInfo.totalItemsCount - 1

internal fun LazyListState.outOfViewItemCount(): Int {
    val lastIdx = layoutInfo.visibleItemsInfo.lastOrNull()?.index
    val totalItems = layoutInfo.totalItemsCount
    return if (lastIdx == null) {
        0
    } else {
        totalItems - lastIdx - 1
    }
}
