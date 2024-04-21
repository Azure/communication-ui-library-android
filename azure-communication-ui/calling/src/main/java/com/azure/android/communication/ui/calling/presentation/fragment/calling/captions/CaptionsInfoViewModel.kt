// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.calling.captions

import com.azure.android.communication.ui.calling.presentation.manager.CaptionsDataManager
import com.azure.android.communication.ui.calling.redux.state.CallingStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Timer
import java.util.TimerTask

internal class CaptionsInfoViewModel {
    private lateinit var displayCaptionsInfoViewFlow: MutableStateFlow<Boolean>
    private lateinit var timer: Timer
    private var captionsDataManager: CaptionsDataManager? = null

    fun getCaptionsDataSharedFlow() = captionsDataManager?.getCaptionsDataReceivedSharedFlow()
    fun getDisplayCaptionsInfoViewFlow(): StateFlow<Boolean> = displayCaptionsInfoViewFlow

    fun update(
        callingStatus: CallingStatus
    ) {
        // displayCaptionsInfoViewFlow.value = !isOverlayDisplayed(callingStatus)
    }

    fun init(
        callingStatus: CallingStatus
    ) {
        timer = Timer()
        displayCaptionsInfoViewFlow = MutableStateFlow(isOverlayDisplayed(callingStatus))
    }

    fun showCaptionsData() {
        timer.cancel()
        displayCaptionsInfoViewFlow.value = true
        timer = Timer()
        timer.schedule(
            object : TimerTask() {
                override fun run() {
                    displayCaptionsInfoViewFlow.value = false
                }
            },
            9000
        )
    }

    fun dismiss() {
        if (displayCaptionsInfoViewFlow.value) {
            displayCaptionsInfoViewFlow.value = false
            timer.cancel()
            return
        }
    }

    private fun isOverlayDisplayed(callingStatus: CallingStatus) =
        callingStatus == CallingStatus.IN_LOBBY || callingStatus == CallingStatus.LOCAL_HOLD

    fun setCaptionsDataManager(captionsViewManager: CaptionsDataManager) {
        this.captionsDataManager = captionsViewManager
    }
}
