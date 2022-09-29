// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.presentation

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.azure.android.communication.ui.chat.presentation.ui.chat.screens.ChattingScreen
import com.azure.android.communication.ui.chat.presentation.ui.container.ContainerView

class ChatCompositeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // hide the existing action bar, it will be replaced with ChatCompositeActionBar
        supportActionBar?.hide()
        setContentView(
            ContainerView(this).apply {
                setContent {
                    ChattingScreen()
                }
            }
        )
    }

    companion object {
        const val KEY_INSTANCE_ID = "InstanceID"
    }
}
