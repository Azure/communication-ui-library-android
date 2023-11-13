// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callingcompositedemoapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.lifecycleScope
import com.azure.android.communication.ui.calling.CallComposite
import com.azure.android.communication.ui.calling.models.CallCompositeIncomingCallInfo
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
import kotlinx.coroutines.launch
import org.threeten.bp.format.DateTimeFormatter
import java.util.UUID

class CallLauncherActivity : AppCompatActivity(), CallCompositeEvents {

    companion object {
        const val TAG = "communication.ui.demo"
        var callCompositeEvents: CallCompositeEvents? = null
    }

    private lateinit var binding: ActivityCallLauncherBinding
    private val callLauncherViewModel: CallLauncherViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createNotificationChannels()

        // event handler to update UI for incoming call
        callCompositeEvents = this

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
        val participantMRI = data?.getQueryParameter("participanturis") ?: BuildConfig.PARTICIPANT_MRIS

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
                oneToOneRadioButton.isChecked = false
            } else if (!deeplinkTeamsUrl.isNullOrEmpty()) {
                groupIdOrTeamsMeetingLinkText.setText(deeplinkTeamsUrl)
                groupCallRadioButton.isChecked = false
                teamsMeetingRadioButton.isChecked = true
                oneToOneRadioButton.isChecked = false
            } else if (!participantMRI.isNullOrEmpty()) {
                groupIdOrTeamsMeetingLinkText.setText(participantMRI)
                groupCallRadioButton.isChecked = false
                teamsMeetingRadioButton.isChecked = false
                oneToOneRadioButton.isChecked = true
            } else {
                groupIdOrTeamsMeetingLinkText.setText(BuildConfig.GROUP_CALL_ID)
            }

            launchButton.setOnClickListener {
                launch()
            }

            closeCompositeButton.setOnClickListener { callLauncherViewModel.close() }

            groupCallRadioButton.setOnClickListener {
                if (groupCallRadioButton.isChecked) {
                    groupIdOrTeamsMeetingLinkText.setText(BuildConfig.GROUP_CALL_ID)
                    teamsMeetingRadioButton.isChecked = false
                    oneToOneRadioButton.isChecked = false
                }
            }
            teamsMeetingRadioButton.setOnClickListener {
                if (teamsMeetingRadioButton.isChecked) {
                    groupIdOrTeamsMeetingLinkText.setText(BuildConfig.TEAMS_MEETING_LINK)
                    groupCallRadioButton.isChecked = false
                    oneToOneRadioButton.isChecked = false
                }
            }
            oneToOneRadioButton.setOnClickListener {
                if (oneToOneRadioButton.isChecked) {
                    groupIdOrTeamsMeetingLinkText.setText(BuildConfig.PARTICIPANT_MRIS)
                    groupCallRadioButton.isChecked = false
                    teamsMeetingRadioButton.isChecked = false
                }
            }

            acceptCallButton.setOnClickListener {
                incomingCallLayout.visibility = LinearLayout.GONE
                callLauncherViewModel.acceptIncomingCall(applicationContext)
            }

            declineCallButton.setOnClickListener {
                incomingCallLayout.visibility = LinearLayout.GONE
                callLauncherViewModel.createCallComposite(applicationContext).declineIncomingCall()
            }

            showCallHistoryButton.setOnClickListener {
                showCallHistory()
            }

            registerPushNotification.setOnClickListener {
                registerPuhNotification()
            }

            lifecycleScope.launch {
                callLauncherViewModel.callCompositeCallStateStateFlow.collect {
                    runOnUiThread {
                        if (it.isNotEmpty()) {
                            callStateText.text = it
                            EndCompositeButtonView.get(application).updateText(it)
                        }
                    }
                }
            }

            lifecycleScope.launch {
                callLauncherViewModel.callCompositeExitSuccessStateFlow.collect {
                    runOnUiThread {
                        if (it &&
                            SettingsFeatures.getReLaunchOnExitByDefaultOption()
                        ) {
                            launch()
                        }
                    }
                }
            }

            disposeCompositeButton.setOnClickListener {
                callLauncherViewModel.destroy()
            }

            if (BuildConfig.DEBUG) {
                versionText.text = "${BuildConfig.VERSION_NAME}"
            } else {
                versionText.text = "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"
            }
        }
    }

    private fun registerPuhNotification() {
        try {
            callLauncherViewModel.registerFirebaseToken(this@CallLauncherActivity)
        } catch (e: Exception) {
            showAlert("Failed to register push notification token. " + e.message)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EndCompositeButtonView.get(this).hide()
        EndCompositeButtonView.buttonView = null
        callLauncherViewModel.close()

        if (isFinishing) {
            callLauncherViewModel.close()
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

        var participantMri: String? = null
        if (binding.oneToOneRadioButton.isChecked) {
            participantMri = binding.groupIdOrTeamsMeetingLinkText.text.toString()
            if (participantMri.isBlank()) {
                val message = "Participant MRI is invalid or empty."
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
            participantMri
        )
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

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = "acs"
            val description = "acs"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel =
                NotificationChannel("acs", name, importance)
            channel.description = description
            val notificationManager = getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun getCallComposite(): CallComposite = callLauncherViewModel.createCallComposite(applicationContext)

    override fun showIncomingCallUI(incomingCallInfo: CallCompositeIncomingCallInfo) {
        this.runOnUiThread {
            showNotificationForIncomingCall(incomingCallInfo)
            binding.incomingCallLayout.visibility = View.VISIBLE
        }
    }

    override fun hideIncomingCallUI() {
        this.runOnUiThread {
            dismissNotificationForIncomingCall()
            binding.incomingCallLayout.visibility = View.GONE
        }
    }

    override fun handleIncomingCall(data: Map<String, String>) {
        val userName = binding.userNameText.text.toString()
        val acsToken = binding.acsTokenText.text.toString()
        callLauncherViewModel.handleIncomingCall(
            data.toMutableMap(),
            applicationContext,
            acsToken,
            userName
        )
    }

    override fun onCompositeDismiss() {
        if (callLauncherViewModel.exitedCompositeToAcceptIncomingCall()) {
            callLauncherViewModel.acceptIncomingCall(applicationContext)
        } else {
            // registerPuhNotification()
            callLauncherViewModel.destroy()
        }
    }

    override fun onRemoteParticipantJoined(rawId: String) {
    }

    override fun incomingCallEnded() {
        // registerPuhNotification()
    }

    override fun acceptIncomingCall() {
        callLauncherViewModel.acceptIncomingCall(applicationContext)
    }

    private fun showNotificationForIncomingCall(notification: CallCompositeIncomingCallInfo) {
        Log.i(TAG, "Showing notification for incoming call")
        val resultIntent = Intent(this, CallLauncherActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val stackBuilder = TaskStackBuilder.create(this)
        stackBuilder.addNextIntentWithParentStack(resultIntent)

        val resultPendingIntent =
            stackBuilder.getPendingIntent(0, PendingIntent.FLAG_IMMUTABLE)

        val answerCallIntent = Intent(applicationContext, HandleNotification::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        answerCallIntent.putExtra("action", "answer")
        val answerCallPendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            1200,
            answerCallIntent,
            PendingIntent.FLAG_IMMUTABLE
        )
        val declineCallIntent = Intent(applicationContext, HandleNotification::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        declineCallIntent.putExtra("action", "decline")
        val declineCallPendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            1201,
            declineCallIntent,
            PendingIntent.FLAG_IMMUTABLE
        )
        val content = java.lang.String.format(
            "%s",
            notification.callerDisplayName
        )
        val builder: NotificationCompat.Builder = NotificationCompat.Builder(this, "acs")
            .setContentIntent(resultPendingIntent)
            .setSmallIcon(android.R.drawable.ic_menu_call)
            .setCategory(NotificationCompat.CATEGORY_CALL)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setWhen(System.currentTimeMillis())
            .setContentTitle("Incoming Call")
            .setContentText(content)
            .addAction(android.R.drawable.ic_menu_call, "Accept", answerCallPendingIntent)
            .addAction(android.R.drawable.ic_menu_call, "Decline", declineCallPendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE))
            .setOngoing(true)
            .setAutoCancel(true)
        val notificationManager = NotificationManagerCompat.from(this)
        notificationManager.notify(1, builder.build())
    }

    private fun dismissNotificationForIncomingCall() {
        val notificationManager = NotificationManagerCompat.from(this)
        notificationManager.cancel(1)
    }
}
