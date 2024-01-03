// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callingcompositedemoapp

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.azure.android.communication.ui.calling.models.CallCompositeParticipantRole
import com.azure.android.communication.ui.callingcompositedemoapp.databinding.ActivityCallLauncherBinding
import com.azure.android.communication.ui.callingcompositedemoapp.features.AdditionalFeatures
import com.azure.android.communication.ui.callingcompositedemoapp.features.FeatureFlags
import com.azure.android.communication.ui.callingcompositedemoapp.features.SettingsFeatures
import com.azure.android.communication.ui.callingcompositedemoapp.features.conditionallyRegisterDiagnostics
import com.azure.android.communication.ui.callingcompositedemoapp.telecom.TelecomConnectionManager
import com.azure.android.communication.ui.callingcompositedemoapp.views.EndCompositeButtonView
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.crashes.Crashes
import com.microsoft.appcenter.distribute.Distribute
import kotlinx.coroutines.launch
import org.threeten.bp.format.DateTimeFormatter
import java.util.UUID

class CallLauncherActivity : AppCompatActivity() {
    companion object {
        const val TAG = "InderTest"
        const val PHONE_ACCOUNT_ID = ""
        const val CALL_LAUNCHER_BROADCAST_ACTION = "CALL_LAUNCHER_BROADCAST_ACTION"
        var isActivityRunning = false
    }

    private val ringToneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
    private lateinit var binding: ActivityCallLauncherBinding
    private val sharedPreference by lazy {
        getSharedPreferences(SETTINGS_SHARED_PREFS, Context.MODE_PRIVATE)
    }
    private var callCompositeManager: CallCompositeManager? = null

