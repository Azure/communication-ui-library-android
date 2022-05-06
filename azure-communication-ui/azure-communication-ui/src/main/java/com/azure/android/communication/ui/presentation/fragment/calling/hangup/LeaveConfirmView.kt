// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.presentation.fragment.calling.hangup

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.azure.android.communication.ui.R
import com.azure.android.communication.ui.utilities.BottomCellAdapter
import com.azure.android.communication.ui.utilities.BottomCellItem
import com.azure.android.communication.ui.utilities.BottomCellItemType
import com.microsoft.fluentui.drawer.DrawerDialog
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlin.math.max

@SuppressLint("ViewConstructor")
internal class LeaveConfirmView(
    private val viewModel: LeaveConfirmViewModel,
    context: Context,
) : RelativeLayout(context) {

    private var leaveConfirmMenuTable: RecyclerView
    private lateinit var leaveConfirmMenuDrawer: DrawerDialog
    private lateinit var bottomCellAdapter: BottomCellAdapter

    init {
        inflate(context, R.layout.azure_communication_ui_listview, this)
        leaveConfirmMenuTable = findViewById(R.id.bottom_drawer_table)
        setBackgroundResource(R.color.azure_communication_ui_color_bottom_drawer_background)
    }

    fun stop() {
        bottomCellAdapter.setBottomCellItems(mutableListOf())
        leaveConfirmMenuTable.layoutManager = null
        if (leaveConfirmMenuDrawer.isShowing) {
            leaveConfirmMenuDrawer.dismissDialog()
            viewModel.requestExitConfirmation()
        }
        removeAllViews()
    }

    fun start(
        viewLifecycleOwner: LifecycleOwner
    ) {
        bottomCellAdapter = BottomCellAdapter(context)
        bottomCellAdapter.setBottomCellItems(bottomCellItems)
        leaveConfirmMenuTable.adapter = bottomCellAdapter
        leaveConfirmMenuTable.layoutManager = AccessibilityManipulatingLinearLayoutManager(context)
        leaveConfirmMenuTable.contentDescription = "Leave Confirm Menu"

        initializeLeaveConfirmMenuDrawer()
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.shouldDisplayLeaveConfirmStateFlow.collect {
                if (it) {
                    showLeaveCallConfirm()
                }
            }
        }
    }

    private fun showLeaveCallConfirm() {
        if (!leaveConfirmMenuDrawer.isShowing) {
            leaveConfirmMenuDrawer.show()
        }
    }

    private fun initializeLeaveConfirmMenuDrawer() {
        leaveConfirmMenuDrawer = DrawerDialog(context, DrawerDialog.BehaviorType.BOTTOM)
        leaveConfirmMenuDrawer.setContentView(this)
        leaveConfirmMenuDrawer.setOnDismissListener {
            viewModel.cancel()
        }
    }

    private fun cancelLeaveConfirm() {
        leaveConfirmMenuDrawer.dismiss()
    }

    private val bottomCellItems: List<BottomCellItem>
        get() {
            val bottomCellItems = mutableListOf(
                // Leave title
                BottomCellItem(
                    null,
                    context.getString(R.string.azure_communication_ui_calling_view_leave_call),
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    BottomCellItemType.BottomMenuTitle,
                    null
                ),
                // Leave
                BottomCellItem(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.azure_communication_ui_leave_confirm_telephone_21_10
                    ),
                    context.getString(R.string.azure_communication_ui_calling_view_leave_call_button_text),
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
                ) {
                    viewModel.confirm()
                },
                // Cancel
                BottomCellItem(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.azure_communication_ui_leave_confirm_dismiss_16_regular
                    ),
                    context.getString(R.string.azure_communication_ui_calling_view_leave_call_cancel),
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
                ) {
                    cancelLeaveConfirm()
                },
            )
            return bottomCellItems
        }
    class AccessibilityManipulatingLinearLayoutManager(context: Context) : LinearLayoutManager(context) {
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
            info.setCollectionItemInfo(itemInfo)
        }
    }
}
