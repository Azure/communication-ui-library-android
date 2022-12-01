// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.azure.android.communication.ui.chat.ChatUIClient
import com.azure.android.communication.ui.chat.presentation.ui.container.ChatCompositeViewImpl

internal class ChatCompositeActivityImpl : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ChatCompositeViewImpl(this, chatUIClient!!, true))
        actionBar?.hide()
        supportActionBar?.hide()
    }

    companion object {
        var chatUIClient: ChatUIClient? = null
    }
}
