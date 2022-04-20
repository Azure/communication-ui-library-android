// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callingcompositedemoapp

import android.graphics.BitmapFactory
import com.azure.android.communication.ui.CallComposite
import com.azure.android.communication.ui.CallingEventHandler
import com.azure.android.communication.ui.configuration.events.CommunicationUIRemoteParticipantJoinedEvent
import com.azure.android.communication.ui.persona.CommunicationUIPersonaData
import java.net.URL

class CallLauncherActivityRemoteParticipantJoinedHandler(
    private val callComposite: CallComposite,
) :
    CallingEventHandler<CommunicationUIRemoteParticipantJoinedEvent> {

    override fun handle(it: CommunicationUIRemoteParticipantJoinedEvent) {
        Thread {
            val url =
                URL("https://dt2sdf0db8zob.cloudfront.net/wp-content/uploads/2019/12/9-Best-Online-Avatars-and-How-to-Make-Your-Own-for-Free-image1-5.png")
            val bitMap = BitmapFactory.decodeStream(url.openConnection().getInputStream())
            callComposite.setRemoteParticipantPersonaData(
                it.communicationIdentifier,
                CommunicationUIPersonaData(bitMap)
            )
        }.start()
    }
}
