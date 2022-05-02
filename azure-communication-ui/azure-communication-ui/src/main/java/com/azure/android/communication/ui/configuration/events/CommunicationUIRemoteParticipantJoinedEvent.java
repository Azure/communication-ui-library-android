// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.configuration.events;

import com.azure.android.communication.common.CommunicationIdentifier;

import java.util.List;

/**
 * Remote participants joined event with communication identifiers.
 */
public final class CommunicationUIRemoteParticipantJoinedEvent {
    private final List<CommunicationIdentifier> identifiers;

    /**
     * Create {@link CommunicationUIRemoteParticipantJoinedEvent} with user identifiers.
     *
     * @param identifiers {@link CommunicationIdentifier};
     */
    public CommunicationUIRemoteParticipantJoinedEvent(final List<CommunicationIdentifier> identifiers) {
        this.identifiers = identifiers;
    }

    /**
     * Returns the communication identifiers.
     *
     * @return The {@link List<CommunicationIdentifier>};
     */
    public List<CommunicationIdentifier> getIdentifiers() {
        return identifiers;
    }
}
