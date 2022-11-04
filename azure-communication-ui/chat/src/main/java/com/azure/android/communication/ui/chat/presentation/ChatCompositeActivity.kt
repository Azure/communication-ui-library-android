// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.presentation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.azure.android.communication.ui.chat.ChatManager
import com.azure.android.communication.ui.chat.ChatThreadManager
import com.azure.android.communication.ui.chat.ChatView
import com.azure.android.communication.ui.chat.locator.ServiceLocator
import java.lang.Exception
import java.lang.RuntimeException

class ChatCompositeActivity : AppCompatActivity() {
    private var instanceID : Long = -1L


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            instanceID = this.intent.extras?.getLong(KEY_INSTANCE)!!
        } catch (exception: Exception) {
            throw IllegalArgumentException("This activity was not started with a Instance ID")
        }
        // hide the existing action bar, it will be replaced with ChatCompositeActionBar
        supportActionBar?.hide()

        val chatView = ChatView(this)

        try {
            chatView.setChatThreadManager(instanceMap[instanceID]!!)
        } catch (exception: Exception) {
            Log.e("ChatCompositeActivity", "Could not find a ChatThreadManager")
            setResult(RESULT_CANCELED)
            finish()
        }

        addContentView(
            chatView,
            ViewGroup.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isFinishing) {
            instanceMap.remove(instanceID)
        }
    }

    companion object {
        private var instanceCounter = 0L
        private const val KEY_INSTANCE = "INSTANCE"

        @JvmStatic
        fun startForChatThread(context: Context, chatThreadManager: ChatThreadManager) {
            val instance = instanceCounter++
            instanceMap[instance] = chatThreadManager
            val intent = Intent(context, ChatCompositeActivity::class.java)
            intent.putExtra(KEY_INSTANCE, instance)
            context.startActivity(intent)
        }

        private val instanceMap = mutableMapOf<Long, ChatThreadManager>()
    }
}