    private val callLauncherBroadCastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == CALL_LAUNCHER_BROADCAST_ACTION) {
                onBroadCastReceived(intent)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isActivityRunning = true
        createNotificationChannels()
        initCallCompositeManager()
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
        var deeplinkGroupId = data?.getQueryParameter("groupid")
        val deeplinkTeamsUrl = data?.getQueryParameter("teamsurl")
        val participantMRI = data?.getQueryParameter("participanturis") ?: BuildConfig.PARTICIPANT_MRIS
        val deepLinkRoomsId = data?.getQueryParameter("roomsid")

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

            if (deeplinkGroupId.isNullOrEmpty()) {
                deeplinkGroupId = BuildConfig.GROUP_CALL_ID
            }

            if (!deeplinkGroupId.isNullOrEmpty()) {
                groupIdOrTeamsMeetingLinkText.setText(deeplinkGroupId)
                groupCallRadioButton.isChecked = true
                teamsMeetingRadioButton.isChecked = false
                oneToOneRadioButton.isChecked = false
                roomsMeetingRadioButton.isChecked = false
            } else if (!deeplinkTeamsUrl.isNullOrEmpty()) {
                groupIdOrTeamsMeetingLinkText.setText(deeplinkTeamsUrl)
                groupCallRadioButton.isChecked = false
                teamsMeetingRadioButton.isChecked = true
                oneToOneRadioButton.isChecked = false
            } else if (participantMRI.isNotEmpty()) {
                groupIdOrTeamsMeetingLinkText.setText(participantMRI)
                groupCallRadioButton.isChecked = false
                teamsMeetingRadioButton.isChecked = false
                oneToOneRadioButton.isChecked = true
                roomsMeetingRadioButton.isChecked = false
            } else if (!deepLinkRoomsId.isNullOrEmpty()) {
                groupIdOrTeamsMeetingLinkText.setText(deepLinkRoomsId)
                groupCallRadioButton.isChecked = false
                teamsMeetingRadioButton.isChecked = false
                roomsMeetingRadioButton.isChecked = true
            } else {
                groupIdOrTeamsMeetingLinkText.setText(BuildConfig.GROUP_CALL_ID)
            }

            launchButton.setOnClickListener {
                launch()
            }

            showUIButton.setOnClickListener {
                showUI()
            }

            closeCompositeButton.setOnClickListener { callCompositeManager?.close() }

            groupCallRadioButton.setOnClickListener {
                if (groupCallRadioButton.isChecked) {
                    groupIdOrTeamsMeetingLinkText.setText(BuildConfig.GROUP_CALL_ID)
                    teamsMeetingRadioButton.isChecked = false
                    oneToOneRadioButton.isChecked = false
                    roomsMeetingRadioButton.isChecked = false
                    attendeeRoleRadioButton.visibility = View.GONE
                    presenterRoleRadioButton.visibility = View.GONE
                }
            }
            teamsMeetingRadioButton.setOnClickListener {
                if (teamsMeetingRadioButton.isChecked) {
                    groupIdOrTeamsMeetingLinkText.setText(BuildConfig.TEAMS_MEETING_LINK)
                    groupCallRadioButton.isChecked = false
                    oneToOneRadioButton.isChecked = false
                    roomsMeetingRadioButton.isChecked = false
                    attendeeRoleRadioButton.visibility = View.GONE
                    presenterRoleRadioButton.visibility = View.GONE
                }
            }
            roomsMeetingRadioButton.setOnClickListener {
                if (roomsMeetingRadioButton.isChecked) {
                    groupIdOrTeamsMeetingLinkText.setText(BuildConfig.ROOMS_ID)
                    presenterRoleRadioButton.visibility = View.VISIBLE
                    attendeeRoleRadioButton.visibility = View.VISIBLE
                    attendeeRoleRadioButton.isChecked = true
                    groupCallRadioButton.isChecked = false
                    teamsMeetingRadioButton.isChecked = false
                } else {
                    presenterRoleRadioButton.visibility = View.GONE
                    attendeeRoleRadioButton.visibility = View.GONE
                }
            }

            presenterRoleRadioButton.setOnClickListener {
                if (presenterRoleRadioButton.isChecked) {
                    attendeeRoleRadioButton.isChecked = false
                }
            }

            attendeeRoleRadioButton.setOnClickListener {
                if (attendeeRoleRadioButton.isChecked) {
                    presenterRoleRadioButton.isChecked = false
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
                callCompositeManager?.acceptIncomingCall(this@CallLauncherActivity)
            }

            declineCallButton.setOnClickListener {
                incomingCallLayout.visibility = LinearLayout.GONE
                callCompositeManager?.declineIncomingCall()
            }

            showCallHistoryButton.setOnClickListener {
                showCallHistory()
            }

            registerPushNotification.setOnClickListener {
                // It is for demo only, storing token in shared preferences is not recommended (security issue)
                if (acsTokenText.text.toString().isEmpty()) {
                    showAlert("ACS token is empty.")
                    return@setOnClickListener
                }
                sharedPreference.edit().putString(CACHED_TOKEN, acsTokenText.text.toString()).apply()
                sharedPreference.edit().putString(CACHED_USER_NAME, userNameText.text.toString()).apply()
                registerPuhNotification()
            }

            lifecycleScope.launch {
                callCompositeManager?.callCompositeCallStateStateFlow?.collect {
                    runOnUiThread {
                        if (it.isNotEmpty()) {
                            callStateText.text = it
                            EndCompositeButtonView.get(application).updateText(it)
                        }
                    }
                }
            }

            lifecycleScope.launch {
                callCompositeManager?.callCompositeExitSuccessStateFlow?.collect {
                    runOnUiThread {
                        if (it &&
                            SettingsFeatures.getReLaunchOnExitByDefaultOption()
                        ) {
                            launch()
                        }
                    }
                }
            }

            lifecycleScope.launch {
                callCompositeManager?.callCompositeShowAlertStateStateFlow?.collect {
                    runOnUiThread {
                        if (it.isNotEmpty()) {
                            showAlert(it + "Call ID: " + callCompositeManager?.getLastCallId(applicationContext))
                        }
                    }
                }
            }

            disposeCompositeButton.setOnClickListener {
                callCompositeManager?.destroy()
            }

            if (BuildConfig.DEBUG) {
                versionText.text = BuildConfig.VERSION_NAME
            } else {
                versionText.text = "${BuildConfig.VERSION_NAME} ${BuildConfig.VERSION_CODE}"
            }
        }

        handlePushNotificationAction(intent)
        registerReceiver(callLauncherBroadCastReceiver, IntentFilter(CALL_LAUNCHER_BROADCAST_ACTION))
    }

    override fun onDestroy() {
        super.onDestroy()
        isActivityRunning = false
        EndCompositeButtonView.get(this).hide()
        EndCompositeButtonView.buttonView = null
        unregisterReceiver(callLauncherBroadCastReceiver)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handlePushNotificationAction(intent!!)
    }

    private fun initCallCompositeManager() {
        if (callCompositeManager != null) {
            return
        }

        val application = application as CallLauncherApplication

        if (application.callCompositeManager != null) {
            callCompositeManager = application.callCompositeManager
            return
        }

        callCompositeManager = CallCompositeManager(this@CallLauncherActivity)
        application.callCompositeManager = callCompositeManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            callCompositeManager?.telecomConnectionManager(TelecomConnectionManager(this@CallLauncherActivity, PHONE_ACCOUNT_ID))
        }
    }

    private fun stringToMap(jsonString: String): Map<String, String> {
        return try {
            val objectMapper: ObjectMapper = jacksonObjectMapper()
            objectMapper.readValue(jsonString, Map::class.java) as Map<String, String>
        } catch (e: JsonProcessingException) {
            e.printStackTrace()
            emptyMap()
        }
    }

    private fun onBroadCastReceived(intent: Intent) {
        val extras = intent.extras
        val tag = extras?.getString("tag")
        tag?.let {
            onIntentAction(tag, extras)
        }
    }

    private fun handlePushNotificationAction(newIntent: Intent) {
        initCallCompositeManager()
        newIntent.action?.let {
            callCompositeManager?.createCallComposite()
            onIntentAction(it, newIntent.extras)
        }
    }

    private fun onIncomingCallPushNotificationReceived(extras: Bundle) {
        val acsIdentityToken = sharedPreference.getString(CACHED_TOKEN, "")
        val displayName = sharedPreference.getString(CACHED_USER_NAME, "")
        val value = stringToMap(extras.getString("data")!!)
        callCompositeManager?.handleIncomingCall(
            value,
            acsIdentityToken!!,
            displayName!!,
            this@CallLauncherActivity
        )
    }

    private fun onIntentAction(tag: String, extras: Bundle?) {
        when (tag) {
            "incoming_call" -> {
                binding.incomingCallLayout.visibility = View.VISIBLE
            }
            "answer" -> {
                binding.incomingCallLayout.visibility = View.GONE
                callCompositeManager?.acceptIncomingCall(this@CallLauncherActivity)
            }
            "decline" -> {
                binding.incomingCallLayout.visibility = View.GONE
                callCompositeManager?.declineIncomingCall()
            }
            "hold" -> {
                callCompositeManager?.getCallComposite()?.hold()
            }
            "resume" -> {
                callCompositeManager?.getCallComposite()?.resume()
            }
            "handle_incoming_call_push" -> {
                extras?.let { onIncomingCallPushNotificationReceived(it) }
            }
            "clear_push_notification" -> {
                callCompositeManager?.hideIncomingCallUI()
            }
        }
    }

    private fun registerPuhNotification() {
        try {
            val acsToken = sharedPreference.getString(CACHED_TOKEN, "")
            val userName = sharedPreference.getString(CACHED_USER_NAME, "")
            callCompositeManager?.registerFirebaseToken(
                acsToken!!,
                userName!!
            )
            showAlert("Register for push notification successfully.")
        } catch (e: Exception) {
            showAlert("Failed to register push notification token. " + e.message)
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
        sharedPreference.edit().putString(CACHED_TOKEN, acsToken).apply()
        sharedPreference.edit().putString(CACHED_USER_NAME, userName).apply()
        val roomId = binding.groupIdOrTeamsMeetingLinkText.text.toString()
        val roomRole = if (binding.attendeeRoleRadioButton.isChecked) CallCompositeParticipantRole.ATTENDEE
        else if (binding.presenterRoleRadioButton.isChecked) CallCompositeParticipantRole.PRESENTER
        else null

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

        callCompositeManager?.launch(
            this@CallLauncherActivity,
            acsToken,
            userName,
            groupId,
            roomId,
            roomRole,
            meetingLink,
            participantMri
        )
    }

    private fun showUI() {
        callCompositeManager?.displayCallCompositeIfWasHidden(this)
    }

    private fun showCallHistory() {
        val history = callCompositeManager?.getCallHistory(this@CallLauncherActivity)?.sortedBy { it.callStartedOn }

        val title = "Total calls: ${history?.count()}"
        var message = "Last Call: none"
        history?.lastOrNull()?.let {
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
            EndCompositeButtonView.get(this).show()
        }
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = "acs"
            val description = "acs"
            val importance = NotificationManager.IMPORTANCE_HIGH

            val channel = NotificationChannel(
                "acs",
                name,
                importance
            )

            channel.description = description
            channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            channel.enableVibration(true)
            channel.setSound(
                ringToneUri,
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setLegacyStreamType(AudioManager.STREAM_RING)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE).build()
            )
            channel.enableLights(true)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                channel.setAllowBubbles(true)
            }
            val notificationManager = getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(channel)
        }
    }
}
