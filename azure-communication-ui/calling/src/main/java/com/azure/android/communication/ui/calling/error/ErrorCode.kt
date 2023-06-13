// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
package com.azure.android.communication.ui.calling.error

import com.azure.android.core.util.ExpandableStringEnum

internal class ErrorCode : ExpandableStringEnum<ErrorCode?>() {
    companion object {
        val CALL_JOIN_FAILED = fromString("callJoinFailed")
        val CALL_END_FAILED = fromString("callEndFailed")
        val TOKEN_EXPIRED = fromString("tokenExpired")
        val SWITCH_CAMERA_FAILED = fromString("switchCameraFailed")
        val TURN_CAMERA_ON_FAILED = fromString("turnCameraOnFailed")
        val TURN_CAMERA_OFF_FAILED = fromString("turnCameraOffFailed")
        val TURN_MIC_ON_FAILED = fromString("turnMicOnFailed")
        val TURN_MIC_OFF_FAILED = fromString("turnMicOffFailed")
        val NETWORK_NOT_AVAILABLE = fromString("networkNotAvailable")
        val CAMERA_INIT_FAILED = fromString("cameraInitiationFailure")
        val MIC_PERMISSION_DENIED = fromString("micPermissionDenied")
        val INTERNET_NOT_AVAILABLE = fromString("internetNotAvailable")
        val MICROPHONE_NOT_AVAILABLE = fromString("microphoneNotAvailable")

        private fun fromString(name: String): ErrorCode {
            return fromString(name, ErrorCode::class.java)
        }
    }
}
