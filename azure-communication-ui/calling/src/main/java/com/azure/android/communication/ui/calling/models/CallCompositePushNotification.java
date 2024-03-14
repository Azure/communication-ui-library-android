// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

import com.azure.android.communication.common.CommunicationIdentifier;

import java.util.Map;

/**
 * CallCompositePushNotification for forwarding calling push notifications to UI Library.
 */
public class CallCompositePushNotification {
    private final Map<String, String> notification;
    private final String fromDisplayName;
    private final CommunicationIdentifier from;
    private final boolean isIncomingWithVideo;
    private final String callId;
    private final CommunicationIdentifier to;
    private final String eventTypeResult;

    /**
     * Create {@link CallCompositePushNotification}.
     * @param notification Notification info map.
     */
    public CallCompositePushNotification(final Map<String, String> notification) {
        final com.azure.android.communication.calling.PushNotificationInfo pushNotificationInfo =
                com.azure.android.communication.calling.PushNotificationInfo.fromMap(notification);

        fromDisplayName = pushNotificationInfo.getFromDisplayName();

        from = pushNotificationInfo.getFrom();

        isIncomingWithVideo = pushNotificationInfo.isIncomingWithVideo();

        callId = pushNotificationInfo.getCallId().toString();

        to = pushNotificationInfo.getTo();

        eventTypeResult = pushNotificationInfo.getEventType().toString();

        this.notification = notification;
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
        return notification;
    }
}
