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
import com.azure.android.communication.ui.callingcompositedemoapp.features.FeatureFlags
import com.azure.android.communication.ui.callingcompositedemoapp.features.conditionallyRegisterDiagnostics
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.crashes.Crashes
import com.microsoft.appcenter.distribute.Distribute
import org.threeten.bp.format.DateTimeFormatter
import java.util.UUID

class CallLauncherActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCallLauncherBinding
    private val callLauncherViewModel: CallLauncherViewModel by viewModels()

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
        FeatureFlags.registerAdditionalFeature(AdditionalFeatures.secondaryThemeFeature)

        binding = ActivityCallLauncherBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val data: Uri? = intent?.data
        val deeplinkAcsToken = data?.getQueryParameter("acstoken")
        val deeplinkName = data?.getQueryParameter("name")
        val deeplinkGroupId = data?.getQueryParameter("groupid")
        val deeplinkTeamsUrl = data?.getQueryParameter("teamsurl")

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
                launch()
            }

            showUIButton.setOnClickListener {
                showUI()
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

            showCallHistoryButton.setOnClickListener {
                showCallHistory()
            }

            if (BuildConfig.DEBUG) {
                versionText.text = "${BuildConfig.VERSION_NAME}"
            } else {
                versionText.text = "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"
            }
        }
    }

    // check whether new Activity instance was brought to top of stack,
    // so that finishing this will get us to the last viewed screen
    private fun shouldFinish() = BuildConfig.CHECK_TASK_ROOT && !isTaskRoot

    private fun showAlert(message: String, title: String = "Alert") {
        runOnUiThread {
            val builder = AlertDialog.Builder(this).apply {
                setMessage(message)
                setTitle(title)
                setPositiveButton("OK") { _, _ ->
                }
            }
            builder.show()
        }
    }

    private fun launch() {
        val userName = binding.userNameText.text.toString()
        val acsToken = binding.acsTokenText.text.toString()

        var groupId: UUID? = null
        if (binding.groupCallRadioButton.isChecked) {
            try {
                groupId =
                    UUID.fromString(binding.groupIdOrTeamsMeetingLinkText.text.toString().trim())
            } catch (e: IllegalArgumentException) {
                val message = "Group ID is invalid or empty."
                showAlert(message)
                return
            }
        }
        var meetingLink: String? = null
        if (binding.teamsMeetingRadioButton.isChecked) {
            meetingLink = binding.groupIdOrTeamsMeetingLinkText.text.toString()
            if (meetingLink.isBlank()) {
                val message = "Teams meeting link is invalid or empty."
                showAlert(message)
                return
            }
        }

        callLauncherViewModel.launch(
            this@CallLauncherActivity,
            acsToken,
            userName,
            groupId,
            meetingLink,
        )
    }

    private fun showUI() {
        callLauncherViewModel.callComposite?.displayCallCompositeIfWasHidden(this)
    }

    private fun showCallHistory() {
        val history = callLauncherViewModel
            .getCallHistory(this@CallLauncherActivity)
            .sortedBy { it.callStartedOn }

        val title = "Total calls: ${history.count()}"
        var message = "Last Call: none"
        history.lastOrNull()?.let {
            message = "Last Call: ${it.callStartedOn.format(DateTimeFormatter.ofPattern("MMM dd 'at' hh:mm"))}"
            it.callIds.forEach { callId ->
                message += "\nCallId: $callId"
            }
        }

        showAlert(message, title)
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
