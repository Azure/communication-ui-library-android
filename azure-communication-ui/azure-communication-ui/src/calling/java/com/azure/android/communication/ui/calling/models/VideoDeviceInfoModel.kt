// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models

internal enum class CameraFacing {
    UNKNOWN,
    EXTERNAL,
    FRONT,
    BACK,
    PANORAMIC,
    LEFT_FRONT,
    RIGHT_FRONT,
}

internal enum class VideoDeviceType {
    UNKNOWN,
    USB_CAMERA,
    CAPTURE_ADAPTER,
    VIRTUAL,
}

internal data class VideoDeviceInfoModel(
    val name: String,
    val id: String,
    val cameraFacing: CameraFacing,
    val videoDeviceType: VideoDeviceType,
)
