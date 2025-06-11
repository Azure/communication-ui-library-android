// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.calling.controlbar.captions

import android.annotation.SuppressLint
import android.app.AlertDialog
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
import com.azure.android.communication.ui.calling.utilities.implementation.CompositeDrawerDialog
import com.azure.android.communication.ui.calling.utilities.launchAll
import com.microsoft.fluentui.drawer.DrawerDialog
import kotlinx.coroutines.flow.collect

@SuppressLint("ViewConstructor")
internal class CaptionsListView(
    context: Context,
    private val viewModel: CaptionsListViewModel,
) : RelativeLayout(context) {
    private var recyclerView: RecyclerView
    private lateinit var menuDrawer: DrawerDialog
    private lateinit var bottomCellAdapter: BottomCellAdapter
    private lateinit var rttConfirmDialog: AlertDialog

    init {
        inflate(context, R.layout.azure_communication_ui_calling_listview, this)
        recyclerView = findViewById(R.id.bottom_drawer_table)
        this.setBackgroundResource(R.color.azure_communication_ui_calling_color_bottom_drawer_background)
    }

    fun start(viewLifecycleOwner: LifecycleOwner) {
        initializeDrawer()

        viewLifecycleOwner.lifecycleScope.launchAll(
            {
                viewModel.displayStateFlow.collect {
                    if (it) {
                        menuDrawer.show()
                    } else {
                        menuDrawer.dismiss()
                    }
                }
            },
            {
                viewModel.activeCaptionLanguageStateFlow.collect {
                    redrawCaptionsListView()
                }
            },
            {
                viewModel.activeSpokenLanguageStateFlow.collect {
                    redrawCaptionsListView()
                }
            },
            {
                viewModel.isCaptionsLangButtonVisibleStateFlow.collect {
                    redrawCaptionsListView()
                }
            },
            {
                viewModel.isCaptionsActiveStateFlow.collect {
                    redrawCaptionsListView()
                }
            },
            {
                viewModel.isCaptionsToggleVisibleStateFlow.collect {
                    redrawCaptionsListView()
                }
            },
            {
                viewModel.isCaptionsToggleEnabledStateFlow.collect {
                    redrawCaptionsListView()
                }
            },
            {
                viewModel.isCaptionsEnabledStateFlow.collect {
                    redrawCaptionsListView()
                }
            },
            {
                viewModel.isSpokenLanguageButtonVisibleStateFlow.collect {
                    redrawCaptionsListView()
                }
            },
            {
                viewModel.isSpokenLanguageButtonEnabledStateFlow.collect {
                    redrawCaptionsListView()
                }
            },
            {
                viewModel.isRttButtonEnabledStateFlow.collect {
                    redrawCaptionsListView()
                }
            },
        )
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
        menuDrawer = CompositeDrawerDialog(
            context,
            DrawerDialog.BehaviorType.BOTTOM,
            R.string.azure_communication_ui_calling_view_captions_menu_list_accessibility_label,
        )
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
        val isCaptionsLangButtonVisible = viewModel.isCaptionsLangButtonVisibleStateFlow.value
        val isCaptionsLangButtonEnabled = viewModel.isCaptionsLangButtonEnabledStateFlow.value
        val activeSpokenLanguage = viewModel.activeSpokenLanguageStateFlow.value
        val activeCaptionLanguage = viewModel.activeCaptionLanguageStateFlow.value
        val isCaptionsActive = viewModel.isCaptionsActiveStateFlow.value
        val isToggleEnabled = viewModel.isCaptionsToggleEnabledStateFlow.value
        val isToggleVisible = viewModel.isCaptionsToggleVisibleStateFlow.value
        val isSpokenLanguageButtonVisible = viewModel.isSpokenLanguageButtonVisibleStateFlow.value
        val isSpokenLanguageButtonEnabled = viewModel.isSpokenLanguageButtonEnabledStateFlow.value
        val isRttButtonEnabledStateFlow = viewModel.isRttButtonEnabledStateFlow.value

        val items = mutableListOf<BottomCellItem>()

        items.add(
            BottomCellItem(
                icon = ContextCompat.getDrawable(
                    context, R.drawable.azure_communication_ui_calling_ic_fluent_chevron_left_24_filled
                ),
                title = context.getString(R.string.azure_communication_ui_calling_captions_rtt_menu),
                iconContentDescription = context.getString(R.string.azure_communication_ui_calling_view_go_back),
                itemType = BottomCellItemType.BottomMenuCenteredTitle,
                iconOnClickAction = {
                    viewModel.back()
                },
                showToggleButton = false,
                isToggleButtonOn = false,
                isEnabled = true,
            )
        )

        if (isToggleVisible) {
            items.add(
                BottomCellItem(
                    icon = ContextCompat.getDrawable(
                        context, R.drawable.azure_communication_ui_calling_ic_fluent_closed_caption_24_selector
                    ),
                    title = context.getString(R.string.azure_communication_ui_calling_live_captions_title),
                    showToggleButton = true,
                    isToggleButtonOn = isCaptionsActive,
                    isEnabled = isToggleEnabled,
                    toggleButtonAction = { _, isChecked ->
                        viewModel.toggleCaptions(context, isChecked)
                    }
                )
            )
        }
        if (isSpokenLanguageButtonVisible) {
            items.add(
                BottomCellItem(
                    icon = ContextCompat.getDrawable(
                        context,
                        R.drawable.azure_communication_ui_calling_ic_fluent_spoken_language_24_selector
                    ),
                    title = context.getString(R.string.azure_communication_ui_calling_captions_spoken_language_title),
                    itemType = BottomCellItemType.BottomMenuAction,
                    showRightArrow = true,
                    subtitle = LocaleHelper.getLocaleDisplayName(activeSpokenLanguage),
                    onClickAction = {
                        viewModel.openSpokenLanguageSelection(context)
                    },
                    isEnabled = isSpokenLanguageButtonEnabled
                )
            )
        }
        if (isCaptionsLangButtonVisible) {
            items.add(
                BottomCellItem(
                    icon = ContextCompat.getDrawable(
                        context,
                        R.drawable.azure_communication_ui_calling_ic_fluent_caption_language_24_selector
                    ),
                    title = context.getString(R.string.azure_communication_ui_calling_captions_caption_language_title),
                    itemType = BottomCellItemType.BottomMenuAction,
                    showRightArrow = true,
                    subtitle = LocaleHelper.getLocaleDisplayName(activeCaptionLanguage),
                    onClickAction = {
                        viewModel.openCaptionLanguageSelection(context)
                    },
                    isEnabled = isCaptionsLangButtonEnabled
                )
            )
        }

        items.add(
            BottomCellItem(
                icon = ContextCompat.getDrawable(
                    context,
                    R.drawable.azure_communication_ui_calling_ic_fluent_slide_text_call_20_regular
                ),
                title = context.getString(R.string.azure_communication_ui_calling_captions_turn_on_rtt),
                itemType = BottomCellItemType.BottomMenuAction,
                onClickAction = {
                    showStartRttConfirm()
                },
                isEnabled = isRttButtonEnabledStateFlow,
                showTopDivider = true,
            )
        )

        return items
    }

    private fun showStartRttConfirm() {
        val dialog = AlertDialog.Builder(context)
            .setTitle(context.getString(R.string.azure_communication_ui_calling_view_rtt_confirmation_title))
            .setMessage(context.getString(R.string.azure_communication_ui_calling_view_rtt_confirmation_message))
            .setPositiveButton(
                context.getString(R.string.azure_communication_ui_calling_view_rtt_confirmation_confirm)
            ) { _, _ ->
                viewModel.enableRTT()
            }
            .setNegativeButton(
                context.getString(R.string.azure_communication_ui_calling_notification_dismiss_accessibility_label)
            ) { _, _ -> }
            .create()
        dialog.setOnShowListener {
            val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            positiveButton.setBackgroundResource(R.drawable.azure_communication_ui_calling_image_button)

            val negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            negativeButton.setBackgroundResource(R.drawable.azure_communication_ui_calling_image_button)
        }
        dialog.show()
    }
}
