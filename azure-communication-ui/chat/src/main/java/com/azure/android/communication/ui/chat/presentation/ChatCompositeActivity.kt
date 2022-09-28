// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.presentation

import android.os.Bundle
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.azure.android.communication.ui.chat.presentation.ui.container.ChatView

class ChatCompositeActivity : AppCompatActivity() {
    private val instanceId get() = intent.extras!!.getInt(KEY_INSTANCE_ID)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addContentView(
            ChatView(this, instanceId),
            ViewGroup.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
        )
    }

    companion object {
        const val KEY_INSTANCE_ID = "InstanceID"
    }
}
