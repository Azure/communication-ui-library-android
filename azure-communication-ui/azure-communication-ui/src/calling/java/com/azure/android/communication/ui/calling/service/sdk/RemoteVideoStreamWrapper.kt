// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.service.sdk

import com.azure.android.communication.calling.MediaStreamType

internal class RemoteVideoStreamWrapper(
    override val native: com.azure.android.communication.calling.RemoteVideoStream
) : RemoteVideoStream {
    override val id: Int = native.id
    override val mediaStreamType: MediaStreamType = native.mediaStreamType
}
