// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

/* <ROOMS_SUPPORT:0> */ 
/**
 * Room Call locator to start Room call experience using
 *  {@link com.azure.android.communication.ui.calling.CallComposite}.
 *
 *  You need to use LocalOptions parameter for
 *  CallComposite.launch() method with roleHint provided.
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
/* </ROOMS_SUPPORT:0> */
