// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.model

internal enum class StreamType {
    VIDEO,
    SCREEN_SHARING,
}

internal data class VideoStreamModel(val videoStreamID: String, val streamType: StreamType)
