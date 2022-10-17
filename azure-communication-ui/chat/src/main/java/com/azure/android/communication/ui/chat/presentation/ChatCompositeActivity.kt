// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.presentation

import android.os.Bundle
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.azure.android.communication.ui.chat.ChatComposite
import com.azure.android.communication.ui.chat.ChatContainer
import com.azure.android.communication.ui.chat.locator.ServiceLocator
import com.azure.android.communication.ui.chat.models.ChatCompositeLocalOptions
import com.azure.android.communication.ui.chat.presentation.ui.container.ChatView

class ChatCompositeActivity : AppCompatActivity() {
    private val instanceId get() = intent.extras!!.getInt(KEY_INSTANCE_ID)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // hide the existing action bar, it will be replaced with ChatCompositeActionBar
        supportActionBar?.hide()
        addContentView(
            ChatView(this, instanceId),
            ViewGroup.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        val chatContainer: ChatComposite = ServiceLocator.getInstance(instanceId).locate()
        chatContainer.stop()
    }

    companion object {
        const val KEY_INSTANCE_ID = "ChatCompositeActivity.InstanceID"
    }
}
