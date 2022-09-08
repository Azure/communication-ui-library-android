// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chatdemoapp.launcher

import com.azure.android.communication.ui.chat.ChatComposite
import com.azure.android.communication.ui.chat.ChatCompositeBuilder
import com.azure.android.communication.ui.chatdemoapp.ChatLauncherActivity

class ChatCompositeKotlinLauncher : ChatCompositeLauncher {
    override fun launch(chatLauncherActivity: ChatLauncherActivity) {
        val chatComposite: ChatComposite = ChatCompositeBuilder().build()
        chatComposite.launch(chatLauncherActivity, null, null)
    }
}
