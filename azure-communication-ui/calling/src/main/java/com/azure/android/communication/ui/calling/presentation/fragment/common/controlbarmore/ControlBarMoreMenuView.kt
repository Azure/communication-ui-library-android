// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.common.controlbarmore

import android.content.Context
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.azure.android.communication.ui.R
import com.azure.android.communication.ui.calling.redux.state.AudioDeviceSelectionStatus
import com.azure.android.communication.ui.calling.redux.state.AudioState
import com.azure.android.communication.ui.calling.utilities.BottomCellAdapter
import com.azure.android.communication.ui.calling.utilities.BottomCellItem
import com.microsoft.fluentui.drawer.DrawerDialog
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

internal class ControlBarMoreMenuView(
    context: Context,
    private val viewModel: ControlBarMoreMenuViewModel
) : RelativeLayout(context) {

    private var deviceTable: RecyclerView
    private lateinit var menuDrawer: DrawerDialog
    private lateinit var bottomCellAdapter: BottomCellAdapter

    init {
        inflate(context, R.layout.azure_communication_ui_calling_listview, this)
        deviceTable = findViewById(R.id.bottom_drawer_table)
        this.setBackgroundResource(R.color.azure_communication_ui_calling_color_bottom_drawer_background)
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
        deviceTable.layoutManager = null
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
        deviceTable.adapter = bottomCellAdapter
        deviceTable.layoutManager = LinearLayoutManager(context)
    }

    private val bottomCellItems: List<BottomCellItem>
        get() {
            val bottomCellItems = listOf(
                // Receiver (default)
                BottomCellItem(
                    icon = ContextCompat.getDrawable(
                        context,
                        R.drawable.azure_communication_ui_calling_ic_fluent_speaker_2_24_regular_composite_button_filled
                    ),
                    title = "Call ID",
                    contentDescription = null,
                    accessoryImage = ContextCompat.getDrawable(
                        context,
                        R.drawable.ms_ic_checkmark_24_filled
                    ),
                    accessoryColor = null,
                    accessoryImageDescription = context.getString(R.string.azure_communication_ui_calling_setup_view_audio_device_selected_accessibility_label),
                    enabled = true,
                    participantViewData = null,
                    isOnHold = false,
                ) {

                    menuDrawer.dismiss()
                },

            )

            return bottomCellItems
        }

    private fun updateSelectedAudioDevice(audioState: AudioState) {
        if (this::bottomCellAdapter.isInitialized) {
            bottomCellAdapter.enableBottomCellItem(getDeviceTypeName(audioState))
            announceForAccessibility(
                context.getString(
                    R.string.azure_communication_ui_calling_selected_audio_device_announcement,
                    getDeviceTypeName(audioState)
                )
            )
        }
    }

    private fun getDeviceTypeName(audioState: AudioState): String {
        return when (audioState.device) {
            AudioDeviceSelectionStatus.RECEIVER_REQUESTED, AudioDeviceSelectionStatus.RECEIVER_SELECTED ->
                context.getString(R.string.azure_communication_ui_calling_audio_device_drawer_android)
            AudioDeviceSelectionStatus.SPEAKER_REQUESTED, AudioDeviceSelectionStatus.SPEAKER_SELECTED ->
                context.getString(R.string.azure_communication_ui_calling_audio_device_drawer_speaker)
            AudioDeviceSelectionStatus.BLUETOOTH_SCO_SELECTED, AudioDeviceSelectionStatus.BLUETOOTH_SCO_REQUESTED ->
                audioState.bluetoothState.deviceName
        }
    }
}
