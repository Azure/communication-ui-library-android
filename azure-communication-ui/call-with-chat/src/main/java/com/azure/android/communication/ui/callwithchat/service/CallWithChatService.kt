// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callwithchat.service

import android.content.Context
import com.azure.android.communication.ui.calling.CallComposite
import com.azure.android.communication.ui.calling.models.CallCompositeGroupCallLocator
import com.azure.android.communication.ui.calling.models.CallCompositeLocalOptions
import com.azure.android.communication.ui.calling.models.CallCompositeRemoteOptions
import com.azure.android.communication.ui.calling.models.CallCompositeTeamsMeetingLinkLocator
import com.azure.android.communication.ui.callwithchat.CallWithChatCompositeException
import com.azure.android.communication.ui.callwithchat.models.CallWithChatCompositeCallAndChatLocator
import com.azure.android.communication.ui.callwithchat.models.CallWithChatCompositeLocalOptions
import com.azure.android.communication.ui.callwithchat.models.CallWithChatCompositeRemoteOptions
import com.azure.android.communication.ui.callwithchat.models.CallWithChatCompositeTeamsMeetingLinkLocator
import com.azure.android.communication.ui.chat.ChatAdapter
import com.azure.android.communication.ui.chat.implementation.ChatServiceConfigurationImpl

internal class CallWithChatService(
    private val callComposite: CallComposite,
    private val chatAdapter: ChatAdapter,
) {
    fun launch(
        context: Context,
        remoteOptions: CallWithChatCompositeRemoteOptions,
        localOptions: CallWithChatCompositeLocalOptions?
    ) {
        ChatServiceConfigurationImpl.usePolling = true

        callComposite.launch(context, getCallRemoteOptions(remoteOptions), getCallLocalOptions(localOptions))
    }

    private fun getCallRemoteOptions(remoteOptions: CallWithChatCompositeRemoteOptions): CallCompositeRemoteOptions {
        val callLocator = when (remoteOptions.locator) {
            is CallWithChatCompositeCallAndChatLocator -> {
                val groupId = (remoteOptions.locator as CallWithChatCompositeCallAndChatLocator).groupId
                CallCompositeGroupCallLocator(groupId)
            }
            is CallWithChatCompositeTeamsMeetingLinkLocator -> {
                val meetingLink = (remoteOptions.locator as CallWithChatCompositeTeamsMeetingLinkLocator).meetingLink
                CallCompositeTeamsMeetingLinkLocator(meetingLink)
            }
            else -> throw CallWithChatCompositeException("Not supported CallWithChatCompositeJoinLocator type")
        }
        return CallCompositeRemoteOptions(callLocator, remoteOptions.credential, remoteOptions.displayName)
    }

    private fun getCallLocalOptions(localOptions: CallWithChatCompositeLocalOptions?): CallCompositeLocalOptions? {
        return null
    }
}
