// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callingcompositedemoapp

import android.graphics.BitmapFactory
import com.azure.android.communication.common.CommunicationIdentifier
import com.azure.android.communication.common.CommunicationUserIdentifier
import com.azure.android.communication.common.MicrosoftTeamsUserIdentifier
import com.azure.android.communication.common.PhoneNumberIdentifier
import com.azure.android.communication.common.UnknownIdentifier
import com.azure.android.communication.ui.CallComposite
import com.azure.android.communication.ui.CallingEventHandler
import com.azure.android.communication.ui.configuration.events.CommunicationUIRemoteParticipantJoinedEvent
import com.azure.android.communication.ui.persona.CommunicationUIPersonaData
import java.net.URL

class CallLauncherActivityRemoteParticipantJoinedHandler(
    private val callComposite: CallComposite,
) :
    CallingEventHandler<CommunicationUIRemoteParticipantJoinedEvent> {

    override fun handle(event: CommunicationUIRemoteParticipantJoinedEvent) {
        // This code is for testing purpose
        // The avatar images can be hosted anywhere as per requirements
        // For testing purpose, the images are hosted on Azure Storage in .png format
        val imageTestUrl = BuildConfig.REMOTE_PARTICIPANT_AVATAR_TEST_URL
        if (imageTestUrl.isNotEmpty()) {
            Thread {
                try {
                    val id =
                        getRemoteParticipantId(event.communicationIdentifier).filterNot { it == ":"[0] || it == "-"[0] }
                    val url =
                        URL("$imageTestUrl$id.png")
                    val bitMap = BitmapFactory.decodeStream(url.openConnection().getInputStream())
                    callComposite.setRemoteParticipantPersonaData(
                        event.communicationIdentifier,
                        CommunicationUIPersonaData(bitMap)
                    )
                } catch (e: Exception) {
                }
            }.start()
        }
    }

    private fun getRemoteParticipantId(identifier: CommunicationIdentifier): String {
        return when (identifier) {
            is PhoneNumberIdentifier -> {
                identifier.phoneNumber
            }
            is MicrosoftTeamsUserIdentifier -> {
                identifier.userId
            }
            is CommunicationUserIdentifier -> {
                identifier.id
            }
            else -> {
                (identifier as UnknownIdentifier).id
            }
        }
    }
}
