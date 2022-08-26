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
        val UNKNOWN_ERROR = fromString("unknownError")
        val NETWORK_NOT_AVAILABLE = fromString("networkNotAvailable")

        private fun fromString(name: String): ErrorCode {
            return fromString(name, ErrorCode::class.java)
        }
    }
}
