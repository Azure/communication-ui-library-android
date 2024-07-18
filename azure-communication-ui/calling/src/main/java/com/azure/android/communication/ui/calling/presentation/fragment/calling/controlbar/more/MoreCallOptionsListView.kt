// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.calling.controlbar.more

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.azure.android.communication.ui.calling.implementation.R
import com.azure.android.communication.ui.calling.utilities.BottomCellAdapter
import com.azure.android.communication.ui.calling.utilities.BottomCellItem
import com.microsoft.fluentui.drawer.DrawerDialog
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@SuppressLint("ViewConstructor")
internal class MoreCallOptionsListView(
    context: Context,
    private val viewModel: MoreCallOptionsListViewModel
) : RelativeLayout(context) {

    private var recyclerView: RecyclerView
    private lateinit var menuDrawer: DrawerDialog
    private lateinit var bottomCellAdapter: BottomCellAdapter

    init {
        inflate(context, R.layout.azure_communication_ui_calling_listview, this)
        recyclerView = findViewById(R.id.bottom_drawer_table)
        this.setBackgroundResource(R.color.azure_communication_ui_calling_color_bottom_drawer_background)

        viewModel.shareDiagnostics = ::shareDiagnostics
    }

    fun start(viewLifecycleOwner: LifecycleOwner) {
        initializeDrawer()
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.displayStateFlow.collect {
                if (it) {
                    menuDrawer.show()
                }
            }
        }
    }

    fun stop() {
        bottomCellAdapter.setBottomCellItems(mutableListOf())
        recyclerView.layoutManager = null
        menuDrawer.dismiss()
        menuDrawer.dismissDialog()
        this.removeAllViews()
    }

    private fun initializeDrawer() {
        menuDrawer = DrawerDialog(context, DrawerDialog.BehaviorType.BOTTOM)
        menuDrawer.setContentView(this)
        menuDrawer.setOnDismissListener {
            viewModel.close()
        }

        bottomCellAdapter = BottomCellAdapter()
        bottomCellAdapter.setBottomCellItems(bottomCellItems)
        recyclerView.adapter = bottomCellAdapter
        recyclerView.layoutManager = LinearLayoutManager(context)
    }

    private val bottomCellItems get() = viewModel.listEntries.map { entry ->
        val title = if (entry.titleResourceId != null)
            context.getString(entry.titleResourceId)
        else entry.titleText

        BottomCellItem(
            icon = ContextCompat.getDrawable(
                context,
                entry.icon ?: android.R.drawable.ic_dialog_alert
            ),
            title = title,
            contentDescription = null,
            accessoryImage = null,
            accessoryColor = null,
            accessoryImageDescription = null,
            isChecked = false,
            participantViewData = null,
            isOnHold = false,
            showRightArrow = entry.showRightArrow,
            onClickAction =
            {
                menuDrawer.dismissDialog()
                entry.onClickListener(this.context)
            }
        )
    }

    private fun shareDiagnostics() {
        val share = Intent.createChooser(
            Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, viewModel.callId)
                type = "text/plain"
                putExtra(Intent.EXTRA_TITLE, context.getString(R.string.azure_communication_ui_calling_view_share_diagnostics_title))
            },
            null
        )
        context.startActivity(share)
    }
}
