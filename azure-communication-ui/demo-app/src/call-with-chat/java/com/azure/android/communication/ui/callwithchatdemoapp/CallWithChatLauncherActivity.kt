// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callwithchatdemoapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.azure.android.communication.ui.callingcompositedemoapp.BuildConfig
import com.azure.android.communication.ui.callingcompositedemoapp.R
import com.azure.android.communication.ui.callingcompositedemoapp.databinding.ActivityCallwithchatLauncherBinding
import com.azure.android.communication.ui.callwithchatdemoapp.features.conditionallyRegisterDiagnostics
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.crashes.Crashes
import com.microsoft.appcenter.distribute.Distribute
import java.util.UUID

class CallWithChatLauncherActivity : AppCompatActivity(), AlertHandler {
    private lateinit var binding: ActivityCallwithchatLauncherBinding

    private val callLauncherViewModel: CallLauncherViewModel by viewModels()
    private val isTokenFunctionOptionSelected: String = "isTokenFunctionOptionSelected"
    private val isKotlinLauncherOptionSelected: String = "isKotlinLauncherOptionSelected"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (shouldFinish()) {
            finish()
            return
        }
        if (!AppCenter.isConfigured() && !BuildConfig.DEBUG) {
            AppCenter.start(
                application,
                BuildConfig.APP_SECRET,
                Analytics::class.java,
                Crashes::class.java,
                Distribute::class.java
            )
        }
        // Register Memory Viewer with FeatureFlags
        conditionallyRegisterDiagnostics(this)

        binding = ActivityCallwithchatLauncherBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val data: Uri? = intent?.data
        val deeplinkAcsToken = data?.getQueryParameter("acstoken")
        val deeplinkName = data?.getQueryParameter("name")
        val deeplinkGroupId = data?.getQueryParameter("groupid")
        val deeplinkTeamsUrl = data?.getQueryParameter("teamsurl")

        if (savedInstanceState != null) {
            if (savedInstanceState.getBoolean(isTokenFunctionOptionSelected)) {
                callLauncherViewModel.useTokenFunction()
            } else {
                callLauncherViewModel.useAcsToken()
            }

            if (savedInstanceState.getBoolean(isKotlinLauncherOptionSelected)) {
                callLauncherViewModel.setKotlinLauncher()
            } else {
                callLauncherViewModel.setJavaLauncher()
            }
        }

        binding.run {

            if (!deeplinkAcsToken.isNullOrEmpty()) {
                acsTokenText.setText(deeplinkAcsToken)
            } else {
                acsTokenText.setText(BuildConfig.ACS_TOKEN)
            }

            if (!deeplinkName.isNullOrEmpty()) {
                userNameText.setText(deeplinkName)
            } else {
                userNameText.setText(BuildConfig.USER_NAME)
            }

            if (!deeplinkGroupId.isNullOrEmpty()) {
                groupIdOrTeamsMeetingLinkText.setText(deeplinkGroupId)
                groupCallRadioButton.isChecked = true
                teamsMeetingRadioButton.isChecked = false
            } else if (!deeplinkTeamsUrl.isNullOrEmpty()) {
                groupIdOrTeamsMeetingLinkText.setText(deeplinkTeamsUrl)
                groupCallRadioButton.isChecked = false
                teamsMeetingRadioButton.isChecked = true
            } else {
                groupIdOrTeamsMeetingLinkText.setText(BuildConfig.GROUP_CALL_ID)
            }

            launchButton.setOnClickListener {
                launchButton.isEnabled = false
                launch()
            }

            tokenFunctionRadioButton.setOnClickListener {
                if (tokenFunctionRadioButton.isChecked) {
                    tokenFunctionUrlText.requestFocus()
                    tokenFunctionUrlText.isEnabled = true
                    acsTokenText.isEnabled = false
                    acsCommunicationUserIdText.isEnabled = false
                    acsTokenRadioButton.isChecked = false
                    callLauncherViewModel.useTokenFunction()
                }
            }
            acsTokenRadioButton.setOnClickListener {
                if (acsTokenRadioButton.isChecked) {
                    acsTokenText.requestFocus()
                    acsTokenText.isEnabled = true
                    acsCommunicationUserIdText.isEnabled = true
                    tokenFunctionUrlText.isEnabled = false
                    tokenFunctionRadioButton.isChecked = false
                    callLauncherViewModel.useAcsToken()
                }
            }
            groupCallRadioButton.setOnClickListener {
                if (groupCallRadioButton.isChecked) {
                    groupIdOrTeamsMeetingLinkText.setText(BuildConfig.GROUP_CALL_ID)
                    teamsMeetingRadioButton.isChecked = false
                    chatThreadIdText.isEnabled = true
                }
            }
            teamsMeetingRadioButton.setOnClickListener {
                if (teamsMeetingRadioButton.isChecked) {
                    groupIdOrTeamsMeetingLinkText.setText(BuildConfig.TEAMS_MEETING_LINK)
                    groupCallRadioButton.isChecked = false
                    chatThreadIdText.isEnabled = false
                }
            }

            javaButton.setOnClickListener {
                callLauncherViewModel.setJavaLauncher()
            }

            kotlinButton.setOnClickListener {
                callLauncherViewModel.setKotlinLauncher()
            }

            if (!BuildConfig.DEBUG) {
                versionText.text = "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"
            }
        }

