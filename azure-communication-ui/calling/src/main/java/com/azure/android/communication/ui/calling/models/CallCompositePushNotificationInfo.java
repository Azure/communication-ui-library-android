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
public class CallCompositePushNotificationInfo {
    private final Map<String, String> notificationInfoMap;
    private final String fromDisplayName;
    private final CommunicationIdentifier from;
    private final boolean isIncomingWithVideo;
    private final String callId;
    private final CommunicationIdentifier to;
    private final String eventTypeResult;

    /**
     * Create {@link CallCompositePushNotificationInfo}.
     * @param notificationInfoMap Notification info map.
     */
    public CallCompositePushNotificationInfo(final Map<String, String> notificationInfoMap)
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
                .invoke(pushNotificationInfo));

        isIncomingWithVideo = (boolean) pushNotificationInfoClass
                .getMethod("isIncomingWithVideo")
                .invoke(pushNotificationInfo);

        callId = ((UUID) pushNotificationInfoClass
                .getMethod("getCallId")
                .invoke(pushNotificationInfo)).toString();

        to = ((CommunicationIdentifier) pushNotificationInfoClass
                .getMethod("getTo")
                .invoke(pushNotificationInfo));

        eventTypeResult = pushNotificationInfoClass
                .getMethod("getEventType")
                .invoke(pushNotificationInfo).toString();

        this.notificationInfoMap = notificationInfoMap;
    }

    /**
     * Get from.
     * @return CommunicationIdentifier.
     */
    public CommunicationIdentifier getFrom() {
        return from;
    }

    /**
     * Get to.
     * @return CommunicationIdentifier.
     */
    public CommunicationIdentifier getTo() {
        return to;
    }

    /**
     * Get from display name.
     * @return from display name.
     */
    public String getFromDisplayName() {
        return fromDisplayName;
    }

    /**
     * Is Incoming with Video.
     * @return is incoming with video.
     */
    public boolean isIncomingWithVideo() {
        return isIncomingWithVideo;
    }


    /**
     * Get call id.
     * @return call id.
     */
    public String getCallId() {
        return callId;
    }

    /**
     * Get push notification event type.
     * @return {@link CallCompositePushNotificationEventType}.
     */
    public CallCompositePushNotificationEventType getEventType() {
        return CallCompositePushNotificationEventType.fromString(eventTypeResult);
    }

    /**
     * Get PushNotificationInfo.
     * @return PushNotificationInfo.
     */
    public Map<String, String> getNotificationInfo() {
        return notificationInfoMap;
    }
}
