// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.calling.controlbar.more.captions

import android.annotation.SuppressLint
import android.content.Context
import android.widget.RelativeLayout
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

@SuppressLint("ViewConstructor")
internal class CaptionsLanguageSelectionListView(
    context: Context,
    private val viewModel: CaptionsLanguageSelectionListViewModel
) : RelativeLayout(context) {
    private var recyclerView: RecyclerView
    private lateinit var menuDrawer: DrawerDialog
    private lateinit var bottomCellAdapter: BottomCellAdapter

    init {
        inflate(context, R.layout.azure_communication_ui_calling_listview, this)
        recyclerView = findViewById(R.id.bottom_drawer_table)
        this.setBackgroundResource(R.color.azure_communication_ui_calling_color_bottom_drawer_background)
    }

    fun start(viewLifecycleOwner: LifecycleOwner) {
        initializeDrawer()
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.displayStateFlow.collect {
                if (it) {
                    menuDrawer.show()
                } else {
                    menuDrawer.dismiss()
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.languagesListStateFlow.collect {
                redrawCaptionsListView()
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun redrawCaptionsListView() {
        bottomCellAdapter.setBottomCellItems(
            getBottomCellItems()
        )
        bottomCellAdapter.notifyDataSetChanged()
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
        bottomCellAdapter.setBottomCellItems(
            getBottomCellItems()
        )
        recyclerView.adapter = bottomCellAdapter
        recyclerView.layoutManager = LinearLayoutManager(context)
    }

    private fun getBottomCellItems(): List<BottomCellItem> {
        val items = mutableListOf<BottomCellItem>()
        viewModel.languagesListStateFlow.value.forEach { language ->
            items.add(
                BottomCellItem(
                    icon = null,
                    title = language,
                    "",
                    null,
                    null,
                    null,
                    language == viewModel.captionsActiveLanguage,
                    null,
                    null,
                    BottomCellItemType.BottomMenuAction,
                    onClickAction = {
                        viewModel.setActiveLanguage(language)
                    }
                )
            )
        }
        if (viewModel.languageSelectionTypeStateFlow != null) {
            items.add(
                0,
                BottomCellItem(
                    icon = null,
                    title = if (viewModel.languageSelectionTypeStateFlow == LanguageSelectionType.CAPTION) {
                        context.getString(R.string.azure_communication_ui_calling_captions_caption_language_title)
                    } else {
                        context.getString(R.string.azure_communication_ui_calling_captions_spoken_language_title)
                    },
                    "",
                    null,
                    null,
                    null,
                    null,
                    null,
                    isOnHold = null,
                    BottomCellItemType.BottomMenuTitle,
                    onClickAction = null
                )
            )
        }
        return items
    }
}
