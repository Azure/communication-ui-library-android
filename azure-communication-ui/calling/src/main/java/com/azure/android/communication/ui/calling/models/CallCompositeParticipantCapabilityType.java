// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

import com.azure.android.core.util.ExpandableStringEnum;

import java.util.Collection;

/**
 *  Available capabilities.
 */
public class CallCompositeParticipantCapabilityType
        extends ExpandableStringEnum<CallCompositeParticipantCapabilityType> {

    /**
     * Turn Video On.
     */
    public static final CallCompositeParticipantCapabilityType TURN_VIDEO_ON =
            fromString("TURN_VIDEO_ON");

    /**
     * Unmute microphone.
     */
    public static final CallCompositeParticipantCapabilityType UNMUTE_MICROPHONE =
            fromString("UNMUTE_MICROPHONE");

    /**
     * Remove Participant
     */
    public static final CallCompositeParticipantCapabilityType REMOVE_PARTICIPANT =
            fromString("REMOVE_PARTICIPANT");

    /**
     * manage lobby
     */
    public static final CallCompositeParticipantCapabilityType MANAGE_LOBBY =
            fromString("MANAGE_LOBBY");

    /**
     * Creates or finds a {@link CallCompositeParticipantCapabilityType} from its string representation.
     *
     * @param name a name to look for.
     * @return the corresponding {@link CallCompositeParticipantCapabilityType}.
     */
    public static CallCompositeParticipantCapabilityType fromString(final String name) {
        return fromString(name, CallCompositeParticipantCapabilityType.class);
    }

    /**
     * @return known {@link CallCompositeParticipantCapabilityType} values.
     */
    public static Collection<CallCompositeParticipantCapabilityType> values() {
        return values(CallCompositeParticipantCapabilityType.class);
    }
}
