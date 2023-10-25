// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.handlers

import com.azure.android.communication.calling.PushNotificationInfo
import com.azure.android.communication.ui.calling.models.CallCompositePushNotificationInfo
import com.azure.android.communication.ui.calling.models.CallCompositePushNotificationOptions
import com.azure.android.communication.ui.calling.service.sdk.CallingSDK

internal class PushNotificationHandler(private val callingSDK: CallingSDK) {

    fun registerPushNotificationAsync(pushNotificationOptions: CallCompositePushNotificationOptions) {
        callingSDK.registerPushNotificationTokenAsync(pushNotificationOptions.deviceRegistrationToken)
    }

    fun handlePushNotificationAsync(pushNotificationInfo: CallCompositePushNotificationInfo) {
        callingSDK.handlePushNotificationAsync(PushNotificationInfo.fromMap(pushNotificationInfo.getNotificationInfo()))
    }
}