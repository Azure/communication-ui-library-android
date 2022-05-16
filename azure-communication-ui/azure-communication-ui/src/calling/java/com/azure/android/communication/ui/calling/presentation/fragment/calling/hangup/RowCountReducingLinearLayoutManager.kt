package com.azure.android.communication.ui.calling.presentation.fragment.calling.hangup

import android.content.Context
import android.view.View
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.max

class RowCountReducingLinearLayoutManager(context: Context) : LinearLayoutManager(context) {
    override fun getRowCountForAccessibility(
        recycler: RecyclerView.Recycler,
        state: RecyclerView.State
    ): Int {
        return max(super.getRowCountForAccessibility(recycler, state) - 1, 0)
    }

    override fun onInitializeAccessibilityNodeInfoForItem(
        recycler: RecyclerView.Recycler,
        state: RecyclerView.State,
        host: View,
        info: AccessibilityNodeInfoCompat
    ) {
        super.onInitializeAccessibilityNodeInfoForItem(recycler, state, host, info)
        val itemInfo = AccessibilityNodeInfoCompat.CollectionItemInfoCompat.obtain(max(info.collectionItemInfo.rowIndex - 1, 0), info.collectionItemInfo.rowSpan, info.collectionItemInfo.columnIndex, info.collectionItemInfo.columnSpan, info.collectionItemInfo.isHeading, info.collectionItemInfo.isSelected)
        if (info.collectionItemInfo.rowIndex == 0) {
            info.setCollectionItemInfo(null)
        } else {
            info.setCollectionItemInfo(itemInfo)
        }
    }
}
