// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callingcompositedemoapp

import android.content.Context
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.azure.android.communication.ui.callingcompositedemoapp.databinding.ActivityIncomingCallBinding

class IncomingCallActivity : AppCompatActivity() {
    private lateinit var binding: ActivityIncomingCallBinding
    private val sharedPreference by lazy {
        getSharedPreferences(SETTINGS_SHARED_PREFS, Context.MODE_PRIVATE)
    }

    companion object {
        const val DISPLAY_NAME = "DisplayName"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_incoming_call)
        binding = ActivityIncomingCallBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.addFlags(
            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                or WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        )
        val context = this
        binding.run {
            accept.setOnClickListener {
                val application = application as CallLauncherApplication
                val acsIdentityToken = sharedPreference.getString(CACHED_TOKEN, "")
                val displayName = sharedPreference.getString(CACHED_USER_NAME, "")
                application.getCallCompositeManager(context).acceptIncomingCall(this@IncomingCallActivity, acsIdentityToken!!, displayName!!)
                finish()
            }
            decline.setOnClickListener {
                val application = application as CallLauncherApplication
                application.getCallCompositeManager(context).declineIncomingCall()
                finish()
            }

            intent.getStringExtra(DISPLAY_NAME)?.let {
                profileName.text = it
            }
        }

        supportActionBar?.hide()
    }
}
