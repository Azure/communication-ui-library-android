// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

import java.util.Map;

/**
 * CallCompositePushNotificationInfo for forwarding calling push notifications to UI.
 */
public class CallCompositePushNotificationInfo {
    private final Map<String, String> notificationInfo;

    /**
     * Create {@link CallCompositePushNotificationInfo}.
     * @param notificationInfo Notification info map.
     */
    public CallCompositePushNotificationInfo(final Map<String, String> notificationInfo) {
        this.notificationInfo = notificationInfo;
    }

    /**
     * Get notification info map.
     * @return {@link Map}.
     */
    public Map<String, String> getNotificationInfo() {
        return notificationInfo;
    }

    /**
     * Create {@link CallCompositePushNotificationInfo} from map.
     * @param map Notification info map.
     * @return {@link CallCompositePushNotificationInfo}.
     */
    public static CallCompositePushNotificationInfo fromMap(final Map<String, String> map) {
        return new CallCompositePushNotificationInfo(map);
    }
}
