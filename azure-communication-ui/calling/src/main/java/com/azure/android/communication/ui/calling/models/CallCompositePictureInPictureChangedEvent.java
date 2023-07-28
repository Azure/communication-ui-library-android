// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

/**
 *
 */
public final class CallCompositePictureInPictureChangedEvent {
    private final boolean isInPictureInPicture;

    public CallCompositePictureInPictureChangedEvent(final boolean isInPictureInPicture) {
        this.isInPictureInPicture = isInPictureInPicture;
    }

    public boolean isInPictureInPicture() {
        return this.isInPictureInPicture;
    }
}
