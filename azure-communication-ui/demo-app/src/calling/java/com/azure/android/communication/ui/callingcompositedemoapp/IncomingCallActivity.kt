// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callingcompositedemoapp

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.azure.android.communication.ui.callingcompositedemoapp.databinding.ActivityIncomingCallBinding

class IncomingCallActivity : AppCompatActivity() {
    private lateinit var binding: ActivityIncomingCallBinding
    private val callLauncherViewModel: CallLauncherViewModel by viewModels()

    companion object {
        const val DISPLAY_NAME = "DisplayName"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_incoming_call)

        binding = ActivityIncomingCallBinding.inflate(layoutInflater)
        setContentView(binding.root)

        CallCompositeManager.initialize()

        window.addFlags(
            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                or WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        )

        binding.run {
            accept.setOnClickListener {
                callLauncherViewModel.acceptIncomingCall(this@IncomingCallActivity)
                finish()
            }
            decline.setOnClickListener {
                CallCompositeManager.getInstance().declineIncomingCall(this@IncomingCallActivity)
                finish()
            }

            intent.getStringExtra(DISPLAY_NAME)?.let {
                profileName.text = it
            }
        }

        supportActionBar?.hide()
    }
}
