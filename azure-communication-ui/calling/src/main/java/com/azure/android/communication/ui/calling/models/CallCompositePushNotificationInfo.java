// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

import com.azure.android.communication.calling.PushNotificationInfo;

import java.util.Map;

/**
 * CallCompositePushNotificationInfo for forwarding calling push notifications to UI.
 */
public class CallCompositePushNotificationInfo {
    private final PushNotificationInfo notificationInfo;

    /**
     * Create {@link CallCompositePushNotificationInfo}.
     * @param notificationInfoMap Notification info map.
     */
    public CallCompositePushNotificationInfo(final Map<String, String> notificationInfoMap) {
        notificationInfo = PushNotificationInfo.fromMap(notificationInfoMap);
    }

    /**
     * Get call id.
     * @return call id.
     */
    public String getCallId() {
        return notificationInfo.getCallId().toString();
    }

    /**
     * Get event type.
     * @return event type.
     */
    public String getEventType() {
        return notificationInfo.getEventType().toString();
    }

    /**
     * Get from display name.
     * @return from display name.
     */
    public String getFromDisplayName() {
        return notificationInfo.getFromDisplayName();
    }
}
