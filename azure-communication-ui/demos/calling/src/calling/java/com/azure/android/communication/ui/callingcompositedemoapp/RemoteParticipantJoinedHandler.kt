// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callingcompositedemoapp

import android.content.Context
import android.graphics.BitmapFactory
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
import java.net.URL

class RemoteParticipantJoinedHandler(
    private val callComposite: CallComposite,
    private val context: Context,
) :
    CallCompositeEventHandler<CallCompositeRemoteParticipantJoinedEvent> {

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
                    val result = callComposite.setRemoteParticipantViewData(
                        communicationIdentifier,
                        CallCompositeParticipantViewData().setAvatarBitmap(bitMap)
                    )

                    if (result == CallCompositeSetParticipantViewDataResult.PARTICIPANT_NOT_IN_CALL) {
                        // participant not in call
                    }

                    if (result == CallCompositeSetParticipantViewDataResult.SUCCESS) {
                        // success
                    }
                } catch (e: Exception) {
                }
            }.start()
        }
    }

    private fun selectRandomAvatar(communicationIdentifier: CommunicationIdentifier) {
        Thread {
            try {
                val id = getRemoteParticipantId(communicationIdentifier)
                val lastChar = id[id.length - 1]

                // get last char of id
                // if string then do nothing
                // if from 0 to 5 pick in order from cat,fox,koala, monkey, mouse, octopus
                if (lastChar.isDigit()) {
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
                    images[number].let {
                        val bitMap =
                            BitmapFactory.decodeResource(context.resources, it)
                        val result = callComposite.setRemoteParticipantViewData(
                            communicationIdentifier,
                            CallCompositeParticipantViewData()
                                .setDisplayName(
                                    context.resources.getResourceEntryName(
                                        it
                                    )
                                )
                                .setAvatarBitmap(bitMap)
                        )

                        if (result == CallCompositeSetParticipantViewDataResult.PARTICIPANT_NOT_IN_CALL) {
                            // participant not in call
                        }

                        if (result == CallCompositeSetParticipantViewDataResult.SUCCESS) {
                            // success
                        }
                    }
                }
            } catch (e: Exception) {
            }
        }.start()
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
