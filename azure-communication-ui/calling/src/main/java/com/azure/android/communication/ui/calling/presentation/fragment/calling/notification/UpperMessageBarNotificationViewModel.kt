// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.calling.notification

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

internal class UpperMessageBarNotificationViewModel {
    private lateinit var upperMessageBarNotificationModelFlow: MutableStateFlow<UpperMessageBarNotificationModel>
    lateinit var dismissNotification: () -> Unit

    fun getUpperMessageBarNotificationModelFlow(): StateFlow<UpperMessageBarNotificationModel> = upperMessageBarNotificationModelFlow

    fun init(upperMessageBarNotificationModel: UpperMessageBarNotificationModel, dismissNotificationCallback: () -> Unit) {
        upperMessageBarNotificationModelFlow = MutableStateFlow(upperMessageBarNotificationModel)
        dismissNotification = dismissNotificationCallback
    }
}
