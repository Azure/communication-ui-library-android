// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.calling.controlbar.more

import android.content.Context
import com.azure.android.communication.ui.calling.implementation.R
import com.azure.android.communication.ui.calling.models.CallCompositeCustomButtonOptions
import com.azure.android.communication.ui.calling.models.createCustomButtonClickEvent
import com.azure.android.communication.ui.calling.presentation.manager.DebugInfoManager
import com.azure.android.communication.ui.calling.redux.Dispatch
import com.azure.android.communication.ui.calling.redux.action.NavigationAction
import com.azure.android.communication.ui.calling.redux.state.VisibilityState
import com.azure.android.communication.ui.calling.redux.state.VisibilityStatus
import kotlinx.coroutines.flow.MutableStateFlow

internal class MoreCallOptionsListViewModel(
    private val debugInfoManager: DebugInfoManager,
    private val showSupportFormOption: Boolean,
    private val dispatch: Dispatch,
    customButtons: Iterable<CallCompositeCustomButtonOptions>?,
) {
    private val unknown = "UNKNOWN"
    val callId: String
        get() {
            val lastKnownCallId = debugInfoManager.getDebugInfo().callHistoryRecords.lastOrNull()?.callIds?.lastOrNull()
            return "Call ID: \"${if (lastKnownCallId.isNullOrEmpty()) unknown else lastKnownCallId}\""
        }

    val displayStateFlow = MutableStateFlow(false)

    var shareDiagnostics: (() -> Unit)? = null

    val listEntries = mutableListOf<Entry>().apply {
        add(
            Entry(
                titleResourceId = R.string.azure_communication_ui_calling_view_share_diagnostics,
                icon = R.drawable.azure_communication_ui_calling_ic_fluent_share_android_24_regular
            ) {
                shareDiagnostics?.let { it() }
            }
        )
        if (showSupportFormOption) {
            add(
                Entry(
                    titleResourceId = R.string.azure_communication_ui_calling_report_issue_title,
                    icon = R.drawable.azure_communication_ui_calling_ic_fluent_person_feedback_24_regular
                ) {
                    requestReportIssueScreen()
                }
            )
        }

        customButtons?.forEach { customButton ->
            add(
                Entry(
                    icon = customButton.drawableId,
                    titleText = customButton.title,
                    onClickListener = {
                        try {
                            customButton.onClickHandler?.handle(createCustomButtonClickEvent(it, customButton))
                        } catch (_: Exception) {}
                    }
                )
            )
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

    fun update(visibilityState: VisibilityState) {
        if (visibilityState.status != VisibilityStatus.VISIBLE)
            close()
    }

    data class Entry(
        val titleResourceId: Int? = null,
        val titleText: String? = null,
        val icon: Int? = null,
        val onClickListener: (context: Context) -> Unit
    )

//    class Entries(val title: Int, val icon: Int?,) {
//        SHARE_DIAGNOSTICS(
//            R.string.azure_communication_ui_calling_view_share_diagnostics,
//            R.drawable.azure_communication_ui_calling_ic_fluent_share_android_24_regular
//        ),
//        REPORT_ISSUE(
//            R.string.azure_communication_ui_calling_report_issue_title,
//            R.drawable.azure_communication_ui_calling_ic_fluent_person_feedback_24_regular
//        ),
//    }
}
