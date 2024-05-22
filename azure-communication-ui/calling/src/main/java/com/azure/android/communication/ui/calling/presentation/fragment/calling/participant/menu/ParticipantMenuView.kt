// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.calling.participant.menu

import android.content.Context
import android.view.accessibility.AccessibilityManager
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.azure.android.communication.ui.calling.implementation.R
import com.azure.android.communication.ui.calling.utilities.BottomCellAdapter
import com.azure.android.communication.ui.calling.utilities.BottomCellItem
import com.azure.android.communication.ui.calling.utilities.BottomCellItemType
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
        initializeDrawer()
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
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.muteParticipantEnabledFlow.collect {
                refreshDrawerItems()
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.remoteParticipantEnabledFlow.collect {
                refreshDrawerItems()
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

    private fun initializeDrawer() {
        accessibilityManager =
            context?.applicationContext?.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager

        bottomCellAdapter = BottomCellAdapter()
        participantTable.adapter = bottomCellAdapter
        participantTable.layoutManager = LinearLayoutManager(context)

        menuDrawer = DrawerDialog(context, DrawerDialog.BehaviorType.BOTTOM)
        menuDrawer.setOnDismissListener {
            viewModel.close()
        }
        menuDrawer.setContentView(this)
    }

    private fun show() {
        if (!menuDrawer.isShowing) {
            bottomCellAdapter.setBottomCellItems(getBottomCellItems())
            bottomCellAdapter.notifyDataSetChanged()
            menuDrawer.show()
        }
    }

    private fun refreshDrawerItems() {
        if (menuDrawer.isShowing) {
            bottomCellAdapter.setBottomCellItems(getBottomCellItems())
            bottomCellAdapter.notifyDataSetChanged()
        }
    }

    private fun getBottomCellItems(): List<BottomCellItem> {

        val bottomCellItems = mutableListOf(
            // Leave title
            BottomCellItem(
                icon = null,
                title = viewModel.displayName ?: "",
                contentDescription = null,
                accessoryImage = null,
                accessoryColor = null,
                accessoryImageDescription = null,
                isChecked = null,
                participantViewData = null,
                isOnHold = false,
                itemType = BottomCellItemType.BottomMenuCenteredTitle,
                onClickAction = null
            ),
            BottomCellItem(
                icon = ContextCompat.getDrawable(
                    context,
                    R.drawable.azure_communication_ui_calling_ic_fluent_mic_off_24_regular
                ),
                title = context.getString(R.string.azure_communication_ui_calling_view_participant_menu_mute),
                contentDescription = context.getString(R.string.azure_communication_ui_calling_view_participant_menu_mute_accessibility_label),
                accessoryImage = null,
                accessoryColor = null,
                accessoryImageDescription = null,
                isChecked = null,
                participantViewData = null,
                isOnHold = false,
                isEnabled = viewModel.muteParticipantEnabledFlow.value,
                onClickAction = {
                    viewModel.muteParticipant()
                }
            ),
            BottomCellItem(
                icon = ContextCompat.getDrawable(
                    context,
                    R.drawable.azure_communication_ui_calling_ic_fluent_person_delete_24_regular
                ),
                title = context.getString(R.string.azure_communication_ui_calling_view_participant_menu_remove),
                contentDescription = context.getString(R.string.azure_communication_ui_calling_view_participant_menu_remove_accessibility_label),
                accessoryImage = null,
                accessoryColor = null,
                accessoryImageDescription = null,
                isChecked = null,
                participantViewData = null,
                isOnHold = false,
                isEnabled = viewModel.remoteParticipantEnabledFlow.value,
                onClickAction = {
                    viewModel.removeParticipant()
                },
            )
        )

        return bottomCellItems
    }
}
