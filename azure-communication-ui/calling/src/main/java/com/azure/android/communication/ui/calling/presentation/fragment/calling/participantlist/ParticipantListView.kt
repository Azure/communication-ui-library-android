// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.calling.participantlist

import android.content.Context
import android.view.accessibility.AccessibilityManager
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.azure.android.communication.ui.R
import com.azure.android.communication.ui.calling.models.CallCompositeParticipantViewData
import com.azure.android.communication.ui.calling.presentation.manager.AvatarViewManager
import com.azure.android.communication.ui.calling.utilities.BottomCellAdapter
import com.azure.android.communication.ui.calling.utilities.BottomCellItem
import com.microsoft.fluentui.drawer.DrawerDialog
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

internal class ParticipantListView(
    private val viewModel: ParticipantListViewModel,
    context: Context,
    private val avatarViewManager: AvatarViewManager,
) : RelativeLayout(context) {
    private var participantTable: RecyclerView

    private lateinit var participantListDrawer: DrawerDialog
    private lateinit var bottomCellAdapter: BottomCellAdapter
    private lateinit var accessibilityManager: AccessibilityManager

    init {
        inflate(context, R.layout.azure_communication_ui_calling_listview, this)
        participantTable = findViewById(R.id.bottom_drawer_table)
        this.setBackgroundResource(R.color.azure_communication_ui_calling_color_bottom_drawer_background)
    }

    fun start(viewLifecycleOwner: LifecycleOwner) {
        initializeParticipantListDrawer()

        viewLifecycleOwner.lifecycleScope.launch {
            avatarViewManager.getRemoteParticipantsPersonaSharedFlow().collect {
                if (participantListDrawer.isShowing) {
                    updateRemoteParticipantListContent(viewModel.getRemoteParticipantListCellStateFlow().value)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getRemoteParticipantListCellStateFlow().collect {
                // To avoid, unnecessary updated to list, the state will update lists only when displayed
                if (participantListDrawer.isShowing) {
                    updateRemoteParticipantListContent(it)
                }
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
                } else {
                    if (participantListDrawer.isShowing) {
                        participantListDrawer.dismissDialog()
                    }
                }
            }
        }
    }

    fun stop() {
        // during screen rotation, destroy, the drawer should be displayed if open
        // to remove memory leak, on activity destroy dialog is dismissed
        // this setOnDismissListener(null) helps to not call view model state change during orientation
        participantListDrawer.setOnDismissListener(null)
        bottomCellAdapter.setBottomCellItems(mutableListOf())
        participantTable.layoutManager = null
        participantListDrawer.dismiss()
        participantListDrawer.dismissDialog()
        this.removeAllViews()
    }

    private fun showParticipantList() {
        if (!participantListDrawer.isShowing) {
            // on show the list is updated to get latest data
            updateRemoteParticipantListContent(viewModel.getRemoteParticipantListCellStateFlow().value)
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
        bottomCellAdapter = BottomCellAdapter()
        participantTable.adapter = bottomCellAdapter
        updateRemoteParticipantListContent(0)
        participantTable.layoutManager = LinearLayoutManager(context)
    }

    private fun updateRemoteParticipantListContent(
        participantListCellModelList: List<ParticipantListCellModel>,
    ) {
        if (this::bottomCellAdapter.isInitialized) {
            val bottomCellItems = generateBottomCellItems(participantListCellModelList)
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
        val localParticipant = viewModel.createLocalParticipantListCell(
            resources.getString(R.string.azure_communication_ui_calling_view_participant_drawer_local_participant)
        )
        val localParticipantViewData =
            avatarViewManager.callCompositeLocalOptions?.participantViewData
        bottomCellItems
            .add(
                generateBottomCellItem(
                    getLocalParticipantNameToDisplay(
                        localParticipantViewData,
                        localParticipant.displayName
                    ),
                    localParticipant.isMuted,
                    localParticipantViewData,
                    localParticipant.isOnHold
                )
            )
        for (remoteParticipant in remoteParticipantCellModels) {
            val remoteParticipantViewData =
                avatarViewManager.getRemoteParticipantViewData(remoteParticipant.userIdentifier)
            val finalName =
                getNameToDisplay(remoteParticipantViewData, remoteParticipant.displayName)

            bottomCellItems.add(
                generateBottomCellItem(
                    finalName.ifEmpty { context.getString(R.string.azure_communication_ui_calling_view_participant_drawer_unnamed) },
                    remoteParticipant.isMuted, remoteParticipantViewData, remoteParticipant.isOnHold
                )
            )
        }
        bottomCellItems.sortWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.title!! })
        return bottomCellItems
    }

    private fun getLocalParticipantNameToDisplay(
        participantViewData: CallCompositeParticipantViewData?,
        displayName: String,
    ): String {
        participantViewData?.displayName?.let {
            if (it.trim().isNotEmpty()) {
                return it + " " + resources.getString(R.string.azure_communication_ui_calling_view_participant_drawer_local_participant)
            }
        }
        return displayName
    }

    private fun generateBottomCellItem(
        displayName: String?,
        isMuted: Boolean,
        participantViewData: CallCompositeParticipantViewData?,
        isOnHold: Boolean,
    ): BottomCellItem {
        val micIcon = ContextCompat.getDrawable(
            context,
            if (isMuted) R.drawable.azure_communication_ui_calling_ic_fluent_mic_off_24_filled_composite_button_filled_grey
            else R.drawable.azure_communication_ui_calling_ic_fluent_mic_on_24_filled_composite_button_filled_grey
        )

        val micAccessibilityAnnouncement = context.getString(
            if (isMuted) R.string.azure_communication_ui_calling_view_participant_list_muted_accessibility_label
            else R.string.azure_communication_ui_calling_view_participant_list_unmuted_accessibility_label
        )

        val onHoldAnnouncement: String = if (isOnHold) context.getString(R.string.azure_communication_ui_calling_remote_participant_on_hold) else ""
        val contentDescription = if (onHoldAnnouncement.isNotEmpty()) {
            displayName + onHoldAnnouncement + context.getString(R.string.azure_communication_ui_calling_view_participant_list_dismiss_list)
        } else {
            displayName + micAccessibilityAnnouncement + context.getString(R.string.azure_communication_ui_calling_view_participant_list_dismiss_list)
        }

        return BottomCellItem(
            null,
            displayName,
            contentDescription,
            micIcon,
            R.color.azure_communication_ui_calling_color_participant_list_mute_mic,
            micAccessibilityAnnouncement,
            isMuted,
            participantViewData,
            isOnHold,
        ) {
            if (accessibilityManager.isEnabled) {
                participantListDrawer.dismiss()
            }
        }
    }

    private fun getNameToDisplay(
        participantViewData: CallCompositeParticipantViewData?,
        displayName: String,
    ): String {
        return participantViewData?.displayName ?: displayName
    }
}
