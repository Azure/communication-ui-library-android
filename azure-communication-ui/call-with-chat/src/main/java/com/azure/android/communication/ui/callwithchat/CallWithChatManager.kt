// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callwithchat

import android.content.Context
import com.azure.android.communication.ui.calling.CallComposite
import com.azure.android.communication.ui.callwithchat.models.CallWithChatCompositeLocalOptions
import com.azure.android.communication.ui.callwithchat.models.CallWithChatCompositeRemoteOptions
import com.azure.android.communication.ui.chat.ChatComposite

internal class CallWithChatManager(
    private val callComposite: CallComposite,
    private val chatComposite: ChatComposite,
) {
    fun launch(
        context: Context,
        remoteOptions: CallWithChatCompositeRemoteOptions,
        localOptions: CallWithChatCompositeLocalOptions?
    ) {
    }
}
