// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models

internal enum class NetworkCallDiagnostic {
    NETWORK_RECONNECTION_QUALITY,
    NETWORK_RECEIVE_QUALITY,
    NETWORK_SEND_QUALITY,
    NETWORK_UNAVAILABLE,
    NETWORK_RELAYS_UNREACHABLE
}

internal enum class MediaCallDiagnostic {
    SPEAKER_NOT_FUNCTIONING,
    SPEAKER_BUSY,
    SPEAKER_MUTED,
    SPEAKER_VOLUME_ZERO,
    NO_SPEAKER_DEVICES_AVAILABLE,
    SPEAKING_WHILE_MICROPHONE_IS_MUTED,
    MICROPHONE_NOT_FUNCTIONING,
    MICROPHONE_BUSY,
    MICROPHONE_MUTED_UNEXPECTEDLY,
    NO_MICROPHONE_DEVICES_AVAILABLE,
    CAMERA_FROZEN,
    CAMERA_START_FAILED,
    CAMERA_START_TIMED_OUT,
    CAMERA_PERMISSION_DENIED
}

internal enum class CallDiagnosticQuality {
    UNKNOWN,
    GOOD,
    POOR,
    BAD;
}

internal class CallDiagnosticModel<DiagnosticKind, DiagnosticValue>(
    var diagnosticKind: DiagnosticKind,
    var diagnosticValue: DiagnosticValue
)

// Alias types representing different types of Call Diagnostics
internal typealias NetworkQualityCallDiagnosticModel = CallDiagnosticModel<NetworkCallDiagnostic, CallDiagnosticQuality>
internal typealias NetworkCallDiagnosticModel = CallDiagnosticModel<NetworkCallDiagnostic, Boolean>
internal typealias MediaCallDiagnosticModel = CallDiagnosticModel<MediaCallDiagnostic, Boolean>
