// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;


import java.util.Map;

public class CallCompositePushNotificationInfo {
    public Map<String, String> notificationInfo;

    public CallCompositePushNotificationInfo(Map<String, String> notificationInfo) {
        this.notificationInfo = notificationInfo;
    }

    public Map<String, String> getNotificationInfo() {
        return notificationInfo;
    }

    public static CallCompositePushNotificationInfo fromMap(Map<String, String> map) {
        return new CallCompositePushNotificationInfo(map);
    }
}
