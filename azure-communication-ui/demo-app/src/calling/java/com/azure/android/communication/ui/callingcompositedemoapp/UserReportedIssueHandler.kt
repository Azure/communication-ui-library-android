package com.azure.android.communication.ui.callingcompositedemoapp

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
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
import org.threeten.bp.format.DateTimeFormatter
import java.io.File
import java.io.IOException

/**
 * This class is responsible for handling user-reported issues within the Azure Communication Services Calling UI composite.
 * It implements the CallCompositeEventHandler interface to listen for CallCompositeUserReportedIssueEvents.
 * The class demonstrates how to send diagnostic information to a server endpoint for support purposes and
 * how to provide user feedback through notifications.
 */
class UserReportedIssueHandler : CallCompositeEventHandler<CallCompositeUserReportedIssueEvent> {
    // Flow to observe user reported issues.
    val userIssuesFlow = MutableStateFlow<CallCompositeUserReportedIssueEvent?>(null)

    // Reference to the application context, used to display notifications.
    lateinit var context: Application

    // Lazy initialization of the NotificationManagerCompat for managing notifications.
    private val notificationManager by lazy { NotificationManagerCompat.from(context) }

    /**
     * Handles the event when a user reports an issue.
     * - Creates a notification channel for Android O and above.
     * - Updates the userIssuesFlow with the new event data.
     * - Sends the event data including user message, app and SDK versions, call history, and log files to a server.
     */
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

    /**
     * Prepares and sends a POST request to a server with the user-reported issue data.
     * Constructs a multipart request body containing the user message, app versions, call history, and log files.
     */
    private fun sendToServer(
        userMessage: String?,
        callingUIVersion: String?,
        callingSDKVersion: String?,
        callHistoryRecords: List<CallCompositeCallHistoryRecord>,
        logFiles: List<File>
    ) {
        if (SERVER_URL.isBlank()) { // Check if the server URL is configured.
            return
        }
        showProgressNotification()
        CoroutineScope(Dispatchers.IO).launch {
            val client = OkHttpClient()
            val requestBody = MultipartBody.Builder().setType(MultipartBody.FORM).apply {
                userMessage?.let { addFormDataPart("user_message", it) }
                callingUIVersion?.let { addFormDataPart("ui_version", it) }
                callingSDKVersion?.let { addFormDataPart("sdk_version", it) }
                addFormDataPart(
                    "call_history",
                    callHistoryRecords.map { "\n\n${it.callStartedOn.format(DateTimeFormatter.BASIC_ISO_DATE)}\n${it.callIds.joinToString("\n")}" }
                        .joinToString("\n")
                )
                logFiles.filter { it.length() > 0 }.forEach { file ->
                    val mediaType = "application/octet-stream".toMediaTypeOrNull()
                    addFormDataPart("log_files", file.name, file.asRequestBody(mediaType))
                }
            }.build()

            val request = Request.Builder()
                .url("$SERVER_URL/receiveEvent")
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

    /**
     * Displays a notification indicating that the issue ticket has been created successfully.
     * The notification includes a URL to view the ticket status, provided by the server response.
     */
    private fun onTicketCreated(url: String) {
        showCompletionNotification(url)
    }

    /**
     * Displays a notification indicating that the submission of the issue ticket failed.
     * The notification includes the error reason.
     */
    private fun onTicketFailed(error: String) {
        showErrorNotification(error)
    }

    companion object {
        // The server URL to which the user-reported issues will be sent. Must be configured.
        private const val SERVER_URL = BuildConfig.SUPPORT_END_POINT_URL
    }

    /**
     * Creates a notification channel for Android O and above.
     * This is necessary to display notifications on these versions of Android.
     */
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

    /**
     * Shows a notification indicating that the report submission is in progress.
     * This uses an indeterminate progress indicator to signify ongoing activity.
     */
    private fun showProgressNotification() {
        val notification = NotificationCompat.Builder(context, "report_submission_channel")
            .setContentTitle("Submitting Report")
            .setContentText("Your report is being submitted...")
            .setSmallIcon(R.drawable.image_monkey) // Replace with an appropriate icon for your app
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setProgress(0, 0, true) // Indeterminate progress
            .build()

        notificationManager.notify(1, notification)
    }

    /**
     * Shows a notification indicating that the report has been successfully submitted.
     * The notification includes an action to view the report status via a provided URL.
     */
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
            .setSmallIcon(R.drawable.image_monkey) // Replace with an appropriate icon for your app
            .setContentIntent(pendingIntent)
            .setAutoCancel(true) // Removes notification after tap
            .build()

        notificationManager.notify(1, notification)
    }

    /**
     * Shows a notification indicating an error in submitting the report.
     * The notification includes the reason for the submission failure.
     */
    private fun showErrorNotification(error: String) {
        val notification = NotificationCompat.Builder(context, "report_submission_channel")
            .setContentTitle("Submission Error")
            .setContentText("Error submitting report\nReason: $error")
            .setSmallIcon(R.drawable.image_monkey) // Replace with an appropriate icon for your app
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        notificationManager.notify(1, notification)
    }
}
