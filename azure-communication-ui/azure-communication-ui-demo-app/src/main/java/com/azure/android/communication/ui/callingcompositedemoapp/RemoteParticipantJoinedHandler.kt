// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callingcompositedemoapp

import android.graphics.BitmapFactory
import com.azure.android.communication.common.CommunicationIdentifier
import com.azure.android.communication.common.CommunicationUserIdentifier
import com.azure.android.communication.common.MicrosoftTeamsUserIdentifier
import com.azure.android.communication.common.PhoneNumberIdentifier
import com.azure.android.communication.common.UnknownIdentifier
import com.azure.android.communication.ui.calling.CallComposite
import com.azure.android.communication.ui.calling.CallingEventHandler
import com.azure.android.communication.ui.calling.configuration.events.CommunicationUIRemoteParticipantJoinedEvent
import com.azure.android.communication.ui.calling.persona.PersonaData
import com.azure.android.communication.ui.calling.persona.SetPersonaDataResult
import java.net.URL

class RemoteParticipantJoinedHandler(
    private val callComposite: CallComposite,
) :
    CallingEventHandler<CommunicationUIRemoteParticipantJoinedEvent> {

    override fun handle(event: CommunicationUIRemoteParticipantJoinedEvent) {
        // This code is for testing purpose
        // The avatar images can be hosted anywhere as per requirements
        // For testing purpose, the images are hosted on Azure Storage in .png format
        val imageTestUrl = BuildConfig.REMOTE_PARTICIPANT_AVATAR_TEST_URL
        if (imageTestUrl.isNotEmpty()) {
            event.identifiers.forEach {
                Thread {
                    try {
                        val id =
                            getRemoteParticipantId(it).filterNot { it == ":"[0] || it == "-"[0] }
                        val url =
                            URL("$imageTestUrl$id.png")
                        val bitMap =
                            BitmapFactory.decodeStream(url.openConnection().getInputStream())
                        val result = callComposite.setRemoteParticipantPersonaData(
                            it,
                            PersonaData(
                                bitMap
                            )
                        )

                        if (result == SetPersonaDataResult.PARTICIPANT_NOT_IN_CALL) {
                            // participant not in call
                        }

                        if (result == SetPersonaDataResult.SUCCESS) {
                            // success
                        }
                    } catch (e: Exception) {
                    }
                }.start()
            }
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
