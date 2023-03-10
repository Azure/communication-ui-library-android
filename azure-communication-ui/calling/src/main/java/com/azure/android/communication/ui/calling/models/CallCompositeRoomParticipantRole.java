// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

import com.azure.android.core.util.ExpandableStringEnum;

public final class CallCompositeRoomParticipantRole extends ExpandableStringEnum<CallCompositeRoomParticipantRole> {
    /**
     * Presenter Role in the Room call.
     */
    public static final CallCompositeRoomParticipantRole PRESENTER = fromString("Presenter");

    /**
     * Attendee Role in the Room call.
     */
    public static final CallCompositeRoomParticipantRole ATTENDEE = fromString("Attendee");

    private static CallCompositeRoomParticipantRole fromString(final String name) {
        return fromString(name, CallCompositeRoomParticipantRole.class);
    }
}
