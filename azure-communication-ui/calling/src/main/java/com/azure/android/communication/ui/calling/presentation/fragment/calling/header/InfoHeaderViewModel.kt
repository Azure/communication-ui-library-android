// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.calling.header

/* <CUSTOM_CALL_HEADER> */
import com.azure.android.communication.ui.calling.implementation.R
import com.azure.android.communication.ui.calling.presentation.manager.CallDurationManager
/* </CUSTOM_CALL_HEADER> */
import com.azure.android.communication.ui.calling.redux.state.CallingStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Timer
import java.util.TimerTask

internal class InfoHeaderViewModel(
    val multitaskingEnabled: Boolean,
    /* <CUSTOM_CALL_HEADER> */
    private val callDurationManager: CallDurationManager? = null,
    private val customTitle: String? = null
    /* </CUSTOM_CALL_HEADER> */
) {
    private lateinit var displayFloatingHeaderFlow: MutableStateFlow<Boolean>
    private lateinit var isOverlayDisplayedFlow: MutableStateFlow<Boolean>
    private lateinit var numberOfParticipantsFlow: MutableStateFlow<Int>

    private lateinit var timer: Timer
    private lateinit var requestCallEndCallback: () -> Unit

    private var displayedOnLaunch = false
    /* <CUSTOM_CALL_HEADER> */
    fun getCustomTitle(): String? {
        return customTitle
    }

    fun getCallDurationManager(): CallDurationManager? {
        return callDurationManager
    }
    /* </CUSTOM_CALL_HEADER> */
    fun getIsOverlayDisplayedFlow(): StateFlow<Boolean> = isOverlayDisplayedFlow

    fun getDisplayFloatingHeaderFlow(): StateFlow<Boolean> = displayFloatingHeaderFlow

    fun getNumberOfParticipantsFlow(): StateFlow<Int> {
        return numberOfParticipantsFlow
    }

    fun update(
        numberOfRemoteParticipants: Int,
    ) {
        numberOfParticipantsFlow.value = numberOfRemoteParticipants
        if (!displayedOnLaunch) {
            displayedOnLaunch = true
            switchFloatingHeader()
        }
    }

    /* <CUSTOM_CALL_HEADER> */
    fun getFormattedElapsedDuration(): String {
        val elapsedDuration = callDurationManager?.getElapsedDuration() ?: 0L

        // Calculate elapsed time components
        val seconds = (elapsedDuration / 1000) % 60
        val minutes = (elapsedDuration / (1000 * 60)) % 60
        val hours = (elapsedDuration / (1000 * 60 * 60)) % 24

        // Determine format string based on the highest non-zero time component
        val formatString = when {
            hours > 0 -> resources.getString(R.string.azure_communication_ui_calling_view_info_header_call_timer_format_with_hours)
            minutes > 0 -> resources.getString(R.string.azure_communication_ui_calling_view_info_header_call_timer_format_with_minutes)
            else -> resources.getString(R.string.azure_communication_ui_calling_view_info_header_call_timer_format_with_seconds)
        }

        // Format the duration based on the calculated components
        return when {
            hours > 0 -> String.format(formatString, hours, minutes, seconds)
            minutes > 0 -> String.format(formatString, minutes, seconds)
            else -> String.format(formatString, seconds)
        }
    }

    /* </CUSTOM_CALL_HEADER> */

    fun updateIsOverlayDisplayed(callingStatus: CallingStatus) {
        isOverlayDisplayedFlow.value = isOverlayDisplayed(callingStatus)
    }

    fun init(
        callingStatus: CallingStatus,
        numberOfRemoteParticipants: Int,
        requestCallEndCallback: () -> Unit,
    ) {
        timer = Timer()
        displayFloatingHeaderFlow = MutableStateFlow(false)
        numberOfParticipantsFlow = MutableStateFlow(numberOfRemoteParticipants)
        isOverlayDisplayedFlow = MutableStateFlow(isOverlayDisplayed(callingStatus))
        this.requestCallEndCallback = requestCallEndCallback
    }

    fun switchFloatingHeader() {
        if (displayFloatingHeaderFlow.value) {
            displayFloatingHeaderFlow.value = false
            timer.cancel()

            return
        }
        displayFloatingHeaderFlow.value = true
        timer = Timer()
        timer.schedule(
            object : TimerTask() {
                override fun run() {
                    displayFloatingHeaderFlow.value = false
                }
            },
            3000
        )
    }

    fun dismiss() {
        if (displayFloatingHeaderFlow.value) {
            displayFloatingHeaderFlow.value = false
            timer.cancel()
            return
        }
    }

    private fun isOverlayDisplayed(callingStatus: CallingStatus) =
        callingStatus == CallingStatus.IN_LOBBY || callingStatus == CallingStatus.LOCAL_HOLD

    fun requestCallEnd() {
        requestCallEndCallback()
    }
}
