// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
package com.azure.android.communication.ui.calling.error

import com.azure.android.communication.ui.calling.models.CallCompositeErrorCode
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
        val CAPTIONS_NOT_ACTIVE = fromString("captionsNotActive")
        val CALL_NOT_CONNECTED = fromString("callNotConnected")
        val CAPTIONS_START_FAILED_SPOKEN_LANGUAGE_NOT_SUPPORTED = fromString("captionsStartFailedSpokenLanguageNotSupported")

        private fun fromString(name: String): ErrorCode {
            return fromString(name, ErrorCode::class.java)
        }
    }

    internal fun toCallCompositeErrorCode(): CallCompositeErrorCode? {
        this.let {
            when (it) {
                ErrorCode.TOKEN_EXPIRED -> {
                    return CallCompositeErrorCode.TOKEN_EXPIRED
                }
                ErrorCode.CALL_JOIN_FAILED, ErrorCode.NETWORK_NOT_AVAILABLE -> {
                    return CallCompositeErrorCode.CALL_JOIN_FAILED
                }
                ErrorCode.CALL_END_FAILED -> {
                    return CallCompositeErrorCode.CALL_END_FAILED
                }
                ErrorCode.SWITCH_CAMERA_FAILED, ErrorCode.TURN_CAMERA_ON_FAILED, ErrorCode.TURN_CAMERA_OFF_FAILED, ErrorCode.CAMERA_INIT_FAILED -> {
                    return CallCompositeErrorCode.CAMERA_FAILURE
                }
                ErrorCode.MIC_PERMISSION_DENIED -> {
                    return CallCompositeErrorCode.MICROPHONE_PERMISSION_NOT_GRANTED
                }
                ErrorCode.INTERNET_NOT_AVAILABLE -> {
                    return CallCompositeErrorCode.NETWORK_CONNECTION_NOT_AVAILABLE
                }
                MICROPHONE_NOT_AVAILABLE -> {
                    return CallCompositeErrorCode.MICROPHONE_NOT_AVAILABLE
                }
                CALL_NOT_CONNECTED -> {
                    return CallCompositeErrorCode.CALL_NOT_CONNECTED
                }
                CAPTIONS_NOT_ACTIVE -> {
                    return CallCompositeErrorCode.CAPTIONS_NOT_ACTIVE
                }
                CAPTIONS_START_FAILED_SPOKEN_LANGUAGE_NOT_SUPPORTED -> {
                    return CallCompositeErrorCode.CAPTIONS_START_FAILED_SPOKEN_LANGUAGE_NOT_SUPPORTED
                }
                else -> {
                    return null
                }
            }
        }
    }
}
