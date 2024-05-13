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
                    updateRemoteParticipantListContent(viewModel.getRemoteParticipantListCellStateFlow().value)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getRemoteParticipantListCellStateFlow().collect { participants ->
                // To avoid, unnecessary updated to list, the state will update lists only when displayed
                if (participantListDrawer.isShowing) {
                    updateRemoteParticipantListContent(participants)
                }

                if (::admitDeclineAlertDialog.isInitialized && admitDeclineAlertDialog.isShowing &&
                    !participants.any { it.userIdentifier == admitDeclinePopUpParticipantId }
                ) {
                    admitDeclineAlertDialog.dismiss()
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

        // title for in call participants
        var titles = 1

        // title for in lobby participants
        if (viewModel.getRemoteParticipantListCellStateFlow().value?.any { it.status == ParticipantStatus.IN_LOBBY } == true) {
            titles += 1
        }

        // set the height of the list to be half of the screen height or 50dp per item, whichever is smaller
        participantTable.layoutParams.height =
            (((listSize - titles) * 50 * context.resources.displayMetrics.density + titles * 30 * context.resources.displayMetrics.density).toInt()).coerceAtMost(
                context.resources.displayMetrics.heightPixels / 2
            )
    }

    private fun generateBottomCellItems(
        remoteParticipantCellModels: List<ParticipantListCellModel>,
    ): MutableList<BottomCellItem> {
        val bottomCellItemsInCallParticipants = mutableListOf<BottomCellItem>()
        val bottomCellItemsInLobbyParticipants = mutableListOf<BottomCellItem>()
        // since we can not get resources from model class, we create the local participant list cell
        // with suffix in this way
        val localParticipant = viewModel.createLocalParticipantListCell(
            resources.getString(R.string.azure_communication_ui_calling_view_participant_drawer_local_participant)
        )
        val localParticipantViewData =
            avatarViewManager.callCompositeLocalOptions?.participantViewData
        bottomCellItemsInCallParticipants
            .add(
                generateBottomCellItem(
                    getLocalParticipantNameToDisplay(
                        localParticipantViewData,
                        localParticipant.displayName
                    ),
                    localParticipant.isMuted,
                    localParticipantViewData,
                    localParticipant.isOnHold,
                    localParticipant.userIdentifier,
                    localParticipant.status
                )
            )

        for (remoteParticipant in remoteParticipantCellModels) {
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
                null,
                context.getString(
                    R.string.azure_communication_ui_calling_participant_list_in_call_n_people,
                    bottomCellItemsInCallParticipants.size
                ),
                "",
                null,
                null,
                null,
                null,
                null,
                false,
                BottomCellItemType.BottomMenuTitle,
                null
            )
        )

        bottomCellItemsInLobbyParticipants.sortWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.title!! })
        if (bottomCellItemsInLobbyParticipants.isNotEmpty()) {
            bottomCellItemsInLobbyParticipants.add(
                0,
                BottomCellItem(
                    null,
                    context.getString(
                        R.string.azure_communication_ui_calling_participant_list_in_lobby_n_people,
                        bottomCellItemsInLobbyParticipants.size
                    ),
                    "",
                    null,
                    null,
                    null,
                    null,
                    null,
                    isOnHold = null,
                    BottomCellItemType.BottomMenuTitle,
                    onClickAction = null,
                    true,
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
        participantViewData?.displayName?.let {
            if (it.trim().isNotEmpty()) {
                return it + " " + resources.getString(R.string.azure_communication_ui_calling_view_participant_drawer_local_participant)
            }
        }
        return displayName
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
            null,
            displayName,
            contentDescription,
            if (status != ParticipantStatus.IN_LOBBY) micIcon else null,
            if (status != ParticipantStatus.IN_LOBBY) R.color.azure_communication_ui_calling_color_participant_list_mute_mic else null,
            micAccessibilityAnnouncement,
            isMuted,
            participantViewData,
            isOnHold,
            onClickAction = {
                if (accessibilityManager.isEnabled) {
                    participantListDrawer.dismiss()
                }

                if (status == ParticipantStatus.IN_LOBBY) {
                    showAdmitDialog(displayName, userIdentifier)
                } else if (accessibilityManager.isEnabled) {
                    participantListDrawer.dismiss()
                }
            }
        )
    }

    private fun showAdmitDialog(displayName: String?, userIdentifier: String) {
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

    private fun getNameToDisplay(
        participantViewData: CallCompositeParticipantViewData?,
        displayName: String,
    ): String {
        return participantViewData?.displayName ?: displayName
    }
}
