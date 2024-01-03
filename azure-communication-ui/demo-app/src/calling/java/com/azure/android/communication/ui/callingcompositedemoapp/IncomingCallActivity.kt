// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callingcompositedemoapp

import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.azure.android.communication.ui.callingcompositedemoapp.databinding.ActivityIncomingCallBinding

class IncomingCallActivity : AppCompatActivity() {
    private lateinit var binding: ActivityIncomingCallBinding

    companion object {
        const val DISPLAY_NAME = "DisplayName"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_incoming_call)
        val application = application as CallLauncherApplication

        binding = ActivityIncomingCallBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.addFlags(
            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                or WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        )

        binding.run {
            accept.setOnClickListener {
                application.callCompositeManager?.acceptIncomingCall(this@IncomingCallActivity)
                finish()
            }
            decline.setOnClickListener {
                application.callCompositeManager?.declineIncomingCall()
                finish()
            }

            intent.getStringExtra(DISPLAY_NAME)?.let {
                profileName.text = it
            }
        }

        supportActionBar?.hide()
    }
}
