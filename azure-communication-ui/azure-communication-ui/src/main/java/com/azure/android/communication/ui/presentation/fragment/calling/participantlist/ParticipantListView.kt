// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.presentation.fragment.calling.participantlist

import android.content.Context
import android.view.accessibility.AccessibilityManager
import android.widget.RelativeLayout
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

internal class ParticipantListView(
    private val viewModel: ParticipantListViewModel,
    context: Context,
) : RelativeLayout(context) {
    private var participantTable: RecyclerView

    private lateinit var participantListDrawer: DrawerDialog
    private lateinit var bottomCellAdapter: BottomCellAdapter
    private lateinit var accessibilityManager: AccessibilityManager

    init {
        inflate(context, R.layout.azure_communication_ui_listview, this)
        participantTable = findViewById(R.id.bottom_drawer_table)
        this.setBackgroundResource(R.color.azure_communication_ui_color_bottom_drawer_background)
    }

    fun start(viewLifecycleOwner: LifecycleOwner) {
        initializeParticipantListDrawer()
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getRemoteParticipantListCellStateFlow().collect {
                updateRemoteParticipantListContent(it)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getLocalParticipantListCellStateFlow().collect {
                updateLocalParticipantCellContent()
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getDisplayParticipantListStateFlow().collect {
                if (it) {
                    showParticipantList()
                }
            }
        }
    }

    fun stop() {
        bottomCellAdapter.setBottomCellItems(mutableListOf())
        participantTable.layoutManager = null
        if (participantListDrawer.isShowing) {
            participantListDrawer.dismissDialog()
            viewModel.displayParticipantList()
        }
        this.removeAllViews()
    }

    private fun showParticipantList() {
        if (!participantListDrawer.isShowing) {
            participantListDrawer.show()
        }
    }

    private fun initializeParticipantListDrawer() {
        accessibilityManager =
            context?.applicationContext?.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        participantListDrawer = DrawerDialog(context, DrawerDialog.BehaviorType.BOTTOM)
        participantListDrawer.setOnDismissListener {
            viewModel.closeParticipantList()
        }
        participantListDrawer.setContentView(this)
        bottomCellAdapter = BottomCellAdapter(context)
        participantTable.adapter = bottomCellAdapter
        updateRemoteParticipantListContent(0)
        participantTable.layoutManager = LinearLayoutManager(context)
    }

    private fun updateRemoteParticipantListContent(
        participantListCellModelList: List<ParticipantListCellModel>,
    ) {
        if (this::bottomCellAdapter.isInitialized) {
            val bottomCellItems =
                generateBottomCellItems(
                    participantListCellModelList
                )
            updateRemoteParticipantListContent(bottomCellItems.size)
            with(bottomCellAdapter) {
                setBottomCellItems(bottomCellItems)
                notifyDataSetChanged()
            }
        }
    }

    private fun updateLocalParticipantCellContent() {
        if (this::bottomCellAdapter.isInitialized) {

            val bottomCellItems = generateBottomCellItems(
                viewModel.getRemoteParticipantListCellStateFlow().value
            )
            with(bottomCellAdapter) {
                setBottomCellItems(bottomCellItems)
                notifyDataSetChanged()
            }
        }
    }

    private fun updateRemoteParticipantListContent(listSize: Int) {
        participantTable.layoutParams.height =
            ((listSize * 50 * context.resources.displayMetrics.density).toInt()).coerceAtMost(
                context.resources.displayMetrics.heightPixels / 2
            )
    }

    private fun generateBottomCellItems(
        remoteParticipantCellModels: List<ParticipantListCellModel>,
    ): MutableList<BottomCellItem> {
        val bottomCellItems = mutableListOf<BottomCellItem>()
        // since we can not get resources from model class, we create the local participant list cell
        // with suffix in this way
        val localParticipant =
            viewModel.createLocalParticipantListCell(
                resources.getString(R.string.azure_communication_ui_calling_view_participant_drawer_local_participant)
            )
        bottomCellItems
            .add(
                generateBottomCellItem(localParticipant.displayName, localParticipant.isMuted)
            )
        for (remoteParticipant in remoteParticipantCellModels) {
            bottomCellItems.add(
                generateBottomCellItem(
                    if (remoteParticipant.displayName.isEmpty()) context.getString(R.string.azure_communication_ui_calling_view_participant_drawer_unnamed)
                    else remoteParticipant.displayName,
                    remoteParticipant.isMuted
                )
            )
        }
        bottomCellItems.sortWith(compareBy(String.CASE_INSENSITIVE_ORDER, { it.title!! }))
        return bottomCellItems
    }

    private fun generateBottomCellItem(displayName: String?, isMuted: Boolean): BottomCellItem {
        return BottomCellItem(
            null,
            displayName,
            displayName + context.getString(R.string.azure_communication_ui_calling_view_participant_list_dismiss_list),
            ContextCompat.getDrawable(
                context,
                R.drawable.azure_communication_ui_ic_fluent_mic_off_24_regular
            ),
            R.color.azure_communication_ui_color_participant_list_mute_mic,
            context.getString(R.string.azure_communication_ui_calling_view_participant_list_muted_accessibility_label),
            isMuted
        ) {
            if (accessibilityManager.isEnabled) {
                participantListDrawer.dismiss()
            }
        }
    }
}
