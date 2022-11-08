// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chatdemoapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.webkit.URLUtil
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.azure.android.communication.common.CommunicationTokenCredential
import com.azure.android.communication.common.CommunicationTokenRefreshOptions
import com.azure.android.communication.ui.callingcompositedemoapp.BuildConfig
import com.azure.android.communication.ui.callingcompositedemoapp.R
import com.azure.android.communication.ui.callingcompositedemoapp.databinding.ActivityChatLauncherBinding
import com.azure.android.communication.ui.chat.ChatCompositeBuilder
import com.azure.android.communication.ui.chat.models.ChatCompositeRemoteOptions
import com.azure.android.communication.ui.chat.presentation.ui.container.ChatCompositeView
import com.azure.android.communication.ui.chatdemoapp.features.AdditionalFeatures
import com.azure.android.communication.ui.chatdemoapp.features.FeatureFlags
import com.azure.android.communication.ui.chatdemoapp.features.conditionallyRegisterDiagnostics
import com.azure.android.communication.ui.chatdemoapp.launcher.TeamsUrlParser
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.crashes.Crashes
import com.microsoft.appcenter.distribute.Distribute
import com.microsoft.appcenter.distribute.UpdateTrack
import java.util.concurrent.Callable

class ChatLauncherActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatLauncherBinding

    private val chatLauncherViewModel: ChatLauncherViewModel by viewModels()

    private var chatView: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (shouldFinish()) {
            finish()
            return
        }
        if (!AppCenter.isConfigured() && !BuildConfig.DEBUG) {
            Distribute.setUpdateTrack(UpdateTrack.PRIVATE)
            AppCenter.start(
                application,
                BuildConfig.APP_SECRET,
                Analytics::class.java,
                Crashes::class.java,
                Distribute::class.java
            )
            Distribute.checkForUpdate()
        }
        // Register Memory Viewer with FeatureFlags
        conditionallyRegisterDiagnostics(this)
        FeatureFlags.registerAdditionalFeature(AdditionalFeatures.secondaryThemeFeature)

        binding = ActivityChatLauncherBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val data: Uri? = intent?.data
        val endpointurl = data?.getQueryParameter("endpointurl") ?: BuildConfig.END_POINT_URL
        val threadid = data?.getQueryParameter("threadid") ?: BuildConfig.THREAD_ID
        val acstoken = data?.getQueryParameter("acstoken") ?: BuildConfig.ACS_TOKEN
        val userid = data?.getQueryParameter("userid") ?: BuildConfig.IDENTITY
        val name = data?.getQueryParameter("name") ?: BuildConfig.USER_NAME

        binding.run {
            endPointURL.setText(endpointurl)
            acsTokenText.setText(acstoken)
            userNameText.setText(name)
            chatThreadID.setText(threadid)
            identity.setText(userid)

            launchButton.setOnClickListener {
                if (chatLauncherViewModel.isChatRunning)
                    openChatUI()
                else
                    launch()
            }

            openChatUIButton.setOnClickListener {
                openChatUI()
            }
            stopChatCompositeButton.setOnClickListener {
                stopChatComposite()
            }

            acsTokenText.requestFocus()
            acsTokenText.isEnabled = true

            if (!BuildConfig.DEBUG) {
                versionText.text = "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"
            }
        }

        this.onBackPressedDispatcher.addCallback {
            if (chatView != null) {
                onChatCompositeExitRequested()
            } else {
                this.handleOnBackPressed()
            }
        }
    }

    // / When a request is made to close the view, lets do that here
    private fun onChatCompositeExitRequested() {
        // Remove chat view from screen
        chatView?.parent?.let {
            (it as ViewGroup).removeView(chatView)
        }
        chatView = null
    }

    // check whether new Activity instance was brought to top of stack,
    // so that finishing this will get us to the last viewed screen
    private fun shouldFinish() = BuildConfig.CHECK_TASK_ROOT && !isTaskRoot

    private fun showAlert(message: String) {
        runOnUiThread {
            val builder = AlertDialog.Builder(this).apply {
                setMessage(message)
                setTitle("Alert")
                setPositiveButton("OK") { _, _ ->
                }
            }
            builder.show()
        }
    }

    private fun getTokenFetcher(): Callable<String>? {
        try {
            val tokenFetcher = chatLauncherViewModel.getTokenFetcher(
                binding.tokenFunctionUrlText.text.toString(),
                binding.acsTokenText.text.toString()
            )
            binding.launchButton.isEnabled = true
            return tokenFetcher
        } catch (ex: Exception) {
            if (ex.message != null) {
                val causeMessage = ex.cause?.message ?: ""
                showAlert(ex.toString() + causeMessage)
                binding.launchButton.isEnabled = true
            } else {
                showAlert("Unknown error")
            }
            return null
        }
    }

    private fun openChatUI() {
        val chatComposite = chatLauncherViewModel.chatComposite!!

        // Create Chat Composite View
        chatView = ChatCompositeView(this, chatComposite)

        // Place it as a child element to any UI I have on the screen
        addContentView(
            chatView,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )

        binding.launchButton.visibility = View.GONE
    }

    private fun launch() {
        val inputChatJoinId = binding.chatThreadID.text.toString()
        val threadId = if (URLUtil.isValidUrl(inputChatJoinId))
            TeamsUrlParser.getThreadId(inputChatJoinId)
        else inputChatJoinId

        val endpoint = binding.endPointURL.text.toString()
        val acsIdentity = binding.identity.text.toString()
        val userName = binding.userNameText.text.toString()

        val tokenRefresher = getTokenFetcher() ?: return

        // Create ChatComposite(Adaptor)
        val communicationTokenRefreshOptions = CommunicationTokenRefreshOptions(tokenRefresher, true)
        val communicationTokenCredential = CommunicationTokenCredential(communicationTokenRefreshOptions)

        val remoteOptions = ChatCompositeRemoteOptions(
            endpoint,
            threadId,
            communicationTokenCredential,
            acsIdentity,
            userName
        )

        val chatComposite = ChatCompositeBuilder()
            .context(this)
            .remoteOptions(remoteOptions)
            .build()

        chatComposite.addOnErrorEventHandler { eventArgs ->
            Log.e("", "Error received from ChatComposite ${eventArgs.errorCode}")
        }
        chatComposite.addOnUnreadMessagesChangedEventHandler { eventArgs ->
            Log.d("", "There is a '${eventArgs.count}' new messages.")
        }

        chatComposite.connect()
        chatLauncherViewModel.chatComposite = chatComposite

        openChatUI()

        binding.run {
            launchButton.visibility = View.GONE
            openChatUIButton.visibility = View.VISIBLE
            stopChatCompositeButton.visibility = View.VISIBLE
        }
    }

    fun stopChatComposite() {
        chatLauncherViewModel.closeChatComposite()
        binding.launchButton.visibility = View.VISIBLE
        binding.openChatUIButton.visibility = View.GONE
        binding.stopChatCompositeButton.visibility = View.GONE
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.launcher_activity_action_bar, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.azure_composite_show_settings -> {
            val settingIntent = Intent(this, SettingsActivity::class.java)
            startActivity(settingIntent)
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
}
