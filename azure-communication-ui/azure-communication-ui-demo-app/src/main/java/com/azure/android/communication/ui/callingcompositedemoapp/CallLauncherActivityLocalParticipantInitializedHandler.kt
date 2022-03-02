// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callingcompositedemoapp

import android.graphics.BitmapFactory
import com.azure.android.communication.ui.AvatarData
import com.azure.android.communication.ui.participant.local.LocalParticipantInitializedHandler
import com.azure.android.communication.ui.participant.local.LocalParticipantManager
import java.lang.ref.WeakReference
import java.net.URL

class CallLauncherActivityLocalParticipantInitializedHandler(callLauncherActivity: CallLauncherActivity) :
    LocalParticipantInitializedHandler {
    private val activityWr: WeakReference<CallLauncherActivity> =
        WeakReference(callLauncherActivity)

    override fun handle(localParticipantManager: LocalParticipantManager) {
        Thread {
            val avatarPersonaData =
                AvatarData()
            val url =
                URL("https://dt2sdf0db8zob.cloudfront.net/wp-content/uploads/2019/12/9-Best-Online-Avatars-and-How-to-Make-Your-Own-for-Free-image1-5.png")
            val image = BitmapFactory.decodeStream(url.openConnection().getInputStream())
            avatarPersonaData.avatarImageBitmap = image

            // val a: AvatarPersonaData = localParticipantManager.localParticipantAvatar
            localParticipantManager.avatar = avatarPersonaData
        }.start()
    }
}
