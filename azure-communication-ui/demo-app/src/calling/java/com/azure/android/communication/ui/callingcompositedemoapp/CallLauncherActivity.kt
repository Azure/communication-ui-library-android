// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callingcompositedemoapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
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
            closeCompositeButton.setOnClickListener { callLauncherViewModel.dismissCallComposite() }

            groupCallRadioButton.setOnClickListener {
                if (groupCallRadioButton.isChecked) {
                    groupIdOrTeamsMeetingLinkText.setText(BuildConfig.GROUP_CALL_ID)
                    teamsMeetingRadioButton.isChecked = false
                    /* <ROOMS_SUPPORT:4> */
                    roomsMeetingRadioButton.isChecked = false
                    attendeeRoleRadioButton.visibility = View.GONE
                    presenterRoleRadioButton.visibility = View.GONE
                    consumerRoleRadioButton.visibility = View.GONE
                    /* </ROOMS_SUPPORT:1> */
                }
            }
            teamsMeetingRadioButton.setOnClickListener {
                if (teamsMeetingRadioButton.isChecked) {
                    groupIdOrTeamsMeetingLinkText.setText(BuildConfig.TEAMS_MEETING_LINK)
                    groupCallRadioButton.isChecked = false
                    /* <ROOMS_SUPPORT:4> */
                    roomsMeetingRadioButton.isChecked = false
                    attendeeRoleRadioButton.visibility = View.GONE
                    presenterRoleRadioButton.visibility = View.GONE
                    consumerRoleRadioButton.visibility = View.GONE
                    /* </ROOMS_SUPPORT:1> */
                }
            }
            /* <ROOMS_SUPPORT:0> */
            roomsMeetingRadioButton.setOnClickListener {
                if (roomsMeetingRadioButton.isChecked) {
                    groupIdOrTeamsMeetingLinkText.setText(BuildConfig.ROOMS_ID)
                    presenterRoleRadioButton.visibility = View.VISIBLE
                    attendeeRoleRadioButton.visibility = View.VISIBLE
                    consumerRoleRadioButton.visibility = View.VISIBLE
                    attendeeRoleRadioButton.isChecked = true
                    groupCallRadioButton.isChecked = false
                    teamsMeetingRadioButton.isChecked = false
                } else {
                    presenterRoleRadioButton.visibility = View.GONE
                    attendeeRoleRadioButton.visibility = View.GONE
                    consumerRoleRadioButton.visibility = View.GONE
                }
            }

            presenterRoleRadioButton.setOnClickListener {
                if (presenterRoleRadioButton.isChecked) {
                    attendeeRoleRadioButton.isChecked = false
                    consumerRoleRadioButton.isChecked = false
                }
            }

            attendeeRoleRadioButton.setOnClickListener {
                if (attendeeRoleRadioButton.isChecked) {
                    presenterRoleRadioButton.isChecked = false
                    consumerRoleRadioButton.isChecked = false
                }
            }
            consumerRoleRadioButton.setOnClickListener {
                if (consumerRoleRadioButton.isChecked) {
                    attendeeRoleRadioButton.isChecked = false
                    presenterRoleRadioButton.isChecked = false
                }
            }
            /* </ROOMS_SUPPORT:0> */

            showCallHistoryButton.setOnClickListener {
                showCallHistory()
            }

            lifecycleScope.launchAll(
                {
                    callLauncherViewModel.callCompositeCallStateStateFlow.collect {
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
    }

    override fun onDestroy() {
        super.onDestroy()
        DismissCompositeButtonView.get(this).hide()
        DismissCompositeButtonView.buttonView = null
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

        /* <ROOMS_SUPPORT:0> */
        val roomId = binding.groupIdOrTeamsMeetingLinkText.text.toString()
        val roomRole = when {
            binding.attendeeRoleRadioButton.isChecked -> CallCompositeParticipantRole.ATTENDEE
            binding.presenterRoleRadioButton.isChecked -> CallCompositeParticipantRole.PRESENTER
            binding.consumerRoleRadioButton.isChecked -> CallCompositeParticipantRole.CONSUMER
            else -> null
        }
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
            /* <ROOMS_SUPPORT:5> */
            roomId,
            roomRole,
            /* </ROOMS_SUPPORT:2> */
            meetingLink,
        )
    }

    private fun showUI() {
        callLauncherViewModel.bringCallCompositeToForeground(this)
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
            DismissCompositeButtonView.get(this).show(callLauncherViewModel)
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
