// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.configuration.events;

import com.azure.android.communication.common.CommunicationIdentifier;

/**
 * Remote participant joined event with communication identifier.
 */
public final class RemoteParticipantJoinedEvent {
    private final CommunicationIdentifier communicationIdentifier;

    /**
     * Create{@linkRemoteParticipantJoinedEvent} with user identifier.
     *
     * @param communicationIdentifier {@link CommunicationIdentifier};
     */
    public RemoteParticipantJoinedEvent(final CommunicationIdentifier communicationIdentifier) {
        this.communicationIdentifier = communicationIdentifier;
    }

    /**
     * Returns the communication identifier.
     *
     * @return The {@link CommunicationIdentifier};
     */
    public CommunicationIdentifier getCommunicationIdentifier() {
        return communicationIdentifier;
    }
}
