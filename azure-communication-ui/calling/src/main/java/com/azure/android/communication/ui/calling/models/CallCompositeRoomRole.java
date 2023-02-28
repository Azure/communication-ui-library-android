// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

import com.azure.android.core.util.ExpandableStringEnum;

public final class CallCompositeRoomRole extends ExpandableStringEnum<CallCompositeRoomRole> {
    /**
     * Presenter Role in the Room call.
     */
    public static final CallCompositeRoomRole PRESENTER = fromString("Presenter");

    /**
     * Attendee Role in the Room call.
     */
    public static final CallCompositeRoomRole ATTENDEE = fromString("Attendee");

    private static CallCompositeRoomRole fromString(final String name) {
        return fromString(name, CallCompositeRoomRole.class);
    }
}
