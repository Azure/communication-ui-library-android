// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callingcompositedemoapp

import android.graphics.BitmapFactory
import com.azure.android.communication.common.CommunicationIdentifier
import com.azure.android.communication.ui.PersonaData
import com.azure.android.communication.ui.participant.remote.RemoteParticipantJoinedHandler
import com.azure.android.communication.ui.participant.remote.RemoteParticipantManager
import java.lang.ref.WeakReference
import java.net.URL

class CallLauncherActivityRemoteParticipantJoinedHandler(callLauncherActivity: CallLauncherActivity) :
    RemoteParticipantJoinedHandler {
    private val activityWr: WeakReference<CallLauncherActivity> =
        WeakReference(callLauncherActivity)

    override fun handle(
        communicationIdentifier: CommunicationIdentifier,
        remoteParticipantManager: RemoteParticipantManager,
    ) {
        Thread {
            val avatarPersonaData =
                PersonaData()
            val url =
                URL("https://dt2sdf0db8zob.cloudfront.net/wp-content/uploads/2019/12/9-Best-Online-Avatars-and-How-to-Make-Your-Own-for-Free-image1-5.png")
            val image = BitmapFactory.decodeStream(url.openConnection().getInputStream())
            avatarPersonaData.avatarImageBitmap = image
            remoteParticipantManager.setPersonaData(
                communicationIdentifier,
                avatarPersonaData
            )
        }.start()
    }
}
