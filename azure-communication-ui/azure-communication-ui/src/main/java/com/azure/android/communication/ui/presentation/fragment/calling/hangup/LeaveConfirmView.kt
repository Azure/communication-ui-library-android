// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.presentation.fragment.calling.hangup

import android.content.Context
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.AccessibilityDelegateCompat
import androidx.core.view.ViewCompat
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.azure.android.communication.ui.R
import com.azure.android.communication.ui.utilities.BottomCellAdapter
import com.azure.android.communication.ui.utilities.BottomCellItem
import com.microsoft.fluentui.drawer.DrawerDialog
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

internal class LeaveConfirmView(
    private val viewModel: LeaveConfirmViewModel,
    context: Context,
) : RelativeLayout(context) {

    private var leaveConfirmMenuTable: RecyclerView
    private var tittle: TextView
    private lateinit var leaveConfirmMenuDrawer: DrawerDialog
    private lateinit var bottomCellAdapter: BottomCellAdapter

    init {
        inflate(context, R.layout.azure_communication_ui_listview, this)
        leaveConfirmMenuTable = findViewById(R.id.bottom_drawer_table)
        tittle = TextView(context)
        this.setBackgroundResource(R.color.azure_communication_ui_color_bottom_drawer_background)
    }

    fun stop() {
        bottomCellAdapter.setBottomCellItems(mutableListOf())
        leaveConfirmMenuTable.layoutManager = null
        if (leaveConfirmMenuDrawer.isShowing) {
            leaveConfirmMenuDrawer.dismissDialog()
            viewModel.requestExitConfirmation()
        }
        this.removeAllViews()
    }

    fun start(
        viewLifecycleOwner: LifecycleOwner
    ) {
        val dp = context.resources.displayMetrics.density
        val tableLayoutParams = leaveConfirmMenuTable.layoutParams as LayoutParams
        tableLayoutParams.setMargins(0, (48 * dp).toInt(), 0, 0)
        leaveConfirmMenuTable.layoutParams = tableLayoutParams

        val titleLayoutParams = LayoutParams(LayoutParams.MATCH_PARENT, (24 * dp).toInt())
        titleLayoutParams.setMargins((16 * dp).toInt(), (12 * dp).toInt(), (72 * dp).toInt(), (12 * dp).toInt())

        tittle.setTextColor(ContextCompat.getColor(context, R.color.azure_communication_ui_color_text_primary))
        tittle.text = context.getString(R.string.azure_communication_ui_calling_view_leave_call)
        tittle.layoutParams = titleLayoutParams

        ViewCompat.setAccessibilityDelegate(
            this,
            object : AccessibilityDelegateCompat() {
                override fun onInitializeAccessibilityNodeInfo(
                    host: View,
                    info: AccessibilityNodeInfoCompat,
                ) {
                    super.onInitializeAccessibilityNodeInfo(host, info)
                    info.removeAction(AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_CLICK)
                    info.isClickable = false
                }
            }
        )
        this.contentDescription = context.getString(R.string.azure_communication_ui_calling_view_leave_call_dismiss)
        this.addView(tittle)
        bottomCellAdapter = BottomCellAdapter(context)
        bottomCellAdapter.setBottomCellItems(bottomCellItems)
        leaveConfirmMenuTable.adapter = bottomCellAdapter
        leaveConfirmMenuTable.layoutManager = LinearLayoutManager(context)

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
        leaveConfirmMenuDrawer.setOnDismissListener {
            viewModel.cancel()
        }
        leaveConfirmMenuDrawer.setContentView(this)
    }

    private val bottomCellItems: List<BottomCellItem>
        get() {
            val bottomCellItems = mutableListOf(
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
                    leaveConfirmMenuDrawer.dismiss()
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
                    leaveConfirmMenuDrawer.dismiss()
                },
            )
            return bottomCellItems
        }
}
