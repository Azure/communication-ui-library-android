// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.redux.state

internal enum class PictureInPictureStatus {
    NONE,
    PIP_MODE_ENTERED
}

internal data class PictureInPictureState(val status: PictureInPictureStatus)
