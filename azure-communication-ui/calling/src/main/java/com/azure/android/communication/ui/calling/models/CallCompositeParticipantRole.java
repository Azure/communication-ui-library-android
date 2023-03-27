// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

import com.azure.android.core.util.ExpandableStringEnum;

/**
 * Defines values for CallCompositeParticipantRole.
 */
public final class CallCompositeParticipantRole extends ExpandableStringEnum<CallCompositeParticipantRole> {
    /**
     * Presenter Role in the Room call.
     */
    public static final CallCompositeParticipantRole PRESENTER = fromString("Presenter");

    /**
     * Attendee Role in the Room call.
     */
    public static final CallCompositeParticipantRole ATTENDEE = fromString("Attendee");

    /**
     * Creates instance of CallCompositeParticipantRole.
     */
    public CallCompositeParticipantRole() { }

    private static CallCompositeParticipantRole fromString(final String name) {
        return fromString(name, CallCompositeParticipantRole.class);
    }
}
