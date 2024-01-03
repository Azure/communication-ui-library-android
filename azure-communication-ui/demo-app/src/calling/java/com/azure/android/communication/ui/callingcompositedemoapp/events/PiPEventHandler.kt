// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callingcompositedemoapp.events

import com.azure.android.communication.ui.calling.CallCompositeEventHandler
import com.azure.android.communication.ui.calling.models.CallCompositePictureInPictureChangedEvent

class PiPEventHandler : CallCompositeEventHandler<CallCompositePictureInPictureChangedEvent> {
    override fun handle(event: CallCompositePictureInPictureChangedEvent) {
        println("addOnMultitaskingStateChangedEventHandler it.isInPictureInPicture: ")
    }
}
