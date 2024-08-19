// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

import com.azure.android.communication.common.CommunicationIdentifier;

import java.util.Collection;
import java.util.Collections;

/**
 * Remote participants removed event with communication identifiers.
 */
public final class CallCompositeRemoteParticipantLeftEvent {
    private final Collection<CommunicationIdentifier> identifiers;

    /**
     * Create {@link CallCompositeRemoteParticipantLeftEvent} with user identifiers.
     *
     * @param identifiers {@link CommunicationIdentifier}.
     */
    CallCompositeRemoteParticipantLeftEvent(final Collection<CommunicationIdentifier> identifiers) {
        this.identifiers = identifiers;
    }

    /**
     * Returns the communication identifiers.
     *
     * @return The collection of {@link CommunicationIdentifier}.
     */
    public Collection<CommunicationIdentifier> getIdentifiers() {
        return Collections.unmodifiableCollection(identifiers);
    }
}
