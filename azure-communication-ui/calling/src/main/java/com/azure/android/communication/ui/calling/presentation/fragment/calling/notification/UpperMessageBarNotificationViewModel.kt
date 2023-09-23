// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.calling.notification

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

internal class UpperMessageBarNotificationViewModel {
    private lateinit var displayUpperMessageBarNotificationFlow: MutableStateFlow<Boolean>
    private lateinit var isOverlayDisplayedFlow: MutableStateFlow<Boolean>
    private lateinit var upperMessageBarNotificationModelFlow: MutableStateFlow<UpperMessageBarNotificationModel>

    fun getIsOverlayDisplayedFlow(): StateFlow<Boolean> = isOverlayDisplayedFlow

    fun getDisplayUpperMessageBarNotificationFlow(): StateFlow<Boolean> = displayUpperMessageBarNotificationFlow

    fun getUpperMessageBarNotificationModelFlow(): StateFlow<UpperMessageBarNotificationModel> = upperMessageBarNotificationModelFlow

    fun init() {
        displayUpperMessageBarNotificationFlow = MutableStateFlow(false)
        isOverlayDisplayedFlow = MutableStateFlow(true)
        upperMessageBarNotificationModelFlow = MutableStateFlow(UpperMessageBarNotificationModel())
    }


    fun dismiss() {
        if (displayUpperMessageBarNotificationFlow.value) {
            displayUpperMessageBarNotificationFlow.value = false
            return
        }
    }

    fun setUpperMessageBarNotificationModel(upperMessageBarNotificationModel : UpperMessageBarNotificationModel){
        displayUpperMessageBarNotificationFlow.value = true
        upperMessageBarNotificationModelFlow.value = upperMessageBarNotificationModel
    }
}
