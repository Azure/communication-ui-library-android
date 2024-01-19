// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

import com.azure.android.core.util.ExpandableStringEnum;
import java.util.Collection;

/**
 * CallCompositePushNotificationEventType for forwarding calling push notifications to UI.
 */
final class CallCompositePushNotificationEventType
        extends ExpandableStringEnum<CallCompositePushNotificationEventType> {

    /**
     * CallCompositePushNotificationEventType for incoming call.
     */
    static final CallCompositePushNotificationEventType INCOMING_CALL = fromString("INCOMING_CALL");

    /**
     * CallCompositePushNotificationEventType for incoming group call.
     */
    static final CallCompositePushNotificationEventType INCOMING_GROUP_CALL = fromString("INCOMING_GROUP_CALL");

    /**
     * CallCompositePushNotificationEventType for incoming PSTN call.
     */
    static final CallCompositePushNotificationEventType INCOMING_PSTN_CALL = fromString("INCOMING_PSTN_CALL");

    /**
     * CallCompositePushNotificationEventType for stop ringing.
     */
    static final CallCompositePushNotificationEventType STOP_RINGING = fromString("STOP_RINGING");

    /**
     * Creates or finds a {@link CallCompositePushNotificationEventType} from its string representation.
     *
     * @param name a name to look for.
     * @return the corresponding CallCompositePushNotificationEventType.
     */
    static CallCompositePushNotificationEventType fromString(final String name) {
        return fromString(name, CallCompositePushNotificationEventType.class);
    }

    /**
     * @return known {@link CallCompositePushNotificationEventType} values.
     */
    static Collection<CallCompositePushNotificationEventType> values() {
        return values(CallCompositePushNotificationEventType.class);
    }
}

