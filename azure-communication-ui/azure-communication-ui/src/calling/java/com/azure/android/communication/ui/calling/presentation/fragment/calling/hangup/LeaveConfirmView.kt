// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.calling.hangup

import android.annotation.SuppressLint
import android.content.Context
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.azure.android.communication.ui.R
import com.azure.android.communication.ui.calling.utilities.BottomCellAdapter
import com.azure.android.communication.ui.calling.utilities.BottomCellItem
import com.azure.android.communication.ui.calling.utilities.BottomCellItemType
import com.microsoft.fluentui.drawer.DrawerDialog
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

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
        if (leaveConfirmMenuDrawer.isShowing) {
            leaveConfirmMenuDrawer.dismissDialog()
            viewModel.requestExitConfirmation()
        }
        removeAllViews()
    }

    fun start(
        viewLifecycleOwner: LifecycleOwner
    ) {
        bottomCellAdapter = BottomCellAdapter()
        bottomCellAdapter.setBottomCellItems(bottomCellItems)
        leaveConfirmMenuTable.adapter = bottomCellAdapter
        leaveConfirmMenuTable.layoutManager = RowCountReducingLinearLayoutManager(context)

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

    private fun dismissLeaveConfirm() {
        leaveConfirmMenuDrawer.dismiss()
    }

    private val bottomCellItems: List<BottomCellItem>
        get() {
            val bottomCellItems = mutableListOf(
                // Leave title
                BottomCellItem(
                    null,
                    context.getString(R.string.azure_communication_ui_calling_view_leave_call),
                    context.getString(R.string.azure_communication_ui_calling_view_leave_confirm_title_description),
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
                        R.drawable.azure_communication_ui_calling_leave_confirm_telephone_21_10
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
                        R.drawable.azure_communication_ui_calling_leave_confirm_dismiss_16_regular
                    ),
                    context.getString(R.string.azure_communication_ui_calling_view_leave_call_cancel),
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
                ) {
                    dismissLeaveConfirm()
                },
            )
            return bottomCellItems
        }
}
