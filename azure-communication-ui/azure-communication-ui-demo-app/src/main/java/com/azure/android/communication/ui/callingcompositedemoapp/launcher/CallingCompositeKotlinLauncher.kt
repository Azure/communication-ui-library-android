// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callingcompositedemoapp.launcher

import com.azure.android.communication.common.CommunicationTokenCredential
import com.azure.android.communication.common.CommunicationTokenRefreshOptions
import com.azure.android.communication.ui.CallComposite
import com.azure.android.communication.ui.CallCompositeBuilder
import com.azure.android.communication.ui.GroupCallOptions
import com.azure.android.communication.ui.TeamsMeetingOptions
import com.azure.android.communication.ui.callingcompositedemoapp.MainActivity
import com.azure.android.communication.ui.callingcompositedemoapp.MainActivityErrorHandler
import java.util.UUID
import java.util.concurrent.Callable

class CallingCompositeKotlinLauncher(private val tokenRefresher: Callable<String>) :
    CallingCompositeLauncher {

    override fun launch(
        context: MainActivity,
        displayName: String,
        groupId: UUID?,
        meetingLink: String?,
        showAlert: ((String) -> Unit)?,
    ) {
        val callComposite: CallComposite = CallCompositeBuilder().build()

        callComposite.setOnErrorHandler(MainActivityErrorHandler(context))

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
