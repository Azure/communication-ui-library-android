// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models

internal class ToastNotificationModel(
    val notificationIconId: Int,
    val notificationMessageId: Int,
    val networkCallDiagnostic: NetworkCallDiagnostic?,
    val mediaCallDiagnostic: MediaCallDiagnostic?,
) {

    fun isEmpty(): Boolean {
        return notificationIconId == 0 && notificationMessageId == 0
    }
}
