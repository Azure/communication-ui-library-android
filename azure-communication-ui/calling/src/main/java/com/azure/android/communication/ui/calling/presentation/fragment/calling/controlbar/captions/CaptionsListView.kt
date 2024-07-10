// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.calling.controlbar.more.captions

import android.annotation.SuppressLint
import android.content.Context
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
import com.azure.android.communication.ui.calling.utilities.LocaleHelper
import com.microsoft.fluentui.drawer.DrawerDialog
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@SuppressLint("ViewConstructor")
internal class CaptionsListView(
    context: Context,
    private val viewModel: CaptionsListViewModel
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
            viewModel.activeCaptionLanguageStateFlow.collect {
                redrawCaptionsListView()
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.activeSpokenLanguageStateFlow.collect {
                redrawCaptionsListView()
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isTranscriptionEnabledStateFlow.collect {
                redrawCaptionsListView()
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isCaptionsActiveStateFlow.collect {
                redrawCaptionsListView()
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.canTurnOnCaptionsStateFlow.collect {
                redrawCaptionsListView()
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isCaptionsEnabledStateFlow.collect {
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
        val isTranscriptionEnabled = viewModel.isTranscriptionEnabledStateFlow.value
        val activeSpokenLanguage = viewModel.activeSpokenLanguageStateFlow.value
        val activeCaptionLanguage = viewModel.activeCaptionLanguageStateFlow.value
        val isCaptionsActive = viewModel.isCaptionsActiveStateFlow.value
        val canTurnOnCaptions = viewModel.canTurnOnCaptionsStateFlow.value

        val items = mutableListOf<BottomCellItem>()
        items.add(
            BottomCellItem(
                icon = ContextCompat.getDrawable(
                    context,
                    R.drawable.azure_communication_ui_calling_ic_fluent_closed_caption_24_selector
                ),
                title = context.getString(R.string.azure_communication_ui_calling_live_captions_title),
                "",
                null,
                null,
                null,
                null,
                null,
                isOnHold = null,
                BottomCellItemType.BottomMenuAction,
                onClickAction = null,
                showToggleButton = true,
                enableToggleButton = canTurnOnCaptions,
                isToggleButtonOn = isCaptionsActive,
                toggleButtonAction = { _, isChecked ->
                    viewModel.toggleCaptions(isChecked)
                }
            )
        )
        items.add(
            BottomCellItem(
                icon = ContextCompat.getDrawable(
                    context,
                    R.drawable.azure_communication_ui_calling_ic_fluent_spoken_language_24_selector
                ),
                title = context.getString(R.string.azure_communication_ui_calling_captions_spoken_language_title),
                "",
                null,
                null,
                null,
                null,
                null,
                isOnHold = null,
                BottomCellItemType.BottomMenuAction,
                showRightArrow = true,
                subtitle = LocaleHelper.getLocaleDisplayName(activeSpokenLanguage),
                onClickAction = {
                    viewModel.openSpokenLanguageSelection()
                },
                isEnabled = isCaptionsActive
            )
        )
        if (isTranscriptionEnabled) {
            items.add(
                BottomCellItem(
                    icon = ContextCompat.getDrawable(
                        context,
                        R.drawable.azure_communication_ui_calling_ic_fluent_caption_language_24_selector
                    ),
                    title = context.getString(R.string.azure_communication_ui_calling_captions_caption_language_title),
                    "",
                    null,
                    null,
                    null,
                    null,
                    null,
                    isOnHold = null,
                    BottomCellItemType.BottomMenuAction,
                    showRightArrow = true,
                    subtitle = LocaleHelper.getLocaleDisplayName(activeCaptionLanguage),
                    onClickAction = {
                        viewModel.openCaptionLanguageSelection()
                    },
                    isEnabled = isCaptionsActive
                )
            )
        }

        return items
    }
}