// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callingcompositedemoapp

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import com.azure.android.communication.common.CommunicationIdentifier
import com.azure.android.communication.common.CommunicationUserIdentifier
import com.azure.android.communication.common.MicrosoftTeamsUserIdentifier
import com.azure.android.communication.common.PhoneNumberIdentifier
import com.azure.android.communication.common.UnknownIdentifier
import com.azure.android.communication.ui.calling.CallComposite
import com.azure.android.communication.ui.calling.CallCompositeEventHandler
import com.azure.android.communication.ui.calling.models.CallCompositeParticipantViewData
import com.azure.android.communication.ui.calling.models.CallCompositeRemoteParticipantJoinedEvent
import com.azure.android.communication.ui.calling.models.CallCompositeSetParticipantViewDataResult
import com.azure.android.communication.ui.callingcompositedemoapp.features.SettingsFeatures
import java.net.URL

class RemoteParticipantJoinedHandler(
    private val callComposite: CallComposite,
    private val context: Context,
) :
    CallCompositeEventHandler<CallCompositeRemoteParticipantJoinedEvent> {
    private val tag = "RemoteParticipantJoined"

    override fun handle(event: CallCompositeRemoteParticipantJoinedEvent) {
        event.identifiers.forEach { communicationIdentifier ->
            if (context.resources.getBoolean(R.bool.remote_url_persona_injection)) {
                getImageFromServer(communicationIdentifier)
            } else {
                selectRandomAvatar(communicationIdentifier)
            }
        }
    }

    // This code is for testing purpose
    // The avatar images can be hosted anywhere as per requirements
    // For testing purpose, the images are hosted on Azure Storage in .png format
    private fun getImageFromServer(
        communicationIdentifier: CommunicationIdentifier,
    ) {

        val imageTestUrl = BuildConfig.REMOTE_PARTICIPANT_AVATAR_TEST_URL
        if (imageTestUrl.isNotEmpty()) {
            Thread {
                try {
                    val id = getRemoteParticipantId(communicationIdentifier)
                        .filterNot { it == ":"[0] || it == "-"[0] }
                    val url = URL("$imageTestUrl$id.png")
                    val bitMap = BitmapFactory.decodeStream(url.openConnection().getInputStream())

                    setRemoteParticipantViewData(
                        communicationIdentifier,
                        CallCompositeParticipantViewData().setAvatarBitmap(bitMap),
                    )

                } catch (e: Exception) {
                }
            }.start()
        }
    }

    private fun selectRandomAvatar(communicationIdentifier: CommunicationIdentifier) {
        Thread {
            try {
                getImage(communicationIdentifier)?.let { imageResourceId ->
                    val bitMap =
                        BitmapFactory.decodeResource(context.resources, imageResourceId)

                    val participantViewData = CallCompositeParticipantViewData()
                        .setAvatarBitmap(bitMap)

                    if (SettingsFeatures.getInjectionDisplayNameRemoteParticipantSelection()) {
                        val displayName = context.resources.getResourceEntryName(
                            imageResourceId
                        )
                        participantViewData.setDisplayName(displayName)
                    }

                    setRemoteParticipantViewData(communicationIdentifier, participantViewData)
                }

            } catch (e: Exception) {
            }
        }.start()
    }

    private fun setRemoteParticipantViewData(
        communicationIdentifier: CommunicationIdentifier,
        participantViewData: CallCompositeParticipantViewData,
    ) {
        val result = callComposite.setRemoteParticipantViewData(
            communicationIdentifier,
            participantViewData,
        )

        if (result == CallCompositeSetParticipantViewDataResult.PARTICIPANT_NOT_IN_CALL) {
            // participant not in call
            Log.i(tag, "setRemoteParticipantViewData: PARTICIPANT_NOT_IN_CALL")
        }

        if (result == CallCompositeSetParticipantViewDataResult.SUCCESS) {
            // success
            Log.i(tag, "setRemoteParticipantViewData SUCCESS")
        }
    }

    private fun getImage(communicationIdentifier: CommunicationIdentifier): Int? {
        val id = getRemoteParticipantId(communicationIdentifier)
        val lastChar = id[id.length - 1]

        if (!lastChar.isDigit()) return null

        val number = lastChar.toString().toInt()
        val images = listOf(
            R.drawable.image_cat,
            R.drawable.image_fox,
            R.drawable.image_koala,
            R.drawable.image_monkey,
            R.drawable.image_mouse,
            R.drawable.image_octopus,
            R.drawable.image_cat,
            R.drawable.image_fox,
            R.drawable.image_koala,
            R.drawable.image_monkey,
        )
        return images[number]
    }

    private fun getRemoteParticipantId(identifier: CommunicationIdentifier): String {
        return when (identifier) {
            is PhoneNumberIdentifier -> identifier.phoneNumber
            is MicrosoftTeamsUserIdentifier -> identifier.userId
            is CommunicationUserIdentifier -> identifier.id
            else -> (identifier as UnknownIdentifier).id
        }
    }
}
