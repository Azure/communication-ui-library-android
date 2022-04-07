// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.configuration.events;

import com.azure.android.communication.common.CommunicationUserIdentifier;

/**
 * Remote participant joined event with user identifier.
 */
public final class RemoteParticipantJoinedEvent {
    private final CommunicationUserIdentifier communicationUserIdentifier;

    /**
     * Create{@linkRemoteParticipantJoinedEvent} with user identifier.
     *
     * @param communicationUserIdentifier {@link CommunicationUserIdentifier};
     */
    public RemoteParticipantJoinedEvent(final CommunicationUserIdentifier communicationUserIdentifier) {
        this.communicationUserIdentifier = communicationUserIdentifier;
    }

    /**
     * Returns the communication user identifier.
     *
     * @return The {@link CommunicationUserIdentifier};
     */
    public CommunicationUserIdentifier getCommunicationUserIdentifier() {
        return communicationUserIdentifier;
    }
}
