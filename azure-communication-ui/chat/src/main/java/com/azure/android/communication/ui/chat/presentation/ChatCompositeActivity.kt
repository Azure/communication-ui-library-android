// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.presentation

import android.os.Bundle
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.azure.android.communication.ui.chat.ChatComposite
import com.azure.android.communication.ui.chat.locator.ServiceLocator

class ChatCompositeActivity : AppCompatActivity() {
    private val instanceId get() = intent.extras!!.getInt(KEY_INSTANCE_ID)
    private val chatComposite: ChatComposite by lazy { ServiceLocator.getInstance(instanceId).locate() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // hide the existing action bar, it will be replaced with ChatCompositeActionBar
        supportActionBar?.hide()
        addContentView(
            chatComposite.getCompositeUIView(this),
            ViewGroup.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isFinishing) {
            chatComposite.stop()
        }
    }

    companion object {
        const val KEY_INSTANCE_ID = "ChatCompositeActivity.InstanceID"
        const val UNKNOWN_USER_NAME = "unknown"
    }
}
