// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models

internal class ToastNotificationModel {
    var notificationIconId: Int = 0
    var notificationMessageId: Int = 0

    fun isEmpty(): Boolean {
        return notificationIconId == 0 && notificationMessageId == 0
    }
}