        callLauncherViewModel.fetchResult.observe(this) {
            processResult(it)
        }
    }

    override fun onDestroy() {
        callLauncherViewModel.destroy()
        super.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        saveState(outState)
        super.onSaveInstanceState(outState)
    }

    // check whether new Activity instance was brought to top of stack,
    // so that finishing this will get us to the last viewed screen
    private fun shouldFinish() = BuildConfig.CHECK_TASK_ROOT && !isTaskRoot

    override fun showAlert(message: String) {
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

    private fun processResult(result: Result<Any?>) {
        if (result.isFailure) {
            result.exceptionOrNull()?.let {
                if (it.message != null) {
                    val causeMessage = it.cause?.message ?: ""
                    showAlert(it.toString() + causeMessage)
                } else {
                    showAlert("Unknown error")
                }
            }
        }
        binding.launchButton.isEnabled = true
    }

    private fun launch() {
        val userName = binding.userNameText.text.toString()
        val acsEndpoint = binding.acsEndpointText.text.toString()
        val chatThreadId = binding.chatThreadIdText.text.toString()

        val authApiUrl = when {
            binding.tokenFunctionRadioButton.isChecked -> binding.tokenFunctionUrlText.text.toString()
            else -> null
        }
        val acsToken = when {
            binding.acsTokenRadioButton.isChecked -> binding.acsTokenText.text.toString()
            else -> null
        }

        val communicationUserId = when {
            binding.acsTokenRadioButton.isChecked -> binding.acsCommunicationUserIdText.text.toString()
            else -> null
        }

        val groupId: UUID? = when {
            binding.groupCallRadioButton.isChecked -> {
                try {
                    UUID.fromString(binding.groupIdOrTeamsMeetingLinkText.text.toString().trim())
                } catch (e: IllegalArgumentException) {
                    val message = "Group ID is invalid or empty."
                    showAlert(message)
                    return@launch
                }
            }
            else -> null
        }

        val meetingLink = when {
            binding.teamsMeetingRadioButton.isChecked -> {
                val text = binding.groupIdOrTeamsMeetingLinkText.text.toString()

                if (text.isBlank()) {
                    val message = "Teams meeting link is invalid or empty."
                    showAlert(message)
                    return@launch
                } else {
                    text
                }
            }
            else -> null
        }

        callLauncherViewModel.doLaunch(
            context = this@CallWithChatLauncherActivity,
            alertHandler = this@CallWithChatLauncherActivity,
            authApiUrl,
            acsToken,
            communicationUserId,
            userName,
            acsEndpoint,
            groupId,
            chatThreadId,
            meetingLink,
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
        outState?.putBoolean(
            isTokenFunctionOptionSelected,
            callLauncherViewModel.isTokenFunctionOptionSelected
        )
        outState?.putBoolean(isKotlinLauncherOptionSelected, callLauncherViewModel.isKotlinLauncher)
    }
}

interface AlertHandler {
    fun showAlert(message: String)
}
