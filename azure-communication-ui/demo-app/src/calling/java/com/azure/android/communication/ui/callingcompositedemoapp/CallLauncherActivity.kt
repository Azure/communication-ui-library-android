// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callingcompositedemoapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.lifecycle.lifecycleScope
import com.azure.android.communication.common.CommunicationTokenCredential
import com.azure.android.communication.ui.calling.utilities.launchAll
import com.azure.android.communication.ui.callingcompositedemoapp.databinding.ActivityCallLauncherBinding
import com.azure.android.communication.ui.callingcompositedemoapp.features.AdditionalFeatures
import com.azure.android.communication.ui.callingcompositedemoapp.features.FeatureFlags
import com.azure.android.communication.ui.callingcompositedemoapp.features.SettingsFeatures
import com.azure.android.communication.ui.callingcompositedemoapp.features.conditionallyRegisterDiagnostics
import com.azure.android.communication.ui.callingcompositedemoapp.views.EndCompositeButtonView
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.crashes.Crashes
import com.microsoft.appcenter.distribute.Distribute
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
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
                launchButton.isEnabled = BuildConfig.ACS_TOKEN.isNotEmpty()
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

            acsTokenText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    launchButton.isEnabled = !s.isNullOrEmpty()
                }

                override fun afterTextChanged(s: Editable?) {
                }
            })

            launchButton.setOnClickListener {
                launch()
            }

            showUIButton.setOnClickListener {
                showUI()
            }
            closeCompositeButton.setOnClickListener { callLauncherViewModel.close() }

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

            lifecycleScope.launchAll(
                {
                    callLauncherViewModel.callCompositeCallStateStateFlow.collect {
                        runOnUiThread {
                            if (it.isNotEmpty()) {
                                callStateText.text = it
                                EndCompositeButtonView.get(application).updateText(it)
                            }
                        }
                    }
                },
                {
                    callLauncherViewModel.callCompositeExitSuccessStateFlow.collect {
                        runOnUiThread {
                            if (it &&
                                SettingsFeatures.getReLaunchOnExitByDefaultOption()
                            ) {
                                launch()
                            }
                        }
                    }
                },
                {
                    callLauncherViewModel.userReportedIssueEventHandler.userIssuesFlow.collect {
                        runOnUiThread {
                            it?.apply {
                                showAlert(this.userMessage)
                            }
                        }
                    }
                },
                {
                    callLauncherViewModel.userReportedIssueEventHandler.userIssuesFlow.collect {
                        it?.apply {
                            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

                            val channelId = "user_reported_issue_channel"
                            val channelName = "User Reported Issue Notifications"
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
                                notificationManager.createNotificationChannel(channel)
                            }

                            // Create a summary of the report
                            val reportSummary = StringBuilder(userMessage)
                            reportSummary.append("\nCall ID: ${debugInfo.callHistoryRecords}}")
                            // Add more information from the event as needed
                            // Prepare the large icon (screenshot) for the notification
                            val largeIcon = if (screenshot?.exists() == true) {
                                BitmapFactory.decodeFile(screenshot.absolutePath)
                            } else {
                                null // or a default image
                            }

                            val notificationBuilder = NotificationCompat.Builder(this@CallLauncherActivity, channelId)
                                .setContentTitle("User Reported Issue")
                                .setSmallIcon(R.drawable.azure_communication_ui_calling_ic_fluent_person_feedback_24_regular) // Replace with your notification icon
                                .setStyle(NotificationCompat.BigTextStyle().bigText(reportSummary.toString()))
                                .setLargeIcon(largeIcon)

                            val notification = notificationBuilder.build()

                            notificationManager.notify(System.currentTimeMillis().toInt(), notification)
                        }
                    }
                }

            )

            if (BuildConfig.DEBUG) {
                versionText.text = "${BuildConfig.VERSION_NAME}"
            } else {
                versionText.text = "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EndCompositeButtonView.get(this).hide()
        EndCompositeButtonView.buttonView = null
        callLauncherViewModel.unsubscribe()
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

        try {
            CommunicationTokenCredential(acsToken)
        } catch (e: Exception) {
            showAlert("Invalid token")
            return
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
        callLauncherViewModel.displayCallCompositeIfWasHidden(this)
    }

    private fun showCallHistory() {
        val history = callLauncherViewModel
            .getCallHistory(this@CallLauncherActivity)
            .sortedBy { it.callStartedOn }

        val title = "Total calls: ${history.count()}"
        var message = "Last Call: none"
        history.lastOrNull()?.let {
            message =
                "Last Call: ${it.callStartedOn.format(DateTimeFormatter.ofPattern("MMM dd 'at' hh:mm"))}"
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

    override fun onStart() {
        super.onStart()
        toggleEndCompositeButton()
    }

    override fun onStop() {
        super.onStop()
        toggleEndCompositeButton()
    }

    override fun onResume() {
        super.onResume()
        toggleEndCompositeButton()
    }

    private fun toggleEndCompositeButton() {
        if (!SettingsFeatures.getEndCallOnByDefaultOption()) {
            EndCompositeButtonView.get(this).hide()
        } else {
            EndCompositeButtonView.get(this).show(callLauncherViewModel)
        }
    }
}
