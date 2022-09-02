// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callingcompositedemoapp.launcher

import com.azure.android.communication.ui.callingcompositedemoapp.CallLauncherActivity
import com.azure.android.communication.ui.chat.ChatComposite
import com.azure.android.communication.ui.chat.ChatCompositeBuilder

class ChatCompositeKotlinLauncher : ChatCompositeLauncher {

    override fun launch(callLauncherActivity: CallLauncherActivity) {

        val chatComposite: ChatComposite = ChatCompositeBuilder().build()
        chatComposite.launch(callLauncherActivity)
    }
}
