// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.calling.controlbar.more

import android.content.Context
import com.azure.android.communication.ui.calling.implementation.R
import com.azure.android.communication.ui.calling.logger.Logger
import com.azure.android.communication.ui.calling.models.CallCompositeButtonViewData
import com.azure.android.communication.ui.calling.models.createButtonClickEvent
import com.azure.android.communication.ui.calling.models.createCustomButtonClickEvent
import com.azure.android.communication.ui.calling.presentation.manager.DebugInfoManager
import com.azure.android.communication.ui.calling.presentation.manager.UpdatableOptionsManager
import com.azure.android.communication.ui.calling.redux.Dispatch
import com.azure.android.communication.ui.calling.redux.action.CaptionsAction
import com.azure.android.communication.ui.calling.redux.action.NavigationAction
import com.azure.android.communication.ui.calling.redux.state.ButtonState
import com.azure.android.communication.ui.calling.redux.state.VisibilityState
import com.azure.android.communication.ui.calling.redux.state.VisibilityStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

internal class MoreCallOptionsListViewModel(
    private val debugInfoManager: DebugInfoManager,
    private val updatableOptionsManager: UpdatableOptionsManager,
    private val showSupportFormOption: Boolean,
    private val dispatch: Dispatch,
    private val isCaptionsEnabled: Boolean,
    private val liveCaptionsButton: CallCompositeButtonViewData?,
    private val liveCaptionsToggleButton: CallCompositeButtonViewData?,
    private val spokenLanguageButton: CallCompositeButtonViewData?,
    private val captionsLanguageButton: CallCompositeButtonViewData?,
    private val shareDiagnosticsButton: CallCompositeButtonViewData?,
    private val reportIssueButton: CallCompositeButtonViewData?,
    private val logger: Logger,
) {
    private val unknown = "UNKNOWN"

    private lateinit var listEntriesMutableStateFlow: MutableStateFlow<List<Entry>>

    val callId: String
        get() {
            val lastKnownCallId = debugInfoManager.getDebugInfo().callHistoryRecords.lastOrNull()?.callIds?.lastOrNull()
            return "Call ID: \"${if (lastKnownCallId.isNullOrEmpty()) unknown else lastKnownCallId}\""
        }

    val displayStateFlow = MutableStateFlow(false)

    var shareDiagnostics: (() -> Unit)? = null

    val listEntriesStateFlow: StateFlow<List<Entry>> get() = listEntriesMutableStateFlow

    private fun createButtons(buttonViewDataState: ButtonState): List<Entry> {
        return mutableListOf<Entry>().apply {
            if (isCaptionsEnabled) {
                add(
                    Entry(
                        titleResourceId = R.string.azure_communication_ui_calling_captions_rtt_menu,
                        icon = R.drawable.azure_communication_ui_calling_ic_fluent_closed_caption_24_selector,
                        isVisible = buttonViewDataState.liveCaptionsButton?.isVisible ?: true && isAnyCaptionsSubMenuButtonsVisible(),
                        isEnabled = buttonViewDataState.liveCaptionsButton?.isEnabled ?: true,
                        showRightArrow = true,
                    ) { context ->
                        callOnClickHandler(context, liveCaptionsButton)
                        dispatch(CaptionsAction.ShowCaptionsOptions())
                    }
                )
            }
            add(
                Entry(
                    titleResourceId = R.string.azure_communication_ui_calling_view_share_diagnostics,
                    icon = R.drawable.azure_communication_ui_calling_ic_fluent_share_android_24_regular,
                    isVisible = buttonViewDataState.shareDiagnosticsButton?.isVisible ?: true,
                    isEnabled = buttonViewDataState.shareDiagnosticsButton?.isEnabled ?: true,
                ) { context ->
                    callOnClickHandler(context, shareDiagnosticsButton)
                    shareDiagnostics?.let { it() }
                }
            )
            if (showSupportFormOption) {
                add(
                    Entry(
                        titleResourceId = R.string.azure_communication_ui_calling_report_issue_title,
                        icon = R.drawable.azure_communication_ui_calling_ic_fluent_person_feedback_24_regular,
                        isVisible = buttonViewDataState.reportIssueButton?.isVisible ?: true,
                        isEnabled = buttonViewDataState.reportIssueButton?.isEnabled ?: true,
                    ) { context ->
                        callOnClickHandler(context, reportIssueButton)
                        requestReportIssueScreen()
                    }
                )
            }

            buttonViewDataState.callScreenCustomButtonsState
                .forEach { customButton ->
                    add(
                        Entry(
                            icon = customButton.drawableId,
                            titleText = customButton.title,
                            isEnabled = customButton.isEnabled ?: true,
                            isVisible = customButton.isVisible ?: true,
                            onClickListener = { context ->
                                try {
                                    customButton.id?.let { id ->
                                        val buttonViewData = updatableOptionsManager.getButton(id)
                                        buttonViewData.onClickHandler.handle(
                                            createCustomButtonClickEvent(
                                                context,
                                                buttonViewData
                                            )
                                        )
                                    }
                                } catch (e: Exception) {
                                    logger.error(
                                        "Call screen control bar custom button onClick exception.",
                                        e
                                    )
                                }
                            }
                        )
                    )
                }
        }
    }

    fun display() {
        displayStateFlow.value = true
    }

    fun close() {
        displayStateFlow.value = false
    }

    fun requestReportIssueScreen() {
        dispatch(NavigationAction.ShowSupportForm())
    }

    fun init(
        visibilityState: VisibilityState,
        buttonViewDataState: ButtonState,
    ) {
        listEntriesMutableStateFlow = MutableStateFlow(createButtons(buttonViewDataState))
        update(visibilityState, buttonViewDataState)
    }

    fun update(
        visibilityState: VisibilityState,
        buttonViewDataState: ButtonState,
    ) {
        if (visibilityState.status != VisibilityStatus.VISIBLE)
            close()

        listEntriesMutableStateFlow.value = createButtons(buttonViewDataState)
    }

    private fun isAnyCaptionsSubMenuButtonsVisible(): Boolean {
        return liveCaptionsToggleButton?.isVisible ?: true ||
            spokenLanguageButton?.isVisible ?: true ||
            captionsLanguageButton?.isVisible ?: true
    }

    private fun callOnClickHandler(
        context: Context,
        buttonOptions: CallCompositeButtonViewData?,
    ) {
        try {
            buttonOptions?.onClickHandler?.handle(
                createButtonClickEvent(context, buttonOptions)
            )
        } catch (e: Exception) {
            logger.error("Call screen control bar custom button onClick exception.", e)
        }
    }

    data class Entry(
        val titleResourceId: Int? = null,
        val titleText: String? = null,
        val icon: Int? = null,
        val showRightArrow: Boolean = false,
        val isVisible: Boolean = true,
        val isEnabled: Boolean = true,
        val onClickListener: (context: Context) -> Unit
    )
}
