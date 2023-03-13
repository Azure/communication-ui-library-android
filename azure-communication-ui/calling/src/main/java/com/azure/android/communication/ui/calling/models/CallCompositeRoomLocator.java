// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

import com.azure.android.communication.ui.calling.CallComposite;

/**
 * Room Call locator to start Room call experience using {@link CallComposite}.
 */
public final class CallCompositeRoomLocator extends CallCompositeJoinLocator {

    private final String roomId;
    private final CallCompositeParticipantRole hintRole;

    /**
     * Creates {@link CallCompositeRoomLocator}.
     * @param roomId   Room identifier.
     * @param hintRole Use this to hint the role of the user when the role is not available before a Rooms
     *                 call is started.
     *                 This value should be obtained using the Rooms API. This role will determine permissions in the
     *                 Setup screen of the {@link CallComposite}.
     *                 The true role of the user will be synced with ACS services when a Rooms call starts.
     */
    public CallCompositeRoomLocator(final String roomId, final CallCompositeParticipantRole hintRole) {
        this.roomId = roomId;
        this.hintRole = hintRole;
    }

    /**
     * Get room id.
     *
     * @return {@link String}
     */
    public String getRoomId() {
        return roomId;
    }

    /**
     * Get hint role. Use this to hint the role of the user when the role is not available before a Rooms
     * call is started.
     * This value should be obtained using the Rooms API. This role will determine permissions in the
     * Setup screen of the {@link CallComposite}.
     * The true role of the user will be synced with ACS services when a Rooms call starts.
     * @return {@link CallCompositeParticipantRole}
     */
    public CallCompositeParticipantRole getHintRole() {
        return hintRole;
    }
}
