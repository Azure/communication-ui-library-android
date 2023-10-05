// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models

import android.view.View

internal class UpperMessageBarNotificationModel {
    var notificationIconId: Int = 0
    var notificationMessageId: Int = 0
    var notificationView: View? = null

    lateinit var mediaCallDiagnostic: MediaCallDiagnostic

    fun isEmpty(): Boolean {
        return notificationIconId == 0 && notificationMessageId == 0 && notificationView == null
    }
}