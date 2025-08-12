// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.calling.hangup

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.view.View
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.azure.android.communication.ui.calling.implementation.R
import com.azure.android.communication.ui.calling.utilities.BottomCellAdapter
import com.azure.android.communication.ui.calling.utilities.BottomCellItem
import com.azure.android.communication.ui.calling.utilities.BottomCellItemType
import com.microsoft.fluentui.drawer.DrawerDialog
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlinx.coroutines.flow.collect

@SuppressLint("ViewConstructor")
internal class LeaveConfirmView(
    private val viewModel: LeaveConfirmViewModel,
    context: Context,
) : RelativeLayout(context) {

    private var leaveConfirmMenuTable: RecyclerView
    private lateinit var leaveConfirmMenuDrawer: DrawerDialog
    private lateinit var bottomCellAdapter: BottomCellAdapter

    init {
        inflate(context, R.layout.azure_communication_ui_calling_listview, this)
        leaveConfirmMenuTable = findViewById(R.id.bottom_drawer_table)
        setBackgroundResource(R.color.azure_communication_ui_calling_color_bottom_drawer_background)
    }

    fun stop() {
        bottomCellAdapter.setBottomCellItems(mutableListOf())
        leaveConfirmMenuTable.layoutManager = null
        leaveConfirmMenuDrawer.dismiss()
        leaveConfirmMenuDrawer.dismissDialog()

        removeAllViews()
    }

    fun start(
        viewLifecycleOwner: LifecycleOwner
    ) {
        bottomCellAdapter = BottomCellAdapter()
        bottomCellAdapter.setBottomCellItems(bottomCellItems)
        leaveConfirmMenuTable.adapter = bottomCellAdapter
        leaveConfirmMenuTable.layoutManager = AccessibilityManipulatingLinearLayoutManager(context)

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
        if (context.applicationInfo.targetSdkVersion >= 35) {
            ViewCompat.setOnApplyWindowInsetsListener(this) { view, windowInsets ->
                val orientation = resources.configuration.orientation
                val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemGestures())

                // Apply padding only in portrait orientation
                if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                    view.updatePadding(0, 0, 0, insets.bottom + 76)
                } else {
                    view.updatePadding(0, 0, 0, 0)
                }
                WindowInsetsCompat.CONSUMED
            }
        }

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
                    title = context.getString(R.string.azure_communication_ui_calling_view_leave_call),
                    contentDescription = context.getString(R.string.azure_communication_ui_calling_view_leave_confirm_menu),
                    isOnHold = false,
                    itemType = BottomCellItemType.BottomMenuTitle,
                ),

                // Leave
                BottomCellItem(
                    icon = ContextCompat.getDrawable(
                        context,
                        R.drawable.azure_communication_ui_calling_leave_confirm_telephone_24
                    ),
                    title = context.getString(R.string.azure_communication_ui_calling_view_leave_call_button_text),
                    contentDescription = context.getString(R.string.azure_communication_ui_calling_leave_confirm_drawer_leave_button),
                    isOnHold = false,
                    onClickAction = {
                        viewModel.confirm()
                    }
                ),

                // Cancel
                BottomCellItem(
                    icon = ContextCompat.getDrawable(
                        context,
                        R.drawable.azure_communication_ui_calling_leave_confirm_dismiss_24
                    ),
                    title = context.getString(R.string.azure_communication_ui_calling_view_leave_call_cancel),
                    contentDescription = context.getString(R.string.azure_communication_ui_calling_leave_confirm_drawer_cancel_button),
                    isOnHold = false,
                    onClickAction = {
                        cancelLeaveConfirm()
                    },
                )
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
            try {
                info?.let {
                    val itemInfo = AccessibilityNodeInfoCompat.CollectionItemInfoCompat.obtain(max(info.collectionItemInfo.rowIndex - 1, 0), info.collectionItemInfo.rowSpan, info.collectionItemInfo.columnIndex, info.collectionItemInfo.columnSpan, info.collectionItemInfo.isHeading, info.collectionItemInfo.isSelected)
                    if (info.collectionItemInfo.rowIndex == 0) {
                        info.setCollectionItemInfo(null)
                    } else {
                        info.setCollectionItemInfo(itemInfo)
                    }
                }
            } catch (e: Exception) {
                // Xamarin cause exception, info is null
                // Cause: -\java.lang.NullPointerException: Attempt to invoke virtual method 'int androidx.core.view.accessibility.AccessibilityNodeInfoCompat$CollectionItemInfoCompat.getRowIndex()
            }
        }
    }
}
