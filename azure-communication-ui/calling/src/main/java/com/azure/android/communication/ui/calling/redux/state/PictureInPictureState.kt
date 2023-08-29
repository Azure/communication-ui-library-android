// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.redux.state

// TODO: rename to VisibilityStatus
internal enum class PictureInPictureStatus {
    VISIBLE,
    HIDE_REQUESTED,
    HIDDEN,
    PIP_MODE_ENTERED,
}

// TODO: rename to VisibilityState
internal data class PictureInPictureState(val status: PictureInPictureStatus)
