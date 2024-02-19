// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.service.sdk

import com.azure.android.communication.calling.MediaStreamType
import com.azure.android.communication.calling.RemoteVideoStream
import com.azure.android.communication.ui.calling.models.StreamType
import com.azure.android.communication.ui.calling.models.VideoStreamModel

internal object VideoStreamModelFactory {
    fun create(
        videoStreams: List<RemoteVideoStream>,
        type: MediaStreamType,
    ): VideoStreamModel? {
        val videoStream = videoStreams.firstOrNull { it.mediaStreamType == type }
        videoStream?.let {
            return VideoStreamModel(
                videoStream.id.toString(),
                getStreamType(type),
            )
        }
        return null
    }

    private fun getStreamType(mediaStreamTye: MediaStreamType) =
        when (mediaStreamTye) {
            MediaStreamType.SCREEN_SHARING -> StreamType.SCREEN_SHARING
            else -> StreamType.VIDEO
        }
}
