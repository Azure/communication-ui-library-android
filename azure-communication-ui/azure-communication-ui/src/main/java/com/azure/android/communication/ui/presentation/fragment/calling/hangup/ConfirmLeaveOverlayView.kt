// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.presentation.fragment.calling.hangup

import android.content.Context
import android.view.accessibility.AccessibilityManager
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
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

internal class ConfirmLeaveOverlayView(
    private val viewModel: ConfirmLeaveOverlayViewModel,
    context: Context,
) : RelativeLayout(context) {

    private var leaveConfirmMenuTable: RecyclerView
    private var tittle: TextView
    private lateinit var leaveConfirmMenuDrawer: DrawerDialog
    private lateinit var bottomCellAdapter: BottomCellAdapter
    private lateinit var accessibilityManager: AccessibilityManager

    init {
        inflate(context, R.layout.azure_communication_ui_listview, this)
        leaveConfirmMenuTable = findViewById(R.id.bottom_drawer_table)
        val relativeParams = leaveConfirmMenuTable.layoutParams as LayoutParams
        relativeParams.setMargins(0, 120, 0, 0)
        leaveConfirmMenuTable.layoutParams = relativeParams

        val lp = LayoutParams(LayoutParams.MATCH_PARENT, 70)
        lp.setMargins(40, 12, 72, 0)

        tittle = TextView(context)
        tittle.setTextColor(ContextCompat.getColor(context, R.color.azure_communication_ui_color_text_primary))
        tittle.text = context.getString(R.string.azure_communication_ui_calling_view_overlay_leave_call)
        tittle.layoutParams = lp

        this.addView(tittle)
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
        initializeLeaveConfirmMenuDrawer()
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.shouldDisplayConfirmLeaveOverlayStateFlow.collect {
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
        accessibilityManager =
            context?.applicationContext?.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        leaveConfirmMenuDrawer = DrawerDialog(context, DrawerDialog.BehaviorType.BOTTOM)
        leaveConfirmMenuDrawer.setOnDismissListener {
            viewModel.cancel()
        }
        leaveConfirmMenuDrawer.setContentView(this)
        bottomCellAdapter = BottomCellAdapter(context)
        bottomCellAdapter.setBottomCellItems(bottomCellItems)
        leaveConfirmMenuTable.adapter = bottomCellAdapter
        leaveConfirmMenuTable.layoutManager = LinearLayoutManager(context)
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
                    context.getString(R.string.azure_communication_ui_calling_view_overlay_leave_call_button_text),
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
                    context.getString(R.string.azure_communication_ui_calling_view_overlay_cancel),
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
