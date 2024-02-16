package com.azure.android.communication.ui.callingcompositedemoapp

import com.azure.android.communication.ui.calling.CallCompositeEventHandler
import com.azure.android.communication.ui.calling.models.CallCompositeCallHistoryRecord
import com.azure.android.communication.ui.calling.models.CallCompositeUserReportedIssueEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.Response
import java.io.File
import java.io.IOException

/**
 * This class is used to handle user reported issues.
 *
 * It offers a flow that can be used to observe user reported issues.
 */
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class UserReportedIssueHandler : CallCompositeEventHandler<CallCompositeUserReportedIssueEvent> {
    val userIssuesFlow = MutableStateFlow<CallCompositeUserReportedIssueEvent?>(null)
    lateinit var context : Application
    private val notificationManager by lazy { NotificationManagerCompat.from(context) }


    override fun handle(eventData: CallCompositeUserReportedIssueEvent?) {
        createNotificationChannel()
        userIssuesFlow.value = eventData
        eventData?.apply {
            sendToServer(
                userMessage,
                debugInfo.versions.azureCallingUILibrary,
                debugInfo.versions.azureCallingLibrary,
                debugInfo.callHistoryRecords,
                debugInfo.logFiles
            )
        }
    }

    private fun sendToServer(
        userMessage: String?,
        callingUIVersion: String?,
        callingSDKVersion: String?,
        callHistoryRecords: List<CallCompositeCallHistoryRecord>,
        logFiles: List<File>
    ) {
        // If not configured, don't run
        if (SERVER_URL.isBlank()) {
            return
        }
        showProgressNotification()
        CoroutineScope(Dispatchers.IO).launch {
            val client = OkHttpClient()
            val requestBody = MultipartBody.Builder().setType(MultipartBody.FORM).apply {
                userMessage?.let { addFormDataPart("user_message", it) }
                callingUIVersion?.let { addFormDataPart("ui_version", it) }
                callingSDKVersion?.let { addFormDataPart("sdk_version", it) }
                addFormDataPart("call_history", callHistoryRecords.joinToString("\n\n"))
                logFiles.filter { it.length() > 0 }.forEach { file ->
                    val mediaType = "application/octet-stream".toMediaTypeOrNull()
                    addFormDataPart("log_files", file.name, file.asRequestBody(mediaType))
                }
            }.build()

            val request = Request.Builder()
                .url(SERVER_URL)
                .post(requestBody)
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    CoroutineScope(Dispatchers.Main).launch {
                        onTicketFailed(e.message ?: "Unknown error")
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    CoroutineScope(Dispatchers.Main).launch {
                        if (response.isSuccessful) {
                            onTicketCreated(response.body?.string() ?: "No URL provided")
                        } else {
                            onTicketFailed("Server error: ${response.message}")
                        }
                    }
                }
            })
        }
    }

    private fun onTicketCreated(url: String) {
        showCompletionNotification(url)
    }

    private fun onTicketFailed(error: String) {
        showErrorNotification(error)
    }

    companion object {
        private const val SERVER_URL = "http://192.168.1.65:3000/receiveEvent"
    }


    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Report Submission"
            val descriptionText = "Notifications for report submission status"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("report_submission_channel", name, importance).apply {
                description = descriptionText
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun showProgressNotification() {
        val notification = NotificationCompat.Builder(context, "report_submission_channel")
            .setContentTitle("Submitting Report")
            .setContentText("Your report is being submitted...")
            .setSmallIcon(R.drawable.image_monkey)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setProgress(0, 0, true) // Indeterminate progress
            .build()

        notificationManager.notify(1, notification)
    }

    private fun showCompletionNotification(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, "report_submission_channel")
            .setContentTitle("Report Submitted")
            .setContentText("Tap to view")
            .setSmallIcon(R.drawable.image_monkey) // Replace with your completion icon
            .setContentIntent(pendingIntent)
            .setAutoCancel(true) // Remove notification after tap
            .build()

        notificationManager.notify(1, notification)
    }

    private fun showErrorNotification(error: String) {
        val notification = NotificationCompat.Builder(context, "report_submission_channel")
            .setContentTitle("Submission Error")
            .setContentText("Error submitting report\nReason: $error")
            .setSmallIcon(R.drawable.image_monkey) // Replace with your completion icon
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        notificationManager.notify(1, notification)
    }

}