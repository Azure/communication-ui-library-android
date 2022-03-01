// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.presentation.fragment.common.audiodevicelist

import android.content.Context
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.azure.android.communication.ui.R
import com.azure.android.communication.ui.redux.state.AudioDeviceSelectionStatus
import com.azure.android.communication.ui.utilities.BottomCellAdapter
import com.azure.android.communication.ui.utilities.BottomCellItem
import com.microsoft.fluentui.drawer.DrawerDialog
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

internal enum class AudioDeviceType {
    BLUETOOTH_SCO,
    ANDROID,
    SPEAKER,
}

internal class AudioDeviceListView(
    private val viewModel: AudioDeviceListViewModel,
    context: Context,
) : RelativeLayout(context) {

    private var deviceTable: RecyclerView
    private lateinit var audioDeviceDrawer: DrawerDialog
    private lateinit var bottomCellAdapter: BottomCellAdapter

    init {
        inflate(context, R.layout.azure_communication_ui_listview, this)
        deviceTable = findViewById(R.id.bottom_drawer_table)
        this.setBackgroundResource(R.color.azure_communication_ui_color_bottom_drawer_background)
    }

    fun start(viewLifecycleOwner: LifecycleOwner) {
        initializeAudioDeviceDrawer()
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.audioDeviceSelectionStatusStateFlow.collect {
                updateSelectedAudioDevice(it)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.displayAudioDeviceSelectionMenuStateFlow.collect {
                if (it) {
                    showAudioDeviceSelectionMenu()
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.bluetoothScoAvailableStateFlow.collect {
                // / rebind the list of items
                bottomCellAdapter = BottomCellAdapter(context)
                bottomCellAdapter.setBottomCellItems(bottomCellItems)
                deviceTable.adapter = bottomCellAdapter
            }
        }
    }

    fun stop() {
        bottomCellAdapter.setBottomCellItems(mutableListOf())
        deviceTable.layoutManager = null
        if (audioDeviceDrawer.isShowing) {
            audioDeviceDrawer.dismissDialog()
            viewModel.displayAudioDeviceSelectionMenu()
        }
        this.removeAllViews()
    }

    private fun showAudioDeviceSelectionMenu() {
        audioDeviceDrawer.show()
    }

    private fun initializeAudioDeviceDrawer() {
        audioDeviceDrawer = DrawerDialog(context, DrawerDialog.BehaviorType.BOTTOM)
        audioDeviceDrawer.setContentView(this)
        audioDeviceDrawer.setOnDismissListener {
            viewModel.closeAudioDeviceSelectionMenu()
        }

        bottomCellAdapter = BottomCellAdapter(context)
        bottomCellAdapter.setBottomCellItems(bottomCellItems)
        deviceTable.adapter = bottomCellAdapter
        deviceTable.layoutManager = LinearLayoutManager(context)
    }

    private val bottomCellItems: List<BottomCellItem>
        get() {
            val initialDevice = viewModel.audioDeviceSelectionStatusStateFlow.value
            val bottomCellItems = mutableListOf(
                // Receiver (default)
                BottomCellItem(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.azure_communication_ui_ic_fluent_speaker_2_24_regular_composite_button_filled
                    ),
                    getDeviceTypeName(AudioDeviceType.ANDROID),
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.ms_ic_checkmark_24_filled
                    ),
                    null,
                    resources.getString(R.string.azure_communication_ui_setup_view_audio_device_selected_accessibility_label),
                    enabled = initialDevice == AudioDeviceSelectionStatus.RECEIVER_SELECTED
                ) {
                    viewModel.switchAudioDevice(AudioDeviceSelectionStatus.RECEIVER_REQUESTED)
                    audioDeviceDrawer.dismiss()
                },
                // Speaker
                BottomCellItem(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.azure_communication_ui_ic_fluent_speaker_2_24_filled_composite_button_enabled
                    ),
                    getDeviceTypeName(AudioDeviceType.SPEAKER),
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.ms_ic_checkmark_24_filled
                    ),
                    null,
                    resources.getString(R.string.azure_communication_ui_setup_view_audio_device_selected_accessibility_label),
                    enabled = initialDevice == AudioDeviceSelectionStatus.SPEAKER_SELECTED,

                ) {
                    viewModel.switchAudioDevice(AudioDeviceSelectionStatus.SPEAKER_REQUESTED)
                    audioDeviceDrawer.dismiss()
                },

            )

            if (viewModel.bluetoothScoAvailableStateFlow.value) {
                bottomCellItems.add(
                    BottomCellItem(
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.azure_communication_ui_ic_fluent_speaker_bluetooth_24_regular
                        ),
                        getDeviceTypeName(AudioDeviceType.BLUETOOTH_SCO),
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.ms_ic_checkmark_24_filled
                        ),

                        null,
                        resources.getString(R.string.azure_communication_ui_setup_view_audio_device_selected_accessibility_label),
                        enabled = initialDevice == AudioDeviceSelectionStatus.BLUETOOTH_SCO_SELECTED,

                    ) {
                        viewModel.switchAudioDevice(AudioDeviceSelectionStatus.BLUETOOTH_SCO_REQUESTED)
                        audioDeviceDrawer.dismiss()
                    }
                )
            }
            return bottomCellItems
        }

    private fun updateSelectedAudioDevice(audioDeviceSelectionStatus: AudioDeviceSelectionStatus) {
        if (this::bottomCellAdapter.isInitialized) {
            when (audioDeviceSelectionStatus) {
                AudioDeviceSelectionStatus.SPEAKER_SELECTED -> {
                    bottomCellAdapter.enableBottomCellItem(getDeviceTypeName(AudioDeviceType.SPEAKER))
                }
                AudioDeviceSelectionStatus.RECEIVER_SELECTED -> {
                    bottomCellAdapter.enableBottomCellItem(getDeviceTypeName(AudioDeviceType.ANDROID))
                }
                AudioDeviceSelectionStatus.BLUETOOTH_SCO_SELECTED -> {
                    bottomCellAdapter.enableBottomCellItem(getDeviceTypeName(AudioDeviceType.BLUETOOTH_SCO))
                }
            }
        }
    }

    private fun getDeviceTypeName(audioDeviceType: AudioDeviceType): String {
        return when (audioDeviceType) {
            AudioDeviceType.ANDROID -> context.getString(R.string.azure_communication_ui_audio_device_drawer_android)
            AudioDeviceType.SPEAKER -> context.getString(R.string.azure_communication_ui_audio_device_drawer_speaker)
            AudioDeviceType.BLUETOOTH_SCO -> context.getString(R.string.azure_communication_ui_audio_device_drawer_bluetooth)
        }
    }
}
