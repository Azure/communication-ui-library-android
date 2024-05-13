// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.calling.participant.menu

import android.content.Context
import android.view.accessibility.AccessibilityManager
import android.widget.RelativeLayout
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.azure.android.communication.ui.calling.implementation.R
import com.azure.android.communication.ui.calling.utilities.BottomCellAdapter
import com.microsoft.fluentui.drawer.DrawerDialog
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

internal class ParticipantMenuView(
    context: Context,
    private val viewModel: ParticipantMenuViewModel,
) : RelativeLayout(context) {
    private var participantTable: RecyclerView

    private lateinit var menuDrawer: DrawerDialog
    private lateinit var bottomCellAdapter: BottomCellAdapter
    private lateinit var accessibilityManager: AccessibilityManager

    init {
        inflate(context, R.layout.azure_communication_ui_calling_listview, this)
        participantTable = findViewById(R.id.bottom_drawer_table)
        this.setBackgroundResource(R.color.azure_communication_ui_calling_color_bottom_drawer_background)
    }

    fun start(viewLifecycleOwner: LifecycleOwner) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.displayMenuFlow.collect {
                if (it) {
                    show()
                } else {
                    if (menuDrawer.isShowing) {
                        menuDrawer.dismissDialog()
                    }
                }
            }
        }
    }

    fun stop() {
        // during screen rotation, destroy, the drawer should be displayed if open
        // to remove memory leak, on activity destroy dialog is dismissed
        // this setOnDismissListener(null) helps to not call view model state change during orientation
        menuDrawer.setOnDismissListener(null)
        bottomCellAdapter.setBottomCellItems(mutableListOf())
        participantTable.layoutManager = null
        menuDrawer.dismiss()
        menuDrawer.dismissDialog()
        this.removeAllViews()
    }

    private fun show() {
        if (!menuDrawer.isShowing) {
//            updateRemoteParticipantName()
            menuDrawer.show()
        }
    }
}