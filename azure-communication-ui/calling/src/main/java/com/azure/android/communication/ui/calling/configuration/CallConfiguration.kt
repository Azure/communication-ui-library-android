// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.configuration
import com.azure.android.communication.calling.PushNotificationInfo
import com.azure.android.communication.common.CommunicationTokenCredential
import com.azure.android.communication.ui.calling.DiagnosticConfig
import com.azure.android.communication.ui.calling.models.CallCompositeParticipantRole
import java.util.UUID

internal enum class CallType {
    GROUP_CALL,
    TEAMS_MEETING,
    DIAL_CALL,
    ROOMS_CALL,
    INCOMING_CALL,
}

internal data class CallConfiguration(
    val communicationTokenCredential: CommunicationTokenCredential,
    val displayName: String,
    val groupId: UUID?,
    val meetingLink: String?,
    val roomId: String?,
    val roomRoleHint: CallCompositeParticipantRole?,
    val callType: CallType,
    val pushNotificationInfo: PushNotificationInfo?,
    val participantMri: String,
    val acceptIncomingCall: Boolean = false,
) {
    val diagnosticConfig = DiagnosticConfig()
}
