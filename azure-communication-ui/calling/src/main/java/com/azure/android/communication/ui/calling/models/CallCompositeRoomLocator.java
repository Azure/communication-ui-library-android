// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

/**
 * Room Call locator to start Room call experience using
 *  {@link com.azure.android.communication.ui.calling.CallComposite}.
 *
 */
public final class CallCompositeRoomLocator extends CallCompositeJoinLocator {
    private final String roomId;

    /**
     * Creates {@link CallCompositeRoomLocator}.
     * @param roomId   Room identifier.
     */
    public CallCompositeRoomLocator(final String roomId) {
        this.roomId = roomId;
    }
    /**
     * Get room id.
     *
     * @return {@link String}
     */
    public String getRoomId() {
        return roomId;
    }
}
