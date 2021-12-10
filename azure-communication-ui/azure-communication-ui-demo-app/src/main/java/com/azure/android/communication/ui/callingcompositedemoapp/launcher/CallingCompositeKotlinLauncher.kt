// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callingcompositedemoapp.launcher

import android.content.Context
import com.azure.android.communication.common.CommunicationTokenCredential
import com.azure.android.communication.common.CommunicationTokenRefreshOptions
import com.azure.android.communication.ui.CallComposite
import com.azure.android.communication.ui.CallCompositeBuilder
import com.azure.android.communication.ui.GroupCallOptions
import com.azure.android.communication.ui.TeamsMeetingOptions
import java.util.UUID
import java.util.concurrent.Callable

class CallingCompositeKotlinLauncher(private val tokenRefresher: Callable<String>) :
    CallingCompositeLauncher {

    override fun launch(
        context: Context,
        displayName: String,
        groupId: UUID?,
        meetingLink: String?,
        showAlert: ((String) -> Unit)?,
    ) {
        val callComposite: CallComposite = CallCompositeBuilder().build()

        callComposite.setOnErrorHandler {
            println("================= application is logging exception =================")
            println(it.cause)
            println(it.errorCode)
            if (it.cause != null) {
                showAlert?.invoke(it.errorCode.toString() + " " + it.cause?.message)
            } else {
                showAlert?.invoke(it.errorCode.toString())
            }
            println("====================================================================")
        }

        val communicationTokenRefreshOptions =
            CommunicationTokenRefreshOptions(tokenRefresher, true)
        val communicationTokenCredential =
            CommunicationTokenCredential(communicationTokenRefreshOptions)

        if (groupId != null) {
            val groupCallOptions = GroupCallOptions(
                context,
                communicationTokenCredential,
                groupId,
                displayName,
            )
            callComposite.launch(groupCallOptions)
        } else if (!meetingLink.isNullOrBlank()) {
            val teamsMeetingOptions = TeamsMeetingOptions(
                context,
                communicationTokenCredential,
                meetingLink,
                displayName,
            )
            callComposite.launch(teamsMeetingOptions)
        }
    }
}
