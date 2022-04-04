// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callingcompositedemoapp.launcher

import android.graphics.BitmapFactory
import androidx.core.graphics.BitmapCompat
import com.azure.android.communication.common.CommunicationTokenCredential
import com.azure.android.communication.common.CommunicationTokenRefreshOptions
import com.azure.android.communication.ui.CallComposite
import com.azure.android.communication.ui.CallCompositeBuilder
import com.azure.android.communication.ui.GroupCallOptions
import com.azure.android.communication.ui.TeamsMeetingOptions
import com.azure.android.communication.ui.callingcompositedemoapp.CallLauncherActivity
import com.azure.android.communication.ui.callingcompositedemoapp.CallLauncherActivityErrorHandler
import com.azure.android.communication.ui.callingcompositedemoapp.R
import com.azure.android.communication.ui.callingcompositedemoapp.features.AdditionalFeatures
import com.azure.android.communication.ui.configuration.LocalParticipantConfiguration
import com.azure.android.communication.ui.configuration.ThemeConfiguration
import com.azure.android.communication.ui.persona.PersonaData
import com.microsoft.appcenter.utils.HandlerUtils.runOnUiThread
import java.net.URL
import java.util.UUID
import java.util.concurrent.Callable

class CallingCompositeKotlinLauncher(private val tokenRefresher: Callable<String>) :
    CallingCompositeLauncher {

    override fun launch(
        callLauncherActivity: CallLauncherActivity,
        displayName: String,
        groupId: UUID?,
        meetingLink: String?,
        showAlert: ((String) -> Unit)?,
    ) {
        val callComposite: CallComposite =
            if (AdditionalFeatures.secondaryThemeFeature.active)
                CallCompositeBuilder().theme(ThemeConfiguration(R.style.MyCompany_Theme_Calling))
                    .build()
            else
                CallCompositeBuilder().build()

        callComposite.setOnErrorHandler(CallLauncherActivityErrorHandler(callLauncherActivity))

        val communicationTokenRefreshOptions =
            CommunicationTokenRefreshOptions(tokenRefresher, true)
        val communicationTokenCredential =
            CommunicationTokenCredential(communicationTokenRefreshOptions)





        if (groupId != null) {
            val groupCallOptions = GroupCallOptions(
                communicationTokenCredential,
                groupId,
                displayName,
            )


            Thread {
                //Run the long-time operation.
                //val url = URL("https://i.postimg.cc/nrm9SQZQ/20220304-132347.jpg")
                val url = URL("https://dt2sdf0db8zob.cloudfront.net/wp-content/uploads/2019/12/9-Best-Online-Avatars-and-How-to-Make-Your-Own-for-Free-image1-5.png")
                //val url = URL("https://static.vecteezy.com/system/resources/thumbnails/002/002/403/small/man-with-beard-avatar-character-isolated-icon-free-vector.jpg")

                val imageBitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream())

                val width = imageBitmap.width
                val height = imageBitmap.height

                val personaData = PersonaData(imageBitmap)
                val localParticipantConfiguration = LocalParticipantConfiguration(personaData)

                val bitmapByteCount= BitmapCompat.getAllocationByteCount(imageBitmap)


                runOnUiThread {
                    callComposite.launch(
                        callLauncherActivity,
                        groupCallOptions,
                        localParticipantConfiguration
                    )
                }
            }.start()


        } else if (!meetingLink.isNullOrBlank()) {
            val teamsMeetingOptions = TeamsMeetingOptions(
                communicationTokenCredential,
                meetingLink,
                displayName,
            )

            Thread {
                //Run the long-time operation.
                val url =
                    URL("https://dt2sdf0db8zob.cloudfront.net/wp-content/uploads/2019/12/9-Best-Online-Avatars-and-How-to-Make-Your-Own-for-Free-image1-5.png")
                val imageBitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream())
                val personaData = PersonaData(imageBitmap)
                val localParticipantConfiguration = LocalParticipantConfiguration(personaData)

                runOnUiThread {
                    callComposite.launch(
                        callLauncherActivity,
                        teamsMeetingOptions,
                        localParticipantConfiguration
                    )
                }
            }.start()


        }
    }
}
