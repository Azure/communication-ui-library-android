// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callwithchat

import android.content.Context
import com.azure.android.communication.ui.calling.CallComposite
import com.azure.android.communication.ui.callwithchat.models.CallWithChatCompositeLocalOptions
import com.azure.android.communication.ui.callwithchat.models.CallWithChatCompositeRemoteOptions
import com.azure.android.communication.ui.chat.ChatComposite
// import com.azure.android.communication.ui.chat.implementation.ChatServiceConfigurationImpl
// import com.azure.android.communication.ui.chat.models.ChatCompositeJoinLocator
// import com.azure.android.communication.ui.chat.models.ChatCompositeLocalOptions
// import com.azure.android.communication.ui.chat.models.ChatCompositeParticipantViewData
// import com.azure.android.communication.ui.chat.models.ChatCompositeRemoteOptions

internal class CallWithChatManager(
    private val callComposite: CallComposite,
    private val chatComposite: ChatComposite,
) {
    fun launch(
        context: Context,
        remoteOptions: CallWithChatCompositeRemoteOptions,
        localOptions: CallWithChatCompositeLocalOptions?
    ) {
//        ChatServiceConfigurationImpl.usePolling = true
//
//        callingIntegrationBridge.addOnCallStaredEventHandler {
//            chatComposite.launch(context, getChatRemoteOptions(remoteOptions), getChatLocalOptions(localOptions))
//        }
//
//        chatComposite.addOnViewClosedEventHandler {
//            callingIntegrationBridge.removeOverlay()
//        }
//
//        callComposite.launch(context, getCallRemoteOptions(remoteOptions))
    }

//    private fun getCallRemoteOptions(remoteOptions: CallWithChatCompositeRemoteOptions): CallCompositeRemoteOptions {
//        val callLocator = if (remoteOptions.locator is CallWithChatCompositeCallAndChatLocator) {
//            val groupId = (remoteOptions.locator as CallWithChatCompositeCallAndChatLocator).groupId
//            CallCompositeGroupCallLocator(groupId)
//        } else {
//            val meetingLink = (remoteOptions.locator as CallWithChatCompositeTeamsMeetingLinkLocator).meetingLink
//            CallCompositeTeamsMeetingLinkLocator(meetingLink)
//        }
//        return CallCompositeRemoteOptions(callLocator, remoteOptions.credential, remoteOptions.displayName)
//    }
//
//    private fun getChatRemoteOptions(remoteOptions: CallWithChatCompositeRemoteOptions): ChatCompositeRemoteOptions {
//        val threadId =
//            if (remoteOptions.locator is CallWithChatCompositeCallAndChatLocator) {
//                (remoteOptions.locator as CallWithChatCompositeCallAndChatLocator).chatThreadId
//            } else {
//                val locator = remoteOptions.locator as CallWithChatCompositeTeamsMeetingLinkLocator
//                TeamsUrlParser.getThreadId(locator.meetingLink)
//            }
//
//        val chatLocator = ChatCompositeJoinLocator(remoteOptions.locator.endpoint, threadId)
//        return ChatCompositeRemoteOptions(
//            chatLocator,
//            remoteOptions.communicationIdentifier,
//            remoteOptions.credential,
//            remoteOptions.displayName,
//            "", "", ""
//        )
//    }

//    private fun getChatLocalOptions(localOptions: CallWithChatCompositeLocalOptions?): ChatCompositeLocalOptions {
//        val participantViewData = ChatCompositeParticipantViewData()
//        participantViewData.avatarBitmap = localOptions?.participantViewData?.avatarBitmap
//
//        return ChatCompositeLocalOptions(participantViewData).also { it.isBackgroundMode = true }
//    }
}
