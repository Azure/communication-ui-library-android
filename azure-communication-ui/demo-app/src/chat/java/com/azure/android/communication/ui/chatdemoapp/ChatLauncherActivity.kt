// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chatdemoapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.webkit.URLUtil
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.azure.android.communication.ui.callingcompositedemoapp.BuildConfig
import com.azure.android.communication.ui.callingcompositedemoapp.R
import com.azure.android.communication.ui.callingcompositedemoapp.databinding.ActivityChatLauncherBinding
import com.azure.android.communication.ui.chatdemoapp.features.AdditionalFeatures
import com.azure.android.communication.ui.chatdemoapp.features.FeatureFlags
import com.azure.android.communication.ui.chatdemoapp.features.conditionallyRegisterDiagnostics
import com.azure.android.communication.ui.chatdemoapp.launcher.ChatCompositeLauncher
import com.azure.android.communication.ui.chatdemoapp.launcher.TeamsUrlParser
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.crashes.Crashes
import com.microsoft.appcenter.distribute.Distribute
import com.microsoft.appcenter.distribute.UpdateTrack

class ChatLauncherActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatLauncherBinding

    private val chatLauncherViewModel: ChatLauncherViewModel by viewModels()
    private val isKotlinLauncherOptionSelected: String = "isKotlinLauncherOptionSelected"

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

        if (savedInstanceState != null) {
            if (savedInstanceState.getBoolean(isKotlinLauncherOptionSelected)) {
                chatLauncherViewModel.setKotlinLauncher()
            } else {
                chatLauncherViewModel.setJavaLauncher()
            }
        }

        val data: Uri? = intent?.data
        val endpointurl = data?.getQueryParameter("endpointurl")
        val threadid = data?.getQueryParameter("threadid")
        val acstoken = data?.getQueryParameter("acstoken")
        val userid = data?.getQueryParameter("userid")
        val name = data?.getQueryParameter("name")

        binding.run {

            if (endpointurl.isNullOrEmpty()) {
                endPointURL.setText(BuildConfig.END_POINT_URL)
            } else {
                endPointURL.setText(endpointurl)
            }

            if (acstoken.isNullOrEmpty()) {
                acsTokenText.setText(BuildConfig.ACS_TOKEN)
            } else {
                acsTokenText.setText(acstoken)
            }

            if (name.isNullOrEmpty()) {
                userNameText.setText(BuildConfig.USER_NAME)
            } else {
                userNameText.setText(name)
            }

            if (threadid.isNullOrEmpty()) {
                chatThreadID.setText(BuildConfig.THREAD_ID)
            } else {
                chatThreadID.setText(threadid)
            }

            if (userid.isNullOrEmpty()) {
                identity.setText(BuildConfig.IDENTITY)
            } else {
                identity.setText(userid)
            }

            launchButton.setOnClickListener {
                chatLauncherViewModel.doLaunch(
                    acsTokenText.text.toString()
                )
            }

            acsTokenText.requestFocus()
            acsTokenText.isEnabled = true

            javaButton.setOnClickListener {
                chatLauncherViewModel.setJavaLauncher()
            }

            kotlinButton.setOnClickListener {
                chatLauncherViewModel.setKotlinLauncher()
            }

            if (!BuildConfig.DEBUG) {
                versionText.text = "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"
            }
        }

        chatLauncherViewModel.fetchResult.observe(this) {
            processResult(it)
        }
    }

    override fun onDestroy() {
        chatLauncherViewModel.destroy()
        super.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        saveState(outState)
        super.onSaveInstanceState(outState)
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

    private fun processResult(result: Result<ChatCompositeLauncher?>) {
        if (result.isFailure) {
            result.exceptionOrNull()?.let {
                if (it.message != null) {
                    val causeMessage = it.cause?.message ?: ""
                    showAlert(it.toString() + causeMessage)
                    binding.launchButton.isEnabled = true
                } else {
                    showAlert("Unknown error")
                }
            }
        }
        if (result.isSuccess) {
            result.getOrNull()?.let { launcherObject ->
                launch(launcherObject)
                binding.launchButton.isEnabled = true
            }
        }
    }

    private fun launch(launcher: ChatCompositeLauncher) {
        val inputChatJoinId = binding.chatThreadID.text.toString()
        val threadId = if (URLUtil.isValidUrl(inputChatJoinId))
            TeamsUrlParser.getThreadId(inputChatJoinId)
        else inputChatJoinId

        launcher.launch(
            this@ChatLauncherActivity,
            threadId,
            binding.endPointURL.text.toString(),
            binding.userNameText.text.toString(),
            binding.identity.text.toString()
        )
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

    private fun saveState(outState: Bundle?) {
        outState?.putBoolean(isKotlinLauncherOptionSelected, chatLauncherViewModel.isKotlinLauncher)
    }
}
