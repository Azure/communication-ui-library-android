// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.presentation

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.azure.android.communication.ui.chat.presentation.ui.container.ChatView

class ChatCompositeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChatView(this)
        }
    }

    companion object {
        const val KEY_INSTANCE_ID = "InstanceID"
    }
}
