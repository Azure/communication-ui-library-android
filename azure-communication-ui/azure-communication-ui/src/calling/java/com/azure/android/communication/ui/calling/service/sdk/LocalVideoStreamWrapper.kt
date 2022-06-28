// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.service.sdk

import java9.util.concurrent.CompletableFuture

internal class LocalVideoStreamWrapper(
    override val native: com.azure.android.communication.calling.LocalVideoStream,
) : LocalVideoStream {
    override fun switchSource(deviceInfo: VideoDeviceInfo): CompletableFuture<Void> {
        return native.switchSource(deviceInfo.native as com.azure.android.communication.calling.VideoDeviceInfo)
    }

    override val source: VideoDeviceInfo = native.source.into()
}
