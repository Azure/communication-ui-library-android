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
class UserReportedIssueHandler : CallCompositeEventHandler<CallCompositeUserReportedIssueEvent> {
    val userIssuesFlow = MutableStateFlow<CallCompositeUserReportedIssueEvent?>(null)

    override fun handle(eventData: CallCompositeUserReportedIssueEvent?) {
        userIssuesFlow.value = eventData
        eventData?.apply {
            sendToServer(
                userMessage,
                screenshot,
                debugInfo.callingUiVersion,
                debugInfo.callingSdkVersion,
                debugInfo.callHistoryRecords,
                debugInfo.logFiles
            )
        }
    }

    private fun sendToServer(
        userMessage: String?,
        screenshot: File?,
        callingUIVersion: String?,
        callingSDKVersion: String?,
        callHistoryRecords: List<CallCompositeCallHistoryRecord>,
        logFiles: List<File>
    ) {
        // If not configured, don't run
        if (SERVER_URL.isBlank()) {
            return
        }
        CoroutineScope(Dispatchers.IO).launch {
            val client = OkHttpClient()
            val requestBody = MultipartBody.Builder().setType(MultipartBody.FORM)

            userMessage?.let {
                requestBody.addFormDataPart("user_message", it)
            }

            screenshot?.let {
                val mediaType = "image/png".toMediaTypeOrNull()
                requestBody.addFormDataPart(
                    "screenshot",
                    it.name,
                    it.asRequestBody(mediaType)
                )
            }

            callingUIVersion?.let {
                requestBody.addFormDataPart("ui_version", it)
            }

            callingSDKVersion?.let {
                requestBody.addFormDataPart("sdk_version", it)
            }

            val callIds = callHistoryRecords.joinToString("\n\n")
            requestBody.addFormDataPart("call_history", callIds)

            logFiles.forEach { file ->
                val mediaType = "text/plain".toMediaTypeOrNull()
                requestBody.addFormDataPart(
                    "log_files",
                    file.name,
                    file.asRequestBody(mediaType)
                )
            }

            val request = Request.Builder()
                .url(SERVER_URL)
                .post(requestBody.build())
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
        // Handle successful ticket creation
    }

    private fun onTicketFailed(error: String) {
        // Handle failed ticket creation
    }

    companion object {
        private const val SERVER_URL = ""
    }
}