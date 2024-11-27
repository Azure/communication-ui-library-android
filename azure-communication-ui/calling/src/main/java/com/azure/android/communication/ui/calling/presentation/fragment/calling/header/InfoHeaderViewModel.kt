// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.calling.header

/* <CALL_SCREEN_HEADER_CUSTOM_BUTTONS:0> */
import android.content.Context
import com.azure.android.communication.ui.calling.logger.Logger
import com.azure.android.communication.ui.calling.models.createCustomButtonClickEvent
import com.azure.android.communication.ui.calling.presentation.manager.UpdatableOptionsManager
import com.azure.android.communication.ui.calling.redux.state.ButtonState
/* </CALL_SCREEN_HEADER_CUSTOM_BUTTONS> */
import com.azure.android.communication.ui.calling.redux.state.CallScreenInfoHeaderState
import com.azure.android.communication.ui.calling.redux.state.CallingStatus
import com.azure.android.communication.ui.calling.redux.state.RttState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

internal class InfoHeaderViewModel(
    val multitaskingEnabled: Boolean,
    /* <CALL_SCREEN_HEADER_CUSTOM_BUTTONS:0> */
    private val updatableOptionsManager: UpdatableOptionsManager,
    private val logger: Logger,
    /* </CALL_SCREEN_HEADER_CUSTOM_BUTTONS> */
) {
    private lateinit var displayFloatingHeaderFlow: MutableStateFlow<Boolean>
    private lateinit var isOverlayDisplayedFlow: MutableStateFlow<Boolean>
    private lateinit var numberOfParticipantsFlow: MutableStateFlow<Int>

    private lateinit var requestCallEndCallback: () -> Unit

    private var displayedOnLaunch = false
    private lateinit var titleStateFlow: MutableStateFlow<String?>
    private lateinit var subtitleStateFlow: MutableStateFlow<String?>

    /* <CALL_SCREEN_HEADER_CUSTOM_BUTTONS:0> */
    private var buttonState: ButtonState? = null
    private lateinit var customButton1MutableStateFlow: MutableStateFlow<CustomButtonEntry?>
    private lateinit var customButton2MutableStateFlow: MutableStateFlow<CustomButtonEntry?>
    /* </CALL_SCREEN_HEADER_CUSTOM_BUTTONS> */

    fun getTitleStateFlow(): StateFlow<String?> = titleStateFlow
    fun getSubtitleStateFlow(): StateFlow<String?> = subtitleStateFlow

    fun getIsOverlayDisplayedFlow(): StateFlow<Boolean> = isOverlayDisplayedFlow

    fun getDisplayFloatingHeaderFlow(): StateFlow<Boolean> = displayFloatingHeaderFlow

    fun getNumberOfParticipantsFlow(): StateFlow<Int> = numberOfParticipantsFlow

    /* <CALL_SCREEN_HEADER_CUSTOM_BUTTONS:0> */
    fun getCustomButton1StateFlow(): StateFlow<CustomButtonEntry?> = customButton1MutableStateFlow
    fun getCustomButton2StateFlow(): StateFlow<CustomButtonEntry?> = customButton2MutableStateFlow
    /* </CALL_SCREEN_HEADER_CUSTOM_BUTTONS> */

    fun init(
        callingStatus: CallingStatus,
        numberOfRemoteParticipants: Int,
        callScreenInfoHeaderState: CallScreenInfoHeaderState,
        /* <CALL_SCREEN_HEADER_CUSTOM_BUTTONS:0> */
        buttonState: ButtonState,
        /* </CALL_SCREEN_HEADER_CUSTOM_BUTTONS> */
        requestCallEndCallback: () -> Unit,
        rttState: RttState,
    ) {
        titleStateFlow = MutableStateFlow(callScreenInfoHeaderState.title)
        subtitleStateFlow = MutableStateFlow(callScreenInfoHeaderState.subtitle)
        displayFloatingHeaderFlow = MutableStateFlow(false)
        numberOfParticipantsFlow = MutableStateFlow(numberOfRemoteParticipants)
        isOverlayDisplayedFlow = MutableStateFlow(isOverlayDisplayed(callingStatus, rttState))
        this.requestCallEndCallback = requestCallEndCallback
        /* <CALL_SCREEN_HEADER_CUSTOM_BUTTONS:0> */
        this.buttonState = buttonState
        customButton1MutableStateFlow = MutableStateFlow(null)
        customButton2MutableStateFlow = MutableStateFlow(null)
        updateCustomButtonsState(buttonState)
        /* </CALL_SCREEN_HEADER_CUSTOM_BUTTONS> */
    }

    fun update(
        numberOfRemoteParticipants: Int,
        callScreenInfoHeaderState: CallScreenInfoHeaderState,
        /* <CALL_SCREEN_HEADER_CUSTOM_BUTTONS:0> */
        buttonState: ButtonState,
        /* </CALL_SCREEN_HEADER_CUSTOM_BUTTONS> */
        callingStatus: CallingStatus,
        rttState: RttState,
    ) {
        /* <CALL_SCREEN_HEADER_CUSTOM_BUTTONS:0> */
        this.buttonState = buttonState
        /* </CALL_SCREEN_HEADER_CUSTOM_BUTTONS> */
        titleStateFlow.value = callScreenInfoHeaderState.title
        subtitleStateFlow.value = callScreenInfoHeaderState.subtitle
        numberOfParticipantsFlow.value = numberOfRemoteParticipants
        if (!displayedOnLaunch) {
            displayedOnLaunch = true
            switchFloatingHeader()
        }
        /* <CALL_SCREEN_HEADER_CUSTOM_BUTTONS:0> */
        updateCustomButtonsState(buttonState)
        /* </CALL_SCREEN_HEADER_CUSTOM_BUTTONS> */

        isOverlayDisplayedFlow.value = isOverlayDisplayed(callingStatus, rttState)
    }

    /* <CALL_SCREEN_HEADER_CUSTOM_BUTTONS:0> */
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
    /* </CALL_SCREEN_HEADER_CUSTOM_BUTTONS> */

    fun switchFloatingHeader() {
        displayFloatingHeaderFlow.value = !displayFloatingHeaderFlow.value
    }

    fun dismiss() {
        displayFloatingHeaderFlow.value = false
    }

    private fun isOverlayDisplayed(callingStatus: CallingStatus,
                                   rttState: RttState,
                                   ): Boolean {
        return callingStatus == CallingStatus.IN_LOBBY ||
                callingStatus == CallingStatus.LOCAL_HOLD ||
                rttState.isMaximized
    }

    fun requestCallEnd() {
        requestCallEndCallback()
    }

    /* <CALL_SCREEN_HEADER_CUSTOM_BUTTONS:0> */
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

    data class CustomButtonEntry(
        val id: String,
        val titleText: String,
        val icon: Int,
        val isVisible: Boolean,
        val isEnabled: Boolean,
    )
    /* </CALL_SCREEN_HEADER_CUSTOM_BUTTONS> */
}
