// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.calling.header

import android.content.Context
import com.azure.android.communication.ui.calling.logger.Logger
import com.azure.android.communication.ui.calling.models.createCustomButtonClickEvent
import com.azure.android.communication.ui.calling.presentation.manager.UpdatableOptionsManager
import com.azure.android.communication.ui.calling.redux.state.ButtonState
import com.azure.android.communication.ui.calling.redux.state.CallScreenInfoHeaderState
import com.azure.android.communication.ui.calling.redux.state.CallingStatus
import com.azure.android.communication.ui.calling.redux.state.RttState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
/* <CALL_START_TIME> */
import java.util.Date
import java.util.Locale
import java.util.Timer
import kotlin.concurrent.fixedRateTimer
/* </CALL_START_TIME> */

internal class InfoHeaderViewModel(
    val multitaskingEnabled: Boolean,
    private val updatableOptionsManager: UpdatableOptionsManager,
    private val logger: Logger,
) {
    private lateinit var displayFloatingHeaderFlow: MutableStateFlow<Boolean>
    private lateinit var isOverlayDisplayedFlow: MutableStateFlow<Boolean>
    private lateinit var numberOfParticipantsFlow: MutableStateFlow<Int>
    /* <CALL_START_TIME> */
    private lateinit var isCallDurationDisplayedFlow: MutableStateFlow<Boolean>
    private lateinit var callDurationFlow: MutableStateFlow<String>
    /* </CALL_START_TIME> */
    private lateinit var requestCallEndCallback: () -> Unit

    private var displayedOnLaunch = false
    private lateinit var titleStateFlow: MutableStateFlow<String?>
    private lateinit var subtitleStateFlow: MutableStateFlow<String?>

    private var buttonState: ButtonState? = null
    private lateinit var customButton1MutableStateFlow: MutableStateFlow<CustomButtonEntry?>
    private lateinit var customButton2MutableStateFlow: MutableStateFlow<CustomButtonEntry?>
    /* <CALL_START_TIME> */
    private var callDurationTimer: Timer? = null
    /* </CALL_START_TIME> */

    fun getTitleStateFlow(): StateFlow<String?> = titleStateFlow
    fun getSubtitleStateFlow(): StateFlow<String?> = subtitleStateFlow
    /* <CALL_START_TIME> */
    fun getDisplayCallDurationFlow(): StateFlow<Boolean> = isCallDurationDisplayedFlow
    fun getCallDurationFlow(): StateFlow<String> = callDurationFlow
    /* </CALL_START_TIME> */
    fun getIsOverlayDisplayedFlow(): StateFlow<Boolean> = isOverlayDisplayedFlow
    fun getDisplayFloatingHeaderFlow(): StateFlow<Boolean> = displayFloatingHeaderFlow
    fun getNumberOfParticipantsFlow(): StateFlow<Int> = numberOfParticipantsFlow
    fun getCustomButton1StateFlow(): StateFlow<CustomButtonEntry?> = customButton1MutableStateFlow
    fun getCustomButton2StateFlow(): StateFlow<CustomButtonEntry?> = customButton2MutableStateFlow

    fun init(
        callingStatus: CallingStatus,
        numberOfRemoteParticipants: Int,
        callScreenInfoHeaderState: CallScreenInfoHeaderState,
        buttonState: ButtonState,
        rttState: RttState,
        requestCallEndCallback: () -> Unit,
        /* <CALL_START_TIME> */
        callStartTime: Date?,
        /* </CALL_START_TIME> */
    ) {
        titleStateFlow = MutableStateFlow(callScreenInfoHeaderState.title)
        subtitleStateFlow = MutableStateFlow(callScreenInfoHeaderState.subtitle)
        displayFloatingHeaderFlow = MutableStateFlow(false)
        numberOfParticipantsFlow = MutableStateFlow(numberOfRemoteParticipants)
        isOverlayDisplayedFlow = MutableStateFlow(isOverlayDisplayed(callingStatus, rttState))
        this.requestCallEndCallback = requestCallEndCallback
        this.buttonState = buttonState
        customButton1MutableStateFlow = MutableStateFlow(null)
        customButton2MutableStateFlow = MutableStateFlow(null)
        updateCustomButtonsState(buttonState)
        /* <CALL_START_TIME> */
        isCallDurationDisplayedFlow = MutableStateFlow(shouldDisplayCallDuration(callScreenInfoHeaderState, callStartTime))
        callDurationFlow = MutableStateFlow("00:00")
        /* </CALL_START_TIME> */
    }

    fun update(
        numberOfRemoteParticipants: Int,
        callScreenInfoHeaderState: CallScreenInfoHeaderState,
        buttonState: ButtonState,
        callingStatus: CallingStatus,
        rttState: RttState,
        /* <CALL_START_TIME> */
        callStartTime: Date?,
        /* </CALL_START_TIME> */
    ) {
        this.buttonState = buttonState
        titleStateFlow.value = callScreenInfoHeaderState.title
        subtitleStateFlow.value = callScreenInfoHeaderState.subtitle
        numberOfParticipantsFlow.value = numberOfRemoteParticipants
        /* <CALL_START_TIME> */
        isCallDurationDisplayedFlow.value = shouldDisplayCallDuration(callScreenInfoHeaderState, callStartTime)
        if (isCallDurationDisplayedFlow.value) {
            callStartTime?.let { startTimer(it) }
        } else {
            stopTimer()
            callDurationFlow.value = ""
        }
        /* </CALL_START_TIME> */
        if (!displayedOnLaunch) {
            displayedOnLaunch = true
            switchFloatingHeader()
        }
        updateCustomButtonsState(buttonState)
        isOverlayDisplayedFlow.value = isOverlayDisplayed(callingStatus, rttState)
    }

    fun switchFloatingHeader() {
        displayFloatingHeaderFlow.value = !displayFloatingHeaderFlow.value
    }

    fun dismiss() {
        displayFloatingHeaderFlow.value = false
    }

    fun requestCallEnd() {
        requestCallEndCallback()
    }

    fun onCustomButtonClicked(context: Context, id: String) {
        try {
            val buttonViewData = updatableOptionsManager.getButton(id)
            buttonViewData.onClickHandler?.handle(
                createCustomButtonClickEvent(context, buttonViewData)
            )
        } catch (e: Exception) {
            logger.error("Call screen control bar custom button onClick exception.", e)
        }
    }

    /* <CALL_START_TIME> */
    private fun startTimer(callStartDate: Date) {
        if (callDurationTimer != null) {
            return
        }
        callDurationTimer = fixedRateTimer(name = "Timer", initialDelay = 0, period = 1000) {
            val currentTime = Date()
            val elapsedTimeMillis = currentTime.time - callStartDate.time
            val hours = (elapsedTimeMillis / (1000 * 60 * 60)).toInt()
            val minutes = (elapsedTimeMillis / (1000 * 60) % 60).toInt()
            val seconds = (elapsedTimeMillis / 1000 % 60).toInt()

            val formattedTime = if (hours > 0) {
                String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds)
            } else {
                String.format(Locale.US, "%02d:%02d", minutes, seconds)
            }
            callDurationFlow.value = formattedTime
        }
    }

    private fun stopTimer() {
        callDurationTimer?.cancel()
        callDurationTimer = null
    }

    private fun shouldDisplayCallDuration(
        callScreenInfoHeaderState: CallScreenInfoHeaderState,
        callStartTime: Date?
    ) = callScreenInfoHeaderState.showCallDuration && callStartTime != null
    /* </CALL_START_TIME> */

    private fun updateCustomButtonsState(buttonState: ButtonState) {
        buttonState.callScreenHeaderCustomButtonsState.firstOrNull()?.let {
            val customButtonEntry = CustomButtonEntry(
                id = it.id ?: "",
                titleText = it.title ?: "",
                icon = it.drawableId,
                isVisible = it.isVisible ?: false,
                isEnabled = it.isEnabled ?: false,
            )
            customButton1MutableStateFlow.value = customButtonEntry
        }

        if (buttonState.callScreenHeaderCustomButtonsState.size > 1) {
            buttonState.callScreenHeaderCustomButtonsState[1].let {
                val customButtonEntry = CustomButtonEntry(
                    id = it.id ?: "",
                    titleText = it.title ?: "",
                    icon = it.drawableId,
                    isVisible = it.isVisible ?: false,
                    isEnabled = it.isEnabled ?: false,
                )
                customButton2MutableStateFlow.value = customButtonEntry
            }
        }
    }

    private fun isOverlayDisplayed(
        callingStatus: CallingStatus,
        rttState: RttState,
    ) = callingStatus == CallingStatus.IN_LOBBY ||
            callingStatus == CallingStatus.LOCAL_HOLD ||
            rttState.isMaximized

    data class CustomButtonEntry(
        val id: String,
        val titleText: String,
        val icon: Int,
        val isVisible: Boolean,
        val isEnabled: Boolean,
    )
}
