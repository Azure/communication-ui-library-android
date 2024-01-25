// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

import com.azure.android.communication.common.CommunicationIdentifier;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.UUID;

/**
 * CallCompositePushNotificationInfo for forwarding calling push notifications to UI Library.
 */
class CallCompositePushNotificationInfo {
    private final Map<String, String> notificationInfoMap;
    private final String fromDisplayName;
    private final String from;
    private final boolean isIncomingWithVideo;
    private final String callId;
    private final String to;
    private final String eventTypeResult;

    /**
     * Create {@link CallCompositePushNotificationInfo}.
     * @param notificationInfoMap Notification info map.
     */
    CallCompositePushNotificationInfo(final Map<String, String> notificationInfoMap)
            throws ClassNotFoundException, NoSuchMethodException,
            InvocationTargetException, IllegalAccessException, InstantiationException {

        final Class<?> pushNotificationInfoClass =
                Class.forName("com.azure.android.communication.calling.PushNotificationInfo");

        final Object pushNotificationInfo = pushNotificationInfoClass
                .getMethod("fromMap", Map.class)
                .invoke(null, notificationInfoMap);

        fromDisplayName = (String) pushNotificationInfoClass
                .getMethod("getFromDisplayName")
                .invoke(pushNotificationInfo);

        from = ((CommunicationIdentifier) pushNotificationInfoClass
                .getMethod("getFrom")
                .invoke(pushNotificationInfo)).getRawId();

        isIncomingWithVideo = (boolean) pushNotificationInfoClass
                .getMethod("isIncomingWithVideo")
                .invoke(pushNotificationInfo);

        callId = ((UUID) pushNotificationInfoClass
                .getMethod("getCallId")
                .invoke(pushNotificationInfo)).toString();

        to = ((CommunicationIdentifier) pushNotificationInfoClass
                .getMethod("getTo")
                .invoke(pushNotificationInfo)).getRawId();

        eventTypeResult = pushNotificationInfoClass
                .getMethod("getEventType")
                .invoke(pushNotificationInfo).toString();

        this.notificationInfoMap = notificationInfoMap;
    }

    /**
     * Get from.
     * @return caller token.
     */
    String getFrom() {
        return from;
    }

    /**
     * Get to.
     * @return return Callee raw identifier.
     */
    String getTo() {
        return to;
    }

    /**
     * Get from display name.
     * @return from display name.
     */
    String getFromDisplayName() {
        return fromDisplayName;
    }

    /**
     * Is Incoming with Video.
     * @return is incoming with video.
     */
    boolean isIncomingWithVideo() {
        return isIncomingWithVideo;
    }


    /**
     * Get call id.
     * @return call id.
     */
    String getCallId() {
        return callId;
    }

    /**
     * Get push notification event type.
     * @return {@link CallCompositePushNotificationEventType}.
     */
    CallCompositePushNotificationEventType getEventType() {
        return CallCompositePushNotificationEventType.fromString(eventTypeResult);
    }

    /**
     * Get PushNotificationInfo.
     * @return PushNotificationInfo.
     */
    Map<String, String> getNotificationInfo() {
        return notificationInfoMap;
    }
}
