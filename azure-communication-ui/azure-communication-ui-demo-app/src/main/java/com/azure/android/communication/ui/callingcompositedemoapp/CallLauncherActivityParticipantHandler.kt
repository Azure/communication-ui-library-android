// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callingcompositedemoapp

import android.graphics.BitmapFactory
import com.azure.android.communication.ui.AvatarPersonaData
import com.azure.android.communication.ui.persona.CallingParticipantHandler
import java9.util.concurrent.CompletableFuture
import java.lang.ref.WeakReference
import java.net.URL


class CallLauncherActivityParticipantHandler(callLauncherActivity: CallLauncherActivity) :
    CallingParticipantHandler {
    private val activityWr: WeakReference<CallLauncherActivity> = WeakReference(callLauncherActivity)

    override fun onFetchAvatarPersonaData(userIdentifier: String?): AvatarPersonaData {
        var avatarPersonaDataFuture: CompletableFuture<AvatarPersonaData> = CompletableFuture<AvatarPersonaData>()
        Thread {
            val avatarPersonaData = AvatarPersonaData()
            val url = URL("https://dt2sdf0db8zob.cloudfront.net/wp-content/uploads/2019/12/9-Best-Online-Avatars-and-How-to-Make-Your-Own-for-Free-image1-5.png")
            val image = BitmapFactory.decodeStream(url.openConnection().getInputStream())
            avatarPersonaData.avatarImageBitmap = image
            avatarPersonaDataFuture.complete(avatarPersonaData)
        }.start()

        return avatarPersonaDataFuture.get()
    }
}
