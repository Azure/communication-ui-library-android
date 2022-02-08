// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callingcompositedemoapp

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.azure.android.communication.ui.callingcompositedemoapp.databinding.ActivityCallLauncherBinding
import com.azure.android.communication.ui.callingcompositedemoapp.launcher.CallingCompositeLauncher
import com.microsoft.office.outlook.magnifierlib.Magnifier
import com.microsoft.office.outlook.magnifierlib.frame.FPSMonitorConfig
import com.microsoft.office.outlook.magnifierlib.memory.FileDescriptorInfo
import com.microsoft.office.outlook.magnifierlib.memory.HeapMemoryInfo
import com.microsoft.office.outlook.magnifierlib.memory.MemoryMonitor
import com.microsoft.office.outlook.magnifierlib.memory.ThreadInfo
import java.util.UUID

class CallLauncherActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCallLauncherBinding
    private val callLauncherViewModel: CallLauncherViewModel by viewModels()
    private val isTokenFunctionOptionSelected: String = "isTokenFunctionOptionSelected"
    private val isKotlinLauncherOptionSelected: String = "isKotlinLauncherOptionSelected"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
                Magnifier.startMonitorFPS(
                    FPSMonitorConfig.Builder(application).lowPercentage(40 / 60f)  // show red tips, (2.0f / 3.0f) by default
                        .mediumPercentage(50 / 60f) // show yellow tips, (5.0f / 6.0f) by default
                        .refreshRate(60f) // defaultDisplay.refreshRate by default
                        .build()
                )

                Magnifier.startMonitorMemoryTiming(threshold = 6 * 1000, sampleCount = 10, onSampleListener = onSampleListener)
                Magnifier.startMonitorMemoryExceedLimit(
                    threshold =  1000, sampleCount = 3, exceedLimitRatio = 0.1f, onSampleListener = onSampleListener
                )
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

        }

        callLauncherViewModel.fetchResult.observe(this) {
            processResult(it)
        }
    }

    private val onSampleListener = object : MemoryMonitor.OnSampleListener {
        override fun onSampleHeap(
            heapMemoryInfo: HeapMemoryInfo,
            sampleInfo: MemoryMonitor.OnSampleListener.SampleInfo
        ) {
            Log.d("hellllo", "heapMemoryInfo:$heapMemoryInfo,sampleInfo:$sampleInfo")
        }

        override fun onSampleFile(
            fileDescriptorInfo: FileDescriptorInfo,
            sampleInfo: MemoryMonitor.OnSampleListener.SampleInfo
        ) {
            Log.d("hellllo", "fileDescriptorInfo:${fileDescriptorInfo.fdMaxCount},sampleInfo:$sampleInfo")
        }

        override fun onSampleThread(
            threadInfo: ThreadInfo,
            sampleInfo: MemoryMonitor.OnSampleListener.SampleInfo
        ) {
            Log.d("hellllo", "threadInfo:${threadInfo.threadsCount},sampleInfo:$sampleInfo")
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

    private fun saveState(outstate: Bundle?) {
        outstate?.putBoolean(isTokenFunctionOptionSelected, callLauncherViewModel.isTokenFunctionOptionSelected)
        outstate?.putBoolean(isKotlinLauncherOptionSelected, callLauncherViewModel.isKotlinLauncher)
    }
}
