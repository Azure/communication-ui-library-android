// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callingcompositedemoapp

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.azure.android.communication.ui.calling.models.CallCompositeRoomRole
import com.azure.android.communication.ui.callingcompositedemoapp.databinding.ActivityCallLauncherBinding
import com.azure.android.communication.ui.callingcompositedemoapp.features.AdditionalFeatures
import com.azure.android.communication.ui.callingcompositedemoapp.features.FeatureFlags
import com.azure.android.communication.ui.callingcompositedemoapp.features.conditionallyRegisterDiagnostics
import com.azure.android.communication.ui.callingcompositedemoapp.launcher.CallingCompositeLauncher
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.crashes.Crashes
import com.microsoft.appcenter.distribute.Distribute
import java.util.UUID

class CallLauncherActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCallLauncherBinding
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
        FeatureFlags.registerAdditionalFeature(AdditionalFeatures.secondaryThemeFeature)

        binding = ActivityCallLauncherBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val data: Uri? = intent?.data
        val deeplinkAcsToken = data?.getQueryParameter("acstoken")
        val deeplinkName = data?.getQueryParameter("name")
        val deeplinkGroupId = data?.getQueryParameter("groupid")
        val deeplinkTeamsUrl = data?.getQueryParameter("teamsurl")
        val deepLinkRoomsId = data?.getQueryParameter("roomsid")

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
                roomsMeetingRadioButton.isChecked = false
            } else if (!deeplinkTeamsUrl.isNullOrEmpty()) {
                groupIdOrTeamsMeetingLinkText.setText(deeplinkTeamsUrl)
                groupCallRadioButton.isChecked = false
                teamsMeetingRadioButton.isChecked = true
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
                    roomsMeetingRadioButton.isChecked = false
                    attendeeRoleRadioButton.visibility = View.GONE
                    presenterRoleRadioButton.visibility = View.GONE
                }
            }
            teamsMeetingRadioButton.setOnClickListener {
                if (teamsMeetingRadioButton.isChecked) {
                    groupIdOrTeamsMeetingLinkText.setText(BuildConfig.TEAMS_MEETING_LINK)
                    groupCallRadioButton.isChecked = false
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
                    callLauncherViewModel.setAttendeeRole()
                    groupCallRadioButton.isChecked = false
                    teamsMeetingRadioButton.isChecked = false
                } else {
                    presenterRoleRadioButton.visibility = View.GONE
                    attendeeRoleRadioButton.visibility = View.GONE
                }
            }

            presenterRoleRadioButton.setOnClickListener {
                if (presenterRoleRadioButton.isChecked) {
                    callLauncherViewModel.setPresenterRole()
                    attendeeRoleRadioButton.isChecked = false
                }
            }

            attendeeRoleRadioButton.setOnClickListener {
                if (attendeeRoleRadioButton.isChecked) {
                    callLauncherViewModel.setAttendeeRole()
                    presenterRoleRadioButton.isChecked = false
                }
            }


            javaButton.setOnClickListener {
                callLauncherViewModel.setJavaLauncher()
            }

            kotlinButton.setOnClickListener {
                callLauncherViewModel.setKotlinLauncher()
            }

            if (BuildConfig.DEBUG) {
                versionText.text = "${BuildConfig.VERSION_NAME}"
            } else {
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
            launcher.launch(
                this@CallLauncherActivity,
                userName,
                groupId,
                null,
                null,
                null,
                ::showAlert
            )
        }

        if (binding.teamsMeetingRadioButton.isChecked) {
            val meetingLink = binding.groupIdOrTeamsMeetingLinkText.text.toString()

            if (meetingLink.isBlank()) {
                val message = "Teams meeting link is invalid or empty."
                showAlert(message)
                return
            }

            launcher.launch(
                this@CallLauncherActivity,
                userName,
                null,
                meetingLink,
                null,
                null,
                ::showAlert,
            )
        }

        if (binding.roomsMeetingRadioButton.isChecked) {
            val roomsId = binding.groupIdOrTeamsMeetingLinkText.text.toString()

            if(roomsId.isBlank()) {
                val message = "Rooms Id is invalid or empty"
                showAlert(message)
                return
            }

            var roomRole = CallCompositeRoomRole.ATTENDEE

            if (callLauncherViewModel.isPresenterRole) {
                roomRole = CallCompositeRoomRole.PRESENTER
            }

            launcher.launch(
                this@CallLauncherActivity,
                userName,
                null,
                null,
                roomsId,
                roomRole,
                ::showAlert
            )
        }
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
