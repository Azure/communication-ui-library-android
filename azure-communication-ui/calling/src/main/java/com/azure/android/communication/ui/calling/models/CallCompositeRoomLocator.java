// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

import com.azure.android.communication.ui.calling.CallComposite;

/**
 * Room Call locator to start Room call experience using {@link CallComposite}.
 */
public final class CallCompositeRoomLocator extends CallCompositeJoinLocator {

    private final String roomId;
    private final CallCompositeRoomRole role;

    /**
     * Creates {@link CallCompositeRoomLocator}.
     * @param roomId   Room identifier.
     */
    public CallCompositeRoomLocator(final String roomId, final CallCompositeRoomRole role) {
        this.roomId = roomId;
        this.role = role;
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
     * Get room role.
     *
     * @return {@link CallCompositeRoomRole}
     */
    public CallCompositeRoomRole getRole() {
        return role;
    }
}
