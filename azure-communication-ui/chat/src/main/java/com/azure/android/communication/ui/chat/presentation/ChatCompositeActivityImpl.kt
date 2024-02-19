// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.azure.android.communication.ui.chat.ChatAdapter
import com.azure.android.communication.ui.chat.presentation.ui.container.ChatCompositeViewImpl

internal class ChatCompositeActivityImpl : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ChatCompositeViewImpl(this, chatAdapter!!, true))
        actionBar?.hide()
        supportActionBar?.hide()
    }

    companion object {
        var chatAdapter: ChatAdapter? = null
    }
}
