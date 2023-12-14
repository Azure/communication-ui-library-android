// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.calling.controlbar.more

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import com.azure.android.communication.ui.R
import com.azure.android.communication.ui.calling.presentation.manager.DebugInfoManager
import kotlinx.coroutines.flow.MutableStateFlow

internal class MoreCallOptionsListViewModel(
    private val debugInfoManager: DebugInfoManager,
    private val showSupportFormOption: Boolean
) {
    private val unknown = "UNKNOWN"
    val callId: String
        get() {
            val lastKnownCallId = debugInfoManager.getDebugInfo().callHistoryRecords.lastOrNull()?.callIds?.lastOrNull()
            return "Call ID: \"${if (lastKnownCallId.isNullOrEmpty()) unknown else lastKnownCallId}\""
        }

    val displayStateFlow = MutableStateFlow(false)

    val listEntries = mutableListOf<ListEntryViewModel>().apply {
        add(ListEntryViewModel(
            R.string.azure_communication_ui_calling_view_share_diagnostics,
            null,
            R.drawable.azure_communication_ui_calling_ic_fluent_share_android_24_regular
        ) {
            shareDiagnostics(it)
        })

        if (showSupportFormOption) {
            add(ListEntryViewModel(
                R.string.azure_communication_ui_calling_report_issue_title,
                null,
                R.drawable.azure_communication_ui_calling_ic_fluent_share_android_24_regular
            ) {
                println("asddsa");
            })
        }
    }

    fun display() {
        displayStateFlow.value = true
    }

    fun close() {
        displayStateFlow.value = false
    }

    fun shareDiagnostics(context: Context) {
        val share = Intent.createChooser(
            Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, callId)
                type = "text/plain"
                putExtra(Intent.EXTRA_TITLE, context.getString(R.string.azure_communication_ui_calling_view_share_diagnostics_title))
            },
            null
        )
        context.startActivity(share)
    }
    companion object {
        class ListEntryViewModel(val title: Int, val description: Int?, val icon: Int?, val action: (Context) -> Unit)
    }
}
