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
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.lifecycleScope
import com.azure.android.communication.common.CommunicationTokenCredential
/* <ROOMS_SUPPORT:0> */
import com.azure.android.communication.ui.calling.models.CallCompositeParticipantRole
/* </ROOMS_SUPPORT:0> */
import com.azure.android.communication.ui.callingcompositedemoapp.databinding.ActivityCallLauncherBinding
import com.azure.android.communication.ui.callingcompositedemoapp.features.AdditionalFeatures
import com.azure.android.communication.ui.callingcompositedemoapp.features.FeatureFlags
import com.azure.android.communication.ui.callingcompositedemoapp.features.SettingsFeatures
import com.azure.android.communication.ui.callingcompositedemoapp.features.conditionallyRegisterDiagnostics
import com.azure.android.communication.ui.callingcompositedemoapp.views.DismissCompositeButtonView
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
        const val TAG = "calling.demo.app"
        var isActivityRunning = false
        const val CALL_LAUNCHER_BROADCAST_ACTION = "CALL_LAUNCHER_BROADCAST_ACTION"
    }

    private lateinit var binding: ActivityCallLauncherBinding
    private lateinit var callCompositeManager: CallCompositeManager
    private val callLauncherBroadCastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == CALL_LAUNCHER_BROADCAST_ACTION) {
                onBroadCastReceived(intent)
            }
        }
    }
    private val sharedPreference by lazy {
        getSharedPreferences(SETTINGS_SHARED_PREFS, Context.MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (shouldFinish()) {
            finish()
            return
        }
        isActivityRunning = true
        createNotificationChannels()
        initCallCompositeManager()
        SettingsFeatures.initialize(applicationContext)

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
        /* </ROOMS_SUPPORT:5> */
        val deepLinkRoomsId = data?.getQueryParameter("roomsid")
        /* </ROOMS_SUPPORT:0> */
        val participantMRIs = data?.getQueryParameter("participantmris")

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
                /* <ROOMS_SUPPORT:4> */
                roomsMeetingRadioButton.isChecked = false
                /* </ROOMS_SUPPORT:1> */
            } else if (!deeplinkTeamsUrl.isNullOrEmpty()) {
                groupIdOrTeamsMeetingLinkText.setText(deeplinkTeamsUrl)
                groupCallRadioButton.isChecked = false
                teamsMeetingRadioButton.isChecked = true
                /* <ROOMS_SUPPORT:4> */
                roomsMeetingRadioButton.isChecked = false
            } else if (!deepLinkRoomsId.isNullOrEmpty()) {
                groupIdOrTeamsMeetingLinkText.setText(deepLinkRoomsId)
                groupCallRadioButton.isChecked = false
                teamsMeetingRadioButton.isChecked = false
                roomsMeetingRadioButton.isChecked = true
                /* </ROOMS_SUPPORT:1> */
            } else if (!participantMRIs.isNullOrEmpty()) {
                groupIdOrTeamsMeetingLinkText.setText(participantMRIs)
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
            closeCompositeButton.setOnClickListener { callCompositeManager.dismissCallComposite() }

            groupCallRadioButton.setOnClickListener {
                if (groupCallRadioButton.isChecked) {
                    groupIdOrTeamsMeetingLinkText.setText(BuildConfig.GROUP_CALL_ID)
                    teamsMeetingRadioButton.isChecked = false
                    teamsMeetingPasscode.visibility = View.GONE
                    teamsMeetingId.visibility = View.GONE
                    /* <ROOMS_SUPPORT:4> */
                    roomsMeetingRadioButton.isChecked = false
                    oneToNCallRadioButton.isChecked = false
                    attendeeRoleRadioButton.visibility = View.GONE
                    presenterRoleRadioButton.visibility = View.GONE
                    /* </ROOMS_SUPPORT:1> */
                }
            }
            teamsMeetingRadioButton.setOnClickListener {
                if (teamsMeetingRadioButton.isChecked) {
                    groupIdOrTeamsMeetingLinkText.setText(BuildConfig.TEAMS_MEETING_LINK)
                    groupCallRadioButton.isChecked = false
                    oneToNCallRadioButton.isChecked = false
                    teamsMeetingPasscode.visibility = View.VISIBLE
                    teamsMeetingId.visibility = View.VISIBLE
                    /* <ROOMS_SUPPORT:4> */
                    roomsMeetingRadioButton.isChecked = false
                    attendeeRoleRadioButton.visibility = View.GONE
                    presenterRoleRadioButton.visibility = View.GONE
                    /* </ROOMS_SUPPORT:1> */
                }
            }
            /* <ROOMS_SUPPORT:0> */
            roomsMeetingRadioButton.setOnClickListener {
                if (roomsMeetingRadioButton.isChecked) {
                    groupIdOrTeamsMeetingLinkText.setText(BuildConfig.ROOMS_ID)
                    presenterRoleRadioButton.visibility = View.VISIBLE
                    attendeeRoleRadioButton.visibility = View.VISIBLE
                    attendeeRoleRadioButton.isChecked = true
                    groupCallRadioButton.isChecked = false
                    oneToNCallRadioButton.isChecked = false
                    teamsMeetingRadioButton.isChecked = false
                    teamsMeetingPasscode.visibility = View.GONE
                    teamsMeetingId.visibility = View.GONE
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
            /* </ROOMS_SUPPORT:0> */

            oneToNCallRadioButton.setOnClickListener {
                if (oneToNCallRadioButton.isChecked) {
                    groupIdOrTeamsMeetingLinkText.setText(BuildConfig.PARTICIPANT_MRIS)
                    groupCallRadioButton.isChecked = false
                    teamsMeetingRadioButton.isChecked = false
                    /* <ROOMS_SUPPORT:4> */
                    roomsMeetingRadioButton.isChecked = false
                    attendeeRoleRadioButton.visibility = View.GONE
                    presenterRoleRadioButton.visibility = View.GONE
                    /* </ROOMS_SUPPORT:1> */
                }
            }

            showCallHistoryButton.setOnClickListener {
                showCallHistory()
            }

            acceptCallButton.setOnClickListener {
                incomingCallLayout.visibility = LinearLayout.GONE
                val acsIdentityToken = sharedPreference.getString(CACHED_TOKEN, "")
                val displayName = sharedPreference.getString(CACHED_USER_NAME, "")
                callCompositeManager.acceptIncomingCall(this@CallLauncherActivity, acsIdentityToken!!, displayName!!)
            }

            registerPushNotificationButton.setOnClickListener {
                if (acsTokenText.text.toString().isEmpty()) {
                    showAlert("ACS token is empty.")
                    return@setOnClickListener
                }
                cacheTokenAndDisplayName()
                registerPushNotification()
            }

            unregisterPushNotificationButton.setOnClickListener {
                if (acsTokenText.text.toString().isEmpty()) {
                    showAlert("ACS token is empty.")
                    return@setOnClickListener
                }
                cacheTokenAndDisplayName()
                unregisterPushNotification()
            }

            declineCallButton.setOnClickListener {
                incomingCallLayout.visibility = LinearLayout.GONE
                callCompositeManager.declineIncomingCall()
            }

            lifecycleScope.launchAll(
                {
                    callCompositeManager.callCompositeCallStateStateFlow.collect {
                        runOnUiThread {
                            if (it.isNotEmpty()) {
                                callStateText.text = it
                                DismissCompositeButtonView.get(application).updateText(it)
                            }
                        }
                    }
                },
            )

            if (BuildConfig.DEBUG) {
                versionText.text = "${BuildConfig.VERSION_NAME}"
            } else {
                versionText.text = "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"
            }
        }

        handlePushNotificationAction(intent)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(
                callLauncherBroadCastReceiver,
                IntentFilter(CALL_LAUNCHER_BROADCAST_ACTION),
                RECEIVER_EXPORTED
            )
        } else {
            registerReceiver(
                callLauncherBroadCastReceiver,
                IntentFilter(CALL_LAUNCHER_BROADCAST_ACTION)
            )
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handlePushNotificationAction(intent!!)
    }

    override fun onDestroy() {
        super.onDestroy()
        isActivityRunning = false
        unregisterReceiver(callLauncherBroadCastReceiver)
        DismissCompositeButtonView.get(this).hide()
        DismissCompositeButtonView.buttonView = null
        (application as CallLauncherApplication).onDestroy()
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

        /* <ROOMS_SUPPORT:0> */
        val roomId = binding.groupIdOrTeamsMeetingLinkText.text.toString()
        val roomRole =
            if (binding.attendeeRoleRadioButton.isChecked) CallCompositeParticipantRole.ATTENDEE
            else if (binding.presenterRoleRadioButton.isChecked) CallCompositeParticipantRole.PRESENTER
            else null
        /* </ROOMS_SUPPORT:0> */

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
        var meetingId: String? = null
        var meetingPasscode: String? = null
        if (binding.teamsMeetingRadioButton.isChecked) {
            meetingLink = binding.groupIdOrTeamsMeetingLinkText.text.toString()
            meetingId = binding.teamsMeetingId.text.toString()
            meetingPasscode = binding.teamsMeetingPasscode.text.toString()
            if (meetingId.isBlank() && meetingLink.isBlank()) {
                val message = "Teams meeting Id and meeting link are invalid or empty."
                showAlert(message)
                return
            }
        }

        var participantMris: String? = null
        if (binding.oneToNCallRadioButton.isChecked) {
            participantMris = binding.groupIdOrTeamsMeetingLinkText.text.toString()
            if (participantMris.isBlank()) {
                val message = "Participant MRIs is invalid or empty."
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

        callCompositeManager.launch(
            this@CallLauncherActivity,
            acsToken,
            userName,
            groupId,
            /* <ROOMS_SUPPORT:5> */
            roomId,
            roomRole,
            /* </ROOMS_SUPPORT:2> */
            meetingLink,
            meetingId,
            meetingPasscode,
            participantMris
        )
    }

    private fun showUI() {
        callCompositeManager.bringCallCompositeToForeground(this)
    }

    private fun showCallHistory() {
        val userName = binding.userNameText.text.toString()
        val acsToken = binding.acsTokenText.text.toString()
        val history = callCompositeManager.getCallHistory(this@CallLauncherActivity, acsToken, userName)?.sortedBy { it.callStartedOn }

        val title = "Total calls: ${history?.count()}"
        var message = "Last Call: none"
        history?.lastOrNull()?.let {
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
        toggleDismissCompositeButton()
    }

    override fun onStop() {
        super.onStop()
        toggleDismissCompositeButton()
    }

    override fun onResume() {
        super.onResume()
        toggleDismissCompositeButton()
    }

    private fun toggleDismissCompositeButton() {
        if (!SettingsFeatures.getDisplayDismissButtonOption()) {
            DismissCompositeButtonView.get(this).hide()
        } else {
            callCompositeManager.let {
                DismissCompositeButtonView.get(this).show(it)
            }
        }
    }

    private fun onBroadCastReceived(intent: Intent) {
        val extras = intent.extras
        val tag = extras?.getString("tag")
        tag?.let {
            onIntentAction(tag, extras)
        }
    }

    private fun onIntentAction(tag: String, extras: Bundle?) {
        when (tag) {
            IntentHelper.INCOMING_CALL -> {
                binding.incomingCallLayout.visibility = View.VISIBLE
            }
            IntentHelper.ANSWER -> {
                binding.incomingCallLayout.visibility = View.GONE
                val acsIdentityToken = sharedPreference.getString(CACHED_TOKEN, "")
                val displayName = sharedPreference.getString(CACHED_USER_NAME, "")
                callCompositeManager.acceptIncomingCall(this@CallLauncherActivity, acsIdentityToken!!, displayName!!)
            }
            IntentHelper.DECLINE -> {
                binding.incomingCallLayout.visibility = View.GONE
                callCompositeManager.declineIncomingCall()
            }
            IntentHelper.HANDLE_INCOMING_CALL_PUSH -> {
                extras?.let { onIncomingCallPushNotificationReceived(it) }
            }
            IntentHelper.CLEAR_PUSH_NOTIFICATION -> {
                callCompositeManager.hideIncomingCallNotification()
            }
        }
    }

    private fun onIncomingCallPushNotificationReceived(extras: Bundle) {
        val acsIdentityToken = sharedPreference.getString(CACHED_TOKEN, "")
        val displayName = sharedPreference.getString(CACHED_USER_NAME, "")
        val value = stringToMap(extras.getString("data")!!)
        callCompositeManager.handleIncomingCall(
            value,
            acsIdentityToken!!,
            displayName!!,
            this@CallLauncherActivity
        )
    }

    private fun createNotificationChannels() {
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
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE),
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

    private fun initCallCompositeManager() {
        val application = application as CallLauncherApplication
        SettingsFeatures.initialize(application)
        callCompositeManager = application.getCallCompositeManager(this)
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

    private fun registerPushNotification() {
        val acsToken = sharedPreference.getString(CACHED_TOKEN, "")
        val userName = sharedPreference.getString(CACHED_USER_NAME, "")
        callCompositeManager.registerPush(
            this@CallLauncherActivity,
            acsToken!!,
            userName!!
        )
    }

    private fun unregisterPushNotification() {
        val acsToken = sharedPreference.getString(CACHED_TOKEN, "")
        val userName = sharedPreference.getString(CACHED_USER_NAME, "")
        callCompositeManager.unregisterPush(
            this@CallLauncherActivity,
            acsToken!!,
            userName!!
        )
    }

    private fun ActivityCallLauncherBinding.cacheTokenAndDisplayName() {
        sharedPreference.edit().putString(CACHED_TOKEN, acsTokenText.text.toString()).apply()
        sharedPreference.edit().putString(CACHED_USER_NAME, userNameText.text.toString()).apply()
    }

    private fun handlePushNotificationAction(newIntent: Intent) {
        initCallCompositeManager()
        newIntent.action?.let {
            onIntentAction(it, newIntent.extras)
        }
    }
}

// We also type launch way to much, this will let it be clean.
internal fun LifecycleCoroutineScope.launchAll(vararg blocks: suspend () -> Unit) {
    launch {
        blocks.forEach { block ->
            launch { block() }
        }
    }
}
