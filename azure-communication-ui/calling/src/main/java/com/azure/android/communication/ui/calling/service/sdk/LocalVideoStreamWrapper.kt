// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.service.sdk

import com.azure.android.communication.ui.calling.utilities.toJavaUtil
import java.util.concurrent.CompletableFuture
import com.azure.android.communication.calling.LocalVideoStream as NativeLocalVideoStream

internal class LocalVideoStreamWrapper(override val native: NativeLocalVideoStream) : LocalVideoStream {
    override val source: VideoDeviceInfo
        get() { return native.source.into() }

    override fun switchSource(deviceInfo: VideoDeviceInfo): CompletableFuture<Void> {
        return native.switchSource(deviceInfo.native as com.azure.android.communication.calling.VideoDeviceInfo)
            .toJavaUtil()
    }
}
