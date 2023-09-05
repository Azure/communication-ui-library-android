// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

import com.azure.android.communication.calling.PushNotificationInfo;
import com.azure.android.communication.ui.calling.CallComposite;

/**
 * Incoming call locator {@link CallComposite}.
 */
public final class CallCompositeIncomingCallLocator extends CallCompositeJoinLocator {

    private final PushNotificationInfo pushNotificationInfo;
    private final Boolean acceptIncomingCall;

    /**
     * Creates {@link CallCompositeIncomingCallLocator}.
     *
     * @param pushNotificationInfo ParticipantMri, for more information please check Quickstart Doc.
     * @param acceptIncomingCall
     */
    public CallCompositeIncomingCallLocator(final PushNotificationInfo pushNotificationInfo,
                                            final Boolean acceptIncomingCall) {
        this.pushNotificationInfo = pushNotificationInfo;
        this.acceptIncomingCall = acceptIncomingCall;
    }

    /**
     * Get Teams meeting link.
     *
     * @return {@link String}.
     */
    public PushNotificationInfo getPushNotificationInfo() {
        return pushNotificationInfo;
    }

    /**
     * Get accept incoming call.
     *
     * @return {@link Boolean}.
     */
    public Boolean getAcceptIncomingCall() {
        return acceptIncomingCall;
    }
}

