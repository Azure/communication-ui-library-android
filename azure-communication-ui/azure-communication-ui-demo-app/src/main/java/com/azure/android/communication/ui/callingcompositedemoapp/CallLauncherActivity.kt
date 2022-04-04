// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callingcompositedemoapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.azure.android.communication.ui.callingcompositedemoapp.databinding.ActivityCallLauncherBinding
import com.azure.android.communication.ui.callingcompositedemoapp.features.AdditionalFeatures
import com.azure.android.communication.ui.callingcompositedemoapp.features.conditionallyRegisterDiagnostics
import com.azure.android.communication.ui.callingcompositedemoapp.launcher.CallingCompositeLauncher
import com.azure.android.communication.ui.utilities.implementation.FeatureFlags
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.crashes.Crashes
import com.microsoft.appcenter.distribute.Distribute
import com.microsoft.appcenter.distribute.UpdateTrack
import java.util.UUID

class CallLauncherActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCallLauncherBinding
    private val callLauncherViewModel: CallLauncherViewModel by viewModels()
    private val isTokenFunctionOptionSelected: String = "isTokenFunctionOptionSelected"
    private val isKotlinLauncherOptionSelected: String = "isKotlinLauncherOptionSelected"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

        // Initialize the FeatureFlags enum
        FeatureFlags.initialize(this)

        binding = ActivityCallLauncherBinding.inflate(layoutInflater)
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
            tokenFunctionUrlText.setText(BuildConfig.TOKEN_FUNCTION_URL)

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
                callLauncherViewModel.doLaunch(
                    tokenFunctionUrlText.text.toString(),
                    acsTokenText.text.toString()
                )
            }

            tokenFunctionRadioButton.setOnClickListener {
                if (tokenFunctionRadioButton.isChecked) {
                    tokenFunctionUrlText.requestFocus()
                    tokenFunctionUrlText.isEnabled = true
                    acsTokenText.isEnabled = false
                    acsTokenRadioButton.isChecked = false
                    callLauncherViewModel.useTokenFunction()
                }
            }
            acsTokenRadioButton.setOnClickListener {
                if (acsTokenRadioButton.isChecked) {
                    acsTokenText.requestFocus()
                    acsTokenText.isEnabled = true
                    tokenFunctionUrlText.isEnabled = false
                    tokenFunctionRadioButton.isChecked = false
                    callLauncherViewModel.useAcsToken()
                }
            }
            groupCallRadioButton.setOnClickListener {
                if (groupCallRadioButton.isChecked) {
                    groupIdOrTeamsMeetingLinkText.setText(BuildConfig.GROUP_CALL_ID)
                    teamsMeetingRadioButton.isChecked = false
                }
            }
            teamsMeetingRadioButton.setOnClickListener {
                if (teamsMeetingRadioButton.isChecked) {
                    groupIdOrTeamsMeetingLinkText.setText(BuildConfig.TEAMS_MEETING_LINK)
                    groupCallRadioButton.isChecked = false
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

    override fun onResume() {
        super.onResume()
        // Will resume this feature if it's active
        // This is a special case for the permission dialog for diagnostic feature
        AdditionalFeatures.diagnosticsFeature.onStart(application)
    }

    override fun onDestroy() {
        callLauncherViewModel.destroy()
        super.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        saveState(outState)
        super.onSaveInstanceState(outState)
    }

    fun showAlert(message: String) {
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

    private fun processResult(result: Result<CallingCompositeLauncher?>) {
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

    private fun launch(launcher: CallingCompositeLauncher) {
        val userName = binding.userNameText.text.toString()
        if (binding.groupCallRadioButton.isChecked) {
            val groupId: UUID
            try {
                groupId =
                    UUID.fromString(binding.groupIdOrTeamsMeetingLinkText.text.toString().trim())
            } catch (e: IllegalArgumentException) {
                val message = "Group ID is invalid or empty."
                showAlert(message)
                return
            }

            launcher.launch(this@CallLauncherActivity, userName, groupId, null, ::showAlert)
        }

        if (binding.teamsMeetingRadioButton.isChecked) {
            val meetingLink = binding.groupIdOrTeamsMeetingLinkText.text.toString()

            if (meetingLink.isBlank()) {
                val message = "Teams meeting link is invalid or empty."
                showAlert(message)
                return
            }

            launcher.launch(this@CallLauncherActivity, userName, null, meetingLink, ::showAlert)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
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
