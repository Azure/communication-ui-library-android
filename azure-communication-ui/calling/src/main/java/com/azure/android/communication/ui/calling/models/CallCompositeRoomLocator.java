// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

/**
 * Room Call locator to start Room call experience using
 *  {@link com.azure.android.communication.ui.calling.CallComposite}.
 *
 *  You need to use LocalOptions parameter for
 *  CallComposite.launch() method with roleHint provided.
 */
/* <ROOMS_SUPPORT:7> */
public final class CallCompositeRoomLocator extends CallCompositeJoinLocator {

    private final String roomId;

    /* </ROOMS_SUPPORT:0> */
    /**
     * Creates {@link CallCompositeRoomLocator}.
     * @param roomId   Room identifier.
     */
    /* <ROOMS_SUPPORT:4> */
    public CallCompositeRoomLocator(final String roomId) {
        this.roomId = roomId;
    }
    /* </ROOMS_SUPPORT:0> */
    /**
     * Get room id.
     *
     * @return {@link String}
     */
    /* <ROOMS_SUPPORT:5> */
    public String getRoomId() {
        return roomId;
    }
}
/* </ROOMS_SUPPORT:0> */
