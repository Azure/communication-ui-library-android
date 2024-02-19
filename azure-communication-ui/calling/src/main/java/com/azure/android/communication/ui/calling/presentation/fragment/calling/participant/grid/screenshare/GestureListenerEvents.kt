// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.calling.participant.grid.screenshare

import android.view.MotionEvent

internal interface GestureListenerEvents {
    fun onSingleClick()

    fun onDoubleClick(motionEvent: MotionEvent)

    fun initTransformation()

    fun updateTransformation()
}
