// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.common.cameradevicelist

import android.content.Context
import android.view.accessibility.AccessibilityManager
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.azure.android.communication.ui.R
import com.azure.android.communication.ui.calling.utilities.BottomCellAdapter
import com.azure.android.communication.ui.calling.utilities.BottomCellItem
import com.microsoft.fluentui.drawer.DrawerDialog
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

internal class CameraDeviceListView(
    private val viewModel: CameraDeviceListViewModel,
    context: Context,
) : RelativeLayout(context) {
    private var table: RecyclerView

    private lateinit var drawer: DrawerDialog
    private lateinit var bottomCellAdapter: BottomCellAdapter
    private lateinit var accessibilityManager: AccessibilityManager

    init {
        inflate(context, R.layout.azure_communication_ui_calling_listview, this)
        table = findViewById(R.id.bottom_drawer_table)
        this.setBackgroundResource(R.color.azure_communication_ui_calling_color_bottom_drawer_background)
    }

    fun start(viewLifecycleOwner: LifecycleOwner) {
        initializeListDrawer()

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getCameraListCellStateFlow().collect {
                // To avoid, unnecessary updated to list, the state will update lists only when displayed
                if (drawer.isShowing) {
                    updateListContent(it)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getDisplayCameraListStateFlow().collect {
                if (it) {
                    showList()
                } else {
                    if (drawer.isShowing) {
                        drawer.dismissDialog()
                    }
                }
            }
        }
    }

    fun stop() {
        // during screen rotation, destroy, the drawer should be displayed if open
        // to remove memory leak, on activity destroy dialog is dismissed
        // this setOnDismissListener(null) helps to not call view model state change during orientation
        drawer.setOnDismissListener(null)
        bottomCellAdapter.setBottomCellItems(mutableListOf())
        table.layoutManager = null
        if (drawer.isShowing) {
            drawer.dismissDialog()
        }
        this.removeAllViews()
    }

    private fun showList() {
        if (!drawer.isShowing) {
            // on show the list is updated to get latest data
            updateListContent(viewModel.getCameraListCellStateFlow().value)
            drawer.show()
        }
    }

    private fun initializeListDrawer() {
        accessibilityManager =
            context?.applicationContext?.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        drawer = DrawerDialog(context, DrawerDialog.BehaviorType.BOTTOM)
        drawer.setOnDismissListener {
            viewModel.closeCameraDeviceSelectionMenu()
        }
        drawer.setContentView(this)
        bottomCellAdapter = BottomCellAdapter()
        table.adapter = bottomCellAdapter
        updateListContent(0)
        table.layoutManager = LinearLayoutManager(context)
    }

    private fun updateListContent(
        cameras: List<CameraListCellModel>,
    ) {
        if (this::bottomCellAdapter.isInitialized) {
            val bottomCellItems = generateBottomCellItems(cameras)
            updateListContent(bottomCellItems.size)
            with(bottomCellAdapter) {
                setBottomCellItems(bottomCellItems)
                notifyDataSetChanged()
            }
        }
    }

    private fun updateListContent(listSize: Int) {
        table.layoutParams.height =
            ((listSize * 50 * context.resources.displayMetrics.density).toInt()).coerceAtMost(
                context.resources.displayMetrics.heightPixels / 2
            )
    }

    private fun generateBottomCellItems(
        cameras: List<CameraListCellModel>,
    ): MutableList<BottomCellItem> {
        val bottomCellItems = mutableListOf<BottomCellItem>()

        for (camera in cameras) {
            bottomCellItems.add(
                generateBottomCellItem(
                    camera
                )
            )
        }
        bottomCellItems.sortWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.title!! })
        return bottomCellItems
    }

    private fun generateBottomCellItem(
        camera: CameraListCellModel,
    ): BottomCellItem {

        return BottomCellItem(
            title = camera.name,
            contentDescription = camera.name + context.getString(R.string.azure_communication_ui_cameras_list_dismiss_list),
            accessoryColor = null,
            accessoryImage = null,
            accessoryImageDescription = null,
            enabled = camera.id == viewModel.getSelectedDeviceID(),
            participantViewData = null,
            isOnHold = false,
            icon = ContextCompat.getDrawable(
                context,
                R.drawable.azure_communication_ui_calling_ic_fluent_video_24_filled_composite_button_enabled
            )
        ) {
            viewModel.selectCameraByID(camera.id)
            drawer.dismiss()
        }
    }
}
