// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.calling.participantlist

import android.app.AlertDialog
import android.content.Context
import android.view.accessibility.AccessibilityManager
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.azure.android.communication.ui.calling.implementation.R
import com.azure.android.communication.ui.calling.models.CallCompositeParticipantViewData
import com.azure.android.communication.ui.calling.models.ParticipantStatus
import com.azure.android.communication.ui.calling.presentation.manager.AvatarViewManager
import com.azure.android.communication.ui.calling.utilities.BottomCellAdapter
import com.azure.android.communication.ui.calling.utilities.BottomCellItem
import com.azure.android.communication.ui.calling.utilities.BottomCellItemType
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
    private var admitDeclinePopUpParticipantId = ""
    private lateinit var admitDeclineAlertDialog: AlertDialog

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
                    updateParticipantListContent(
                        viewModel.participantListContentStateFlow.value,
                    )
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.participantListContentStateFlow.collect { vvm ->
                if (vvm.isDisplayed) {
                    showParticipantList()
                } else {
                    if (participantListDrawer.isShowing) {
                        participantListDrawer.dismissDialog()
                    }
                }

                // To avoid, unnecessary updated to list, the state will update lists only when displayed
                if (participantListDrawer.isShowing) {
                    updateParticipantListContent(vvm)
                }

                if (::admitDeclineAlertDialog.isInitialized && admitDeclineAlertDialog.isShowing &&
                    !vvm.remoteParticipantList.any { it.userIdentifier == admitDeclinePopUpParticipantId }
                ) {
                    admitDeclineAlertDialog.dismiss()
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
        updateTableHeight(0)
        participantTable.layoutManager = LinearLayoutManager(context)
    }

    private fun updateParticipantListContent(
        participantListContent: ParticipantListContent,
    ) {
        if (this::bottomCellAdapter.isInitialized) {
            val bottomCellItems = generateBottomCellItems(participantListContent)
            updateTableHeight(bottomCellItems.size)
            with(bottomCellAdapter) {
                setBottomCellItems(bottomCellItems)
                notifyDataSetChanged()
            }
        }
    }

    private fun updateTableHeight(listSize: Int) {

        // title for in call participants
        var titles = 1

        // title for in lobby participants
        if (viewModel.participantListContentStateFlow.value.remoteParticipantList.any { it.status == ParticipantStatus.IN_LOBBY }) {
            titles += 1
        }

        // set the height of the list to be half of the screen height or 50dp per item, whichever is smaller
        participantTable.layoutParams.height =
            (((listSize - titles) * 50 * context.resources.displayMetrics.density + titles * 30 * context.resources.displayMetrics.density).toInt()).coerceAtMost(
                context.resources.displayMetrics.heightPixels / 2
            )
    }

    private fun generateBottomCellItems(
        participantListContent: ParticipantListContent,
    ): MutableList<BottomCellItem> {
        val totalActiveParticipantCount = participantListContent.totalActiveParticipantCount
        val bottomCellItemsInCallParticipants = mutableListOf<BottomCellItem>()
        val bottomCellItemsInLobbyParticipants = mutableListOf<BottomCellItem>()
        // since we can not get resources from model class, we create the local participant list cell
        // with suffix in this way
        val localParticipantViewModel = participantListContent.localParticipantListCell

        val localParticipantViewData =
            avatarViewManager.callCompositeLocalOptions?.participantViewData
        bottomCellItemsInCallParticipants
            .add(
                generateBottomCellItem(
                    getLocalParticipantNameToDisplay(
                        localParticipantViewData,
                        localParticipantViewModel.displayName,
                    ),
                    localParticipantViewModel.isMuted,
                    localParticipantViewData,
                    localParticipantViewModel.isOnHold,
                    localParticipantViewModel.userIdentifier,
                    localParticipantViewModel.status
                )
            )

        for (remoteParticipant in participantListContent.remoteParticipantList) {
            val remoteParticipantViewData =
                avatarViewManager.getRemoteParticipantViewData(remoteParticipant.userIdentifier)
            val finalName =
                getNameToDisplay(remoteParticipantViewData, remoteParticipant.displayName)

            if (remoteParticipant.status == ParticipantStatus.IN_LOBBY) {
                bottomCellItemsInLobbyParticipants.add(
                    generateBottomCellItem(
                        finalName.ifEmpty { context.getString(R.string.azure_communication_ui_calling_view_participant_drawer_unnamed) },
                        null,
                        remoteParticipantViewData,
                        null,
                        remoteParticipant.userIdentifier,
                        remoteParticipant.status
                    )
                )
            } else if (remoteParticipant.status != ParticipantStatus.DISCONNECTED) {
                bottomCellItemsInCallParticipants.add(
                    generateBottomCellItem(
                        finalName.ifEmpty { context.getString(R.string.azure_communication_ui_calling_view_participant_drawer_unnamed) },
                        remoteParticipant.isMuted,
                        remoteParticipantViewData,
                        remoteParticipant.isOnHold,
                        remoteParticipant.userIdentifier,
                        remoteParticipant.status
                    )
                )
            }
        }
        bottomCellItemsInCallParticipants.sortWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.title!! })
        bottomCellItemsInCallParticipants.add(
            0,
            BottomCellItem(
                title = context.getString(
                    R.string.azure_communication_ui_calling_participant_list_in_call_n_people,
                    totalActiveParticipantCount + 1 // add one for local participant
                ),
                isOnHold = false,
                itemType = BottomCellItemType.BottomMenuTitle,
            )
        )

        val plusMoreParticipants = participantListContent.totalActiveParticipantCount -
            participantListContent.remoteParticipantList.count()

        if (plusMoreParticipants > 0) {
            bottomCellItemsInCallParticipants.add(
                BottomCellItem(
                    title = context.getString(
                        R.string.azure_communication_ui_calling_participant_list_in_call_plus_more_people,
                        plusMoreParticipants
                    ),
                    isOnHold = false,
                    itemType = BottomCellItemType.BottomMenuTitle,
                )
            )
        }
        bottomCellItemsInLobbyParticipants.sortWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.title!! })
        if (bottomCellItemsInLobbyParticipants.isNotEmpty()) {
            bottomCellItemsInLobbyParticipants.add(
                0,
                BottomCellItem(
                    title = context.getString(
                        R.string.azure_communication_ui_calling_participant_list_in_lobby_n_people,
                        bottomCellItemsInLobbyParticipants.size
                    ),
                    itemType = BottomCellItemType.BottomMenuTitle,
                    showAdmitAllButton = true,
                    admitAllButtonAction = {
                        admitAllLobbyParticipants()
                    }
                )
            )
        }
        return (bottomCellItemsInLobbyParticipants + bottomCellItemsInCallParticipants).toMutableList()
    }

    private fun admitAllLobbyParticipants() {
        viewModel.admitAllLobbyParticipants()
    }

    private fun getLocalParticipantNameToDisplay(
        participantViewData: CallCompositeParticipantViewData?,
        displayName: String,
    ): String {
        val localParticipantDisplayName = if (participantViewData?.displayName != null)
            participantViewData.displayName else displayName

        return resources.getString(R.string.azure_communication_ui_calling_view_participant_drawer_local_participant)
            .format(localParticipantDisplayName)
            .trim()
    }

    private fun generateBottomCellItem(
        displayName: String?,
        isMuted: Boolean?,
        participantViewData: CallCompositeParticipantViewData?,
        isOnHold: Boolean?,
        userIdentifier: String,
        status: ParticipantStatus?,
    ): BottomCellItem {
        val micIcon = ContextCompat.getDrawable(
            context,
            if (isMuted == true) R.drawable.azure_communication_ui_calling_ic_fluent_mic_off_24_filled_composite_button_filled_grey
            else R.drawable.azure_communication_ui_calling_ic_fluent_mic_on_24_filled_composite_button_filled_grey
        )

        val micAccessibilityAnnouncement = context.getString(
            if (isMuted == true) R.string.azure_communication_ui_calling_view_participant_list_muted_accessibility_label
            else R.string.azure_communication_ui_calling_view_participant_list_unmuted_accessibility_label
        )
        val onHoldAnnouncement: String = if (isOnHold == true) context.getString(R.string.azure_communication_ui_calling_remote_participant_on_hold) else ""
        val contentDescription = if (onHoldAnnouncement.isNotEmpty()) {
            displayName + onHoldAnnouncement + context.getString(R.string.azure_communication_ui_calling_view_participant_list_dismiss_list)
        } else if (status == ParticipantStatus.IN_LOBBY) {
            displayName + context.getString(R.string.azure_communication_ui_calling_view_participant_list_dismiss_lobby_list)
        } else {
            displayName + micAccessibilityAnnouncement + context.getString(R.string.azure_communication_ui_calling_view_participant_list_dismiss_list)
        }

        return BottomCellItem(
            title = displayName,
            contentDescription = contentDescription,
            accessoryImage = if (status != ParticipantStatus.IN_LOBBY) micIcon else null,
            accessoryColor = if (status != ParticipantStatus.IN_LOBBY) R.color.azure_communication_ui_calling_color_participant_list_mute_mic else null,
            accessoryImageDescription = micAccessibilityAnnouncement,
            isChecked = isMuted,
            participantViewData = participantViewData,
            isOnHold = isOnHold,
            onClickAction = {
                when (status) {
                    ParticipantStatus.IN_LOBBY -> showAdmitDialog(userIdentifier, displayName)
                    else -> displayParticipantMenu(userIdentifier, displayName)
                }

                if (status != ParticipantStatus.IN_LOBBY && accessibilityManager.isEnabled) {
                    participantListDrawer.dismiss()
                }
            }
        )
    }

    private fun showAdmitDialog(userIdentifier: String, displayName: String?) {
        admitDeclinePopUpParticipantId = userIdentifier
        val dialog =
            AlertDialog.Builder(context, R.style.AzureCommunicationUICalling_AlertDialog_Theme)
        dialog.setMessage(
            context.getString(
                R.string.azure_communication_ui_calling_admit_name,
                displayName
            )
        )
            .setPositiveButton(
                context.getString(
                    R.string.azure_communication_ui_calling_admit
                )
            ) { _, _ ->
                viewModel.admitParticipant(userIdentifier)
            }
            .setNegativeButton(
                context.getString(
                    R.string.azure_communication_ui_calling_decline
                )
            ) { _, _ ->
                viewModel.declineParticipant(userIdentifier)
            }
        admitDeclineAlertDialog = dialog.create()
        admitDeclineAlertDialog.show()
    }

    private fun displayParticipantMenu(userIdentifier: String, displayName: String?) {
        viewModel.displayParticipantMenu(userIdentifier, displayName)
    }

    private fun getNameToDisplay(
        participantViewData: CallCompositeParticipantViewData?,
        displayName: String,
    ): String {
        return participantViewData?.displayName ?: displayName
    }
}